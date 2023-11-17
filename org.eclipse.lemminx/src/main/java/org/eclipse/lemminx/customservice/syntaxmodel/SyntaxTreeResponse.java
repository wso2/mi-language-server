package org.eclipse.lemminx.customservice.syntaxmodel;

import com.google.gson.JsonElement;

public class SyntaxTreeResponse {

    private JsonElement syntaxTree;
    private String defFilePath;

    public JsonElement getSyntaxTree() {
        return syntaxTree;
    }

    public void setSyntaxTree(JsonElement syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    public void setDefFilePath(String defFilePath) {
        this.defFilePath = defFilePath;
    }

    public String getDefFilePath() {
        return defFilePath;
    }

    public SyntaxTreeResponse(JsonElement syntaxTree, String defFilePath) {
        this.syntaxTree = syntaxTree;
        this.defFilePath = defFilePath;
    }
}
