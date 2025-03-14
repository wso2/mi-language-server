package org.eclipse.lemminx.customservice.synapse.mediatorService.pojo;

import org.eclipse.lsp4j.TextEdit;

import java.util.ArrayList;
import java.util.List;

public class SynapseConfigResponse {

    private List<TextEdit> textEdits;

    public SynapseConfigResponse() {

        textEdits = new ArrayList<>();
    }

    public SynapseConfigResponse(TextEdit edit) {

        textEdits = new ArrayList<>();
        addTextEdit(edit);
    }

    public List<TextEdit> getTextEdits() {

        sort();
        return textEdits;
    }

    public void addTextEdit(TextEdit edit) {

        textEdits.add(edit);
    }

    public void sort() {

        textEdits.sort((o1, o2) -> {
            if (o1.getRange().getStart().getLine() == o2.getRange().getStart().getLine()) {
                return o2.getRange().getStart().getCharacter() - o1.getRange().getStart().getCharacter();
            }
            return o2.getRange().getStart().getLine() - o1.getRange().getStart().getLine();
        });
    }

    @Override
    public String toString() {

        return "SynapseConfigResponse{" +
                "textEdits=" + textEdits +
                '}';
    }
}
