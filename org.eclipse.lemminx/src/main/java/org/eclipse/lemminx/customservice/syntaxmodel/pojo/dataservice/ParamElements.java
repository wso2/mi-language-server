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

package org.eclipse.lemminx.customservice.syntaxmodel.pojo.dataservice;

import java.util.Optional;

public class ParamElements {

    Optional<ParamValidateCustom> validateCustom;
    Optional<ParamValidateLength> validateLength;
    Optional<ParamValidatePattern> validatePattern;
    Optional<ParamValidateLongRange> validateLongRange;
    Optional<ParamValidateDoubleRange> validateDoubleRange;

    public Optional<ParamValidateCustom> getValidateCustom() {

        return validateCustom;
    }

    public void setValidateCustom(Optional<ParamValidateCustom> validateCustom) {

        this.validateCustom = validateCustom;
    }

    public Optional<ParamValidateLength> getValidateLength() {

        return validateLength;
    }

    public void setValidateLength(Optional<ParamValidateLength> validateLength) {

        this.validateLength = validateLength;
    }

    public Optional<ParamValidatePattern> getValidatePattern() {

        return validatePattern;
    }

    public void setValidatePattern(Optional<ParamValidatePattern> validatePattern) {

        this.validatePattern = validatePattern;
    }

    public Optional<ParamValidateLongRange> getValidateLongRange() {

        return validateLongRange;
    }

    public void setValidateLongRange(Optional<ParamValidateLongRange> validateLongRange) {

        this.validateLongRange = validateLongRange;
    }

    public Optional<ParamValidateDoubleRange> getValidateDoubleRange() {

        return validateDoubleRange;
    }

    public void setValidateDoubleRange(Optional<ParamValidateDoubleRange> validateDoubleRange) {

        this.validateDoubleRange = validateDoubleRange;
    }
}
