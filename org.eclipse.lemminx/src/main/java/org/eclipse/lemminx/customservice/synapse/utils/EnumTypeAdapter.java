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

package org.eclipse.lemminx.customservice.synapse.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class EnumTypeAdapter implements JsonSerializer<Enum<?>>, JsonDeserializer<Enum<?>> {

    @Override
    public Enum<?> deserialize(JsonElement jsonElement, Type type,
                               JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        return Utils.getEnumFromValue(jsonElement.getAsString(), (Class<Enum>) type);
    }

    @Override
    public JsonElement serialize(Enum<?> anEnum, Type type, JsonSerializationContext jsonSerializationContext) {

        try {
            Method method = anEnum.getClass().getDeclaredMethod("getValue");
            String value = (String) method.invoke(anEnum);
            return jsonSerializationContext.serialize(value);
        } catch (Exception e) {
        }
        return jsonSerializationContext.serialize(anEnum.name());
    }
}
