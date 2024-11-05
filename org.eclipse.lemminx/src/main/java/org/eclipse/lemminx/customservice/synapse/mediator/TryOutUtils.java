/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.eclipse.lemminx.customservice.synapse.mediator;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.debuginfo.IDebugInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Edit;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TryOutUtils {

    private TryOutUtils() {

    }

    /**
     * This method is used to get the offset of the given position in the content.
     *
     * @param content
     * @param position
     * @return
     */
    public static int getOffset(String content, Position position) {

        String[] lines = content.split("\n", -1);
        int offset = 0;
        for (int i = 0; i < position.getLine(); i++) {
            offset += lines[i].length() + 1;
        }
        return offset + position.getCharacter();
    }

    /**
     * This method is used to check whether the actual breakpoint is the expected breakpoint.
     *
     * @param actual
     * @param expected
     * @return
     */
    public static boolean isExpectedBreakpoint(JsonObject actual, IDebugInfo expected) {

        JsonElement actualSequenceData = actual.get(Constant.SEQUENCE);

        JsonObject breakpointJson = expected.toJson().getAsJsonObject();
        JsonElement expectedSequenceData = breakpointJson.get(Constant.SEQUENCE);

        return expectedSequenceData != null
                && expectedSequenceData.equals(actualSequenceData);
    }

    /**
     * Relativizes a target path against a given source path and then resolves it
     * against another source path to produce a new path.
     * <p>
     * This method first computes the relative path from {@code relativizeSourcePath}
     * to {@code relativizeTargetPath}. It then resolves this relative path
     * against {@code resolveSourcePath}.
     *
     * @param relativizeSourcePath the base path against which {@code relativizeTargetPath} will be relativized
     * @param relativizeTargetPath the target path to be relativized against {@code relativizeSourcePath}
     * @param resolveSourcePath    the path against which the computed relative path will be resolved
     * @return the resulting path obtained by resolving the relative path between
     * {@code relativizeSourcePath} and {@code relativizeTargetPath} against {@code resolveSourcePath}
     * @throws IllegalArgumentException if {@code relativizeSourcePath} and {@code relativizeTargetPath} are not of the same type (both relative or both absolute)
     */
    public static Path relativizeAndResolvePath(Path relativizeSourcePath, Path relativizeTargetPath,
                                                Path resolveSourcePath) {

        Path relativeFilePath = relativizeSourcePath.relativize(relativizeTargetPath);
        return resolveSourcePath.resolve(relativeFilePath);
    }

    /**
     * This method is used to clone and preprocess the project.
     *
     * @param projectUri the project URI
     * @param tryoutFile the file in which the mediator is getting tried out
     * @param edits      the edits to be applied
     * @param tempFolder the temporary folder to clone the project
     * @return
     * @throws IOException
     */
    public static Path cloneAndPreprocessProject(String projectUri, String tryoutFile, Edit[] edits, Path tempFolder)
            throws IOException {

        Path projectPath = Path.of(projectUri);
        Utils.copyFolder(projectPath, tempFolder, null);

        // Apply the edits from user
        Path editFilePath = TryOutUtils.relativizeAndResolvePath(projectPath, Path.of(tryoutFile),
                tempFolder);
        doEdits(edits, editFilePath);
        return editFilePath;
    }

    /**
     * This method is used to apply the edits to the file.
     *
     * @param edits        the edits to be applied
     * @param editFilePath the file in which the edits are applied
     * @throws IOException
     */
    public static void doEdits(Edit[] edits, Path editFilePath) throws IOException {

        if (edits != null && edits.length > 0) {
            for (Edit edit : edits) {
                doEdit(edit, editFilePath);
            }
        }
    }

    /**
     * This method is used to apply the edit to the file.
     *
     * @param edit         the edit to be applied
     * @param editFilePath the file in which the edit is applied
     * @throws IOException
     */
    public static void doEdit(Edit edit, Path editFilePath) throws IOException {

        String editContent = edit.getText();
        Range editRange = edit.getRange();

        String fileContent = Files.readString(editFilePath);

        int startOffset = TryOutUtils.getOffset(fileContent, editRange.getStart());
        int endOffset = TryOutUtils.getOffset(fileContent, editRange.getEnd());

        String newContent = fileContent.substring(0, startOffset) +
                editContent + "\n" +
                fileContent.substring(endOffset);

        Files.writeString(editFilePath, newContent);
    }
}
