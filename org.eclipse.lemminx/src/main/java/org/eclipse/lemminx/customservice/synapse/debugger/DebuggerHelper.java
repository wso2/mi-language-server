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

package org.eclipse.lemminx.customservice.synapse.debugger;

import com.google.gson.JsonElement;
import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.ApiDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.IDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.InboundDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.ProxyDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.SequenceDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.debuginfo.TemplateDebugInfo;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.Breakpoint;
import org.eclipse.lemminx.customservice.synapse.debugger.entity.BreakpointValidity;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.ApiVisitor;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.InboundEndpointVisitor;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.ProxyVisitor;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.SequenceVisitor;
import org.eclipse.lemminx.customservice.synapse.debugger.visitor.TemplateVisitor;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.SyntaxTreeGenerator;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.NamedSequence;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.STNode;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.api.API;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.inbound.InboundEndpoint;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.proxy.Proxy;
import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.template.Template;
import org.eclipse.lemminx.customservice.synapse.utils.Constant;
import org.eclipse.lemminx.customservice.synapse.utils.Utils;
import org.eclipse.lemminx.dom.DOMDocument;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DebuggerHelper {

    private static final Logger LOGGER = Logger.getLogger(DebuggerHelper.class.getName());
    private STNode syntaxTree;
    private String filePath;

    public DebuggerHelper(String filePath) {

        this.filePath = filePath;
        try {
            this.syntaxTree = getSyntaxTree();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error while generating syntax tree for: " + filePath, e);
        }
    }

    public List<BreakpointValidity> validateBreakpoints(List<Breakpoint> breakpoints) {

        List<IDebugInfo> debugInfos = generateDebugInfo(breakpoints);
        List<BreakpointValidity> validationList = new ArrayList<>();
        for (int i = 0; i < breakpoints.size(); i++) {
            Breakpoint breakPoint = breakpoints.get(i);
            IDebugInfo debugInfo = debugInfos.get(i);
            BreakpointValidity breakpointValidity = new BreakpointValidity(breakPoint.getLine(), debugInfo.isValid(),
                    debugInfo.getError());
            validationList.add(breakpointValidity);
        }
        return validationList;
    }

    public List<JsonElement> generateDebugInfoJson(List<Breakpoint> breakpoints) {

        List<IDebugInfo> debugInfos = generateDebugInfo(breakpoints);
        List<JsonElement> out = new ArrayList<>();
        for (IDebugInfo debugInfo : debugInfos) {
            out.add(debugInfo.toJson());
        }
        return out;
    }

    public List<IDebugInfo> generateDebugInfo(List<Breakpoint> breakpoints) {

        List<IDebugInfo> breakPointInfo = new ArrayList<>();
        for (Breakpoint bpr : breakpoints) {
            breakPointInfo.add(generateDebugInfo(bpr));
        }
        return breakPointInfo;
    }

    public IDebugInfo generateDebugInfo(Breakpoint breakPoint) {

        String tag = syntaxTree.getTag();
        IDebugInfo debugInfo = null;
        if (Constant.API.equalsIgnoreCase(tag)) {
            debugInfo = new ApiDebugInfo();
            ApiVisitor apiVisitor = new ApiVisitor((API) syntaxTree, breakPoint, (ApiDebugInfo) debugInfo);
            apiVisitor.startVisit();
        } else if (Constant.PROXY.equalsIgnoreCase(tag)) {
            debugInfo = new ProxyDebugInfo();
            ProxyVisitor proxyVisitor = new ProxyVisitor((Proxy) syntaxTree, breakPoint,
                    (ProxyDebugInfo) debugInfo);
            proxyVisitor.startVisit();
        } else if (Constant.SEQUENCE.equalsIgnoreCase(tag)) {
            debugInfo = new SequenceDebugInfo();
            SequenceVisitor sequenceVisitor = new SequenceVisitor((NamedSequence) syntaxTree, breakPoint,
                    (SequenceDebugInfo) debugInfo);
            sequenceVisitor.startVisit();
        } else if (Constant.INBOUND_ENDPOINT.equalsIgnoreCase(tag)) {
            debugInfo = new InboundDebugInfo();
            InboundEndpointVisitor inboundEndpointVisitor = new InboundEndpointVisitor((InboundEndpoint) syntaxTree,
                    breakPoint, (InboundDebugInfo) debugInfo);
            inboundEndpointVisitor.startVisit();
        } else if (Constant.TEMPLATE.equalsIgnoreCase(tag)) {
            debugInfo = new TemplateDebugInfo();
            TemplateVisitor templateVisitor = new TemplateVisitor((Template) syntaxTree, breakPoint,
                    (TemplateDebugInfo) debugInfo);
            templateVisitor.startVisit();
        }
        return debugInfo;
    }

    private STNode getSyntaxTree() throws IOException {

        File file = new File(filePath);
        DOMDocument document = Utils.getDOMDocument(file);
        return SyntaxTreeGenerator.buildTree(document.getDocumentElement());
    }
}
