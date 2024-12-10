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

package org.eclipse.lemminx.customservice.synapse.expression;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.ExpressionParam;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.Functions;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.HelperPanelData;
import org.eclipse.lemminx.customservice.synapse.expression.pojo.HelperPanelItem;
import org.eclipse.lemminx.customservice.synapse.mediator.schema.generate.ServerLessTryoutHandler;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutInfo;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.MediatorTryoutRequest;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Params;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Properties;
import org.eclipse.lemminx.customservice.synapse.mediator.tryout.pojo.Property;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lsp4j.CompletionItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionHelperProvider {

    private static final Map<Properties.Type, String> PROPERTIES_SECOND_LEVEL = Map.of(
            Properties.Type.SYNAPSE, "attributes.synapse",
            Properties.Type.AXIS2, "attributes.axis2",
            Properties.Type.AXIS2_CLIENT, "attributes.axis2Client",
            Properties.Type.AXIS2_TRANSPORT, "attributes.axis2Transport",
            Properties.Type.AXIS2_OPERATION, "attributes.axis2Operation"
                                                                                      );
    private static final Map<Params.Type, String> PARAMS_SECOND_LEVEL = Map.of(
            Params.Type.QUERY, "params.queryParams",
            Params.Type.URI, "params.uriParams",
            Params.Type.FUNC, "params.functionParams"
                                                                              );
    private final ServerLessTryoutHandler tryoutHandler;
    private final String projectPath;

    public ExpressionHelperProvider(String projectPath) {

        this.projectPath = projectPath;
        this.tryoutHandler = new ServerLessTryoutHandler(projectPath);
    }

    public HelperPanelData getExpressionHelperData(ExpressionParam param) {

        MediatorTryoutRequest request = new MediatorTryoutRequest(param.getDocumentUri(), param.getPosition().getLine(),
                param.getPosition().getLine(), null, null);
        MediatorTryoutInfo tryoutInfo = getMediatorTryoutInfo(request);
        MediatorInfo propsData = tryoutInfo.getOutput();
        Map<String, Functions> functions = ExpressionCompletionUtils.getFunctions();
        return createHelperData(propsData, functions);
    }

    private MediatorTryoutInfo getMediatorTryoutInfo(MediatorTryoutRequest request) {

        MediatorTryoutInfo info = tryoutHandler.handle(request);
        List<Property> configurables = ExpressionCompletionUtils.getConfigs(projectPath);
        info.setInputConfigs(configurables);
        info.setOutputConfigs(configurables);
        return info;
    }

    private HelperPanelData createHelperData(MediatorInfo propsData, Map<String, Functions> functions) {

        HelperPanelData helperData = new HelperPanelData();
        setFunctions(helperData, functions);
        helperData.setVariables(createDataList(propsData.getVariables(), ExpressionConstants.VAR));
        helperData.setPayload(createDataList(propsData.getPayload()));
        helperData.setProperties(createDataList(propsData.getProperties()));
        helperData.setParams(createDataList(propsData.getParams()));
        helperData.setConfigs(createDataList(propsData.getConfigs(), ExpressionConstants.CONFIG));
        helperData.setHeaders(createDataList(propsData.getHeaders(), ExpressionConstants.HEADERS));
        return helperData;
    }

    private void setFunctions(HelperPanelData helperData, Map<String, Functions> functions) {

        Map<String, Functions> clonedFunctionsMap = new HashMap<>();
        for (Map.Entry<String, Functions> entry : functions.entrySet()) {
            String key = entry.getKey();
            Functions value = entry.getValue();
            clonedFunctionsMap.put(key, value.deepCopy());
        }
        helperData.setFunctions(clonedFunctionsMap);
    }

    private List<CompletionItem> createDataList(Properties attributes) {

        List<CompletionItem> dataList = new ArrayList<>();

        for (Map.Entry<Properties.Type, String> entry : PROPERTIES_SECOND_LEVEL.entrySet()) {
            String label = Utils.toCamelCase(entry.getKey().toString());
            HelperPanelItem item = new HelperPanelItem(label, entry.getValue());
            item.addChildren(createDataList(attributes.getPropertiesByType(entry.getKey()), entry.getValue()));
            dataList.add(item);
        }
        return dataList;
    }

    private List<CompletionItem> createDataList(Params params) {

        List<CompletionItem> dataList = new ArrayList<>();
        for (Map.Entry<Params.Type, String> entry : PARAMS_SECOND_LEVEL.entrySet()) {
            String label = Utils.toCamelCase(entry.getKey().toString());
            HelperPanelItem item = new HelperPanelItem(label, entry.getValue());
            item.addChildren(createDataList(params.getPropertiesByType(entry.getKey()), entry.getValue()));
            dataList.add(item);
        }
        return dataList;
    }

    private List<CompletionItem> createDataList(JsonPrimitive payload) {

        List<CompletionItem> dataList = new ArrayList<>();
        if (payload != null && Utils.isJSONObject(payload.toString())) {
            JsonObject jsonObject = Utils.getJsonObject(payload.getAsString());
            if (jsonObject != null) {
                CompletionItem item = new HelperPanelItem(ExpressionConstants.PAYLOAD, ExpressionConstants.PAYLOAD);
                ((HelperPanelItem) item).addChildren(addJsonChildren(jsonObject, ExpressionConstants.PAYLOAD));
                dataList.add(item);
            }
        }
        return dataList;
    }

    private List<CompletionItem> createDataList(List<Property> variables, String expressionPrefix) {

        List<CompletionItem> dataList = new ArrayList<>();
        for (Property variable : variables) {
            String expression = expressionPrefix + Constant.DOT + variable.getKey();
            HelperPanelItem item = new HelperPanelItem(variable.getKey(), expression);
            String value = variable.getValue();
            if (value != null && Utils.isJSONObject(value)) {
                item.addChildren(addJsonChildren(Utils.getJsonObject(value), expression));
            }
            dataList.add(item);
        }
        return dataList;
    }

    private List<CompletionItem> addJsonChildren(JsonObject jsonObject, String expressionPrefix) {

        List<CompletionItem> dataList = new ArrayList<>();
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String expression = expressionPrefix + "." + entry.getKey();
            HelperPanelItem item = new HelperPanelItem(entry.getKey(), expression);
            JsonElement value = entry.getValue();
            if (value.isJsonObject()) {
                item.addChildren(addJsonChildren(value.getAsJsonObject(), expression));
            }
            dataList.add(item);
        }
        return dataList;
    }
}
