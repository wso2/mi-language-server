package org.eclipse.lemminx.customservice.synapse.mediatorService.pojo;

import org.eclipse.lsp4j.TextEdit;

import java.util.ArrayList;
import java.util.List;

public class SynapseConfigResponse {

    public List<TextEdit> textEdits;

    public SynapseConfigResponse() {

        textEdits = new ArrayList<>();
    }

    public SynapseConfigResponse(TextEdit edit) {

        textEdits = new ArrayList<>();
        addTextEdit(edit);
    }

    public void addTextEdit(TextEdit edit) {

        textEdits.add(edit);
    }

    @Override
    public String toString() {

        return "SynapseConfigResponse{" +
                "textEdits=" + textEdits +
                '}';
    }
}
