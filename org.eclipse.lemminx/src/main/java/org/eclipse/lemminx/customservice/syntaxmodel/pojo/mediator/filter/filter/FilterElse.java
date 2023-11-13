/*
 * Copyright (c) 2023, WSO2 LLC. (http://www.wso2.com).
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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.filter.filter;

import org.eclipse.lemminx.customservice.syntaxmodel.pojo.STNode;
import org.eclipse.lemminx.customservice.syntaxmodel.pojo.mediator.Mediator;

import java.util.ArrayList;
import java.util.List;

public class FilterElse extends STNode {

    List<Mediator> mediatorList;
    String sequence;

    public FilterElse() {

        mediatorList = new ArrayList<>();
    }

    public void addToMediatorList(Mediator mediator) {

        mediatorList.add(mediator);
    }

    public List<Mediator> getMediatorList() {

        return mediatorList;
    }

    public void setMediatorList(List<Mediator> mediatorList) {

        this.mediatorList = mediatorList;
    }

    public String getSequence() {

        return sequence;
    }

    public void setSequence(String sequence) {

        this.sequence = sequence;
    }
}
