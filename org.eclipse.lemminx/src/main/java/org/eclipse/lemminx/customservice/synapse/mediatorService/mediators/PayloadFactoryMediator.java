package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.mediatorService.MediatorUtils;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.Switch;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.filter.switchMediator.SwitchCase;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.transformation.payload.PayloadFactory;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PayloadFactoryMediator {

    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData(Map<String, Object> data, PayloadFactory payloadFactory, List<String> dirtyFields) {


        return Either.forLeft(data);
    }

    public static Map<String, Object> getDataFromST(PayloadFactory node) {
        Map<String, Object> data = new HashMap<>();



        return data;
    }
}
