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

package org.eclipse.lemminx.customservice.synapse.mediatorService.mediators;

import org.eclipse.lemminx.customservice.synapse.syntaxTree.pojo.mediator.advanced.Transaction;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.messages.Either;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransactionMediator {
    public static Either<Map<String, Object>, Map<Range, Map<String, Object>>> processData430(Map<String, Object> data,
                                                                                              Transaction transaction,
                                                                                              List<String> dirtyFields) {
        if (data.containsKey("action") && data.get("action") instanceof String) {
            String action = (String) data.get("action");
            switch (action) {
                case "Commit transaction":
                    data.put("action", "commit");
                    break;
                case "Fault if no transaction":
                    data.put("action", "fault-if-no-tx");
                    break;
                case "Initiate new transaction":
                    data.put("action", "new");
                    break;
                case "Resume transaction":
                    data.put("action", "resume");
                    break;
                case "Suspend transaction":
                    data.put("action", "suspend");
                    break;
                case "Rollback transaction":
                    data.put("action", "rollback");
                    break;
                case "Use existing or initiate transaction":
                    data.put("action", "use-existing-or-new");
                    break;
                default:
                    data.put("action", "");
                    break;
            }
        }
        return Either.forLeft(data);

    }

    public static Map<String, Object> getDataFromST430(Transaction node) {

        Map<String, Object> data = new HashMap<>();
        data.put("description", node.getDescription());
        if (node.getAction() != null) {
            switch (node.getAction()) {
                case COMMIT:
                    data.put("action", "Commit transaction");
                    break;
                case FAULT_IF_NO_TX:
                    data.put("action", "Fault if no transaction");
                    break;
                case NEW:
                    data.put("action", "Initiate new transaction");
                    break;
                case RESUME:
                    data.put("action", "Resume transaction");
                    break;
                case SUSPEND:
                    data.put("action", "Suspend transaction");
                    break;
                case ROLLBACK:
                    data.put("action", "Rollback transaction");
                    break;
                case USE_EXISTING_OR_NEW:
                    data.put("action", "Use existing or initiate transaction");
                    break;
                default:
                    data.put("action", "");
                    break;
            }
        }
        return data;
    }
}
