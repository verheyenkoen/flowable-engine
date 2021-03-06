/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.flowable.variable.api.types;

/**
 * Interface describing a container for all available {@link VariableType}s of variables.
 * 
 * @author dsyer
 * @author Frederik Heremans
 */
public interface VariableTypes {

    /**
     * @return the type for the given type name. Returns null if no type was found with the name.
     */
    VariableType getVariableType(String typeName);

    /**
     * @return the variable type to be used to store the given value as a variable.
     * @throws org.flowable.common.engine.api.FlowableException
     *             When no available type is capable of storing the value.
     */
    VariableType findVariableType(Object value);

    VariableTypes addType(VariableType type);

    /**
     * Add the variable type before the type with the given name.
     * When a type with the requested name is not registered then use {@link #addType(VariableType)}
     * @param type the type to add
     * @param afterTypeName the name of the other type
     */
    default VariableTypes addTypeBefore(VariableType type, String afterTypeName) {
        int afterTypeIndex = getTypeIndex(afterTypeName);
        if (afterTypeIndex > -1) {
            return addType(type, afterTypeIndex);
        } else {
            return addType(type);
        }
    }

    /**
     * Add type at the given index. The index is used when finding a type for an object. When different types can store a specific object value, the one with the smallest index will be used.
     */
    VariableTypes addType(VariableType type, int index);

    int getTypeIndex(VariableType type);

    int getTypeIndex(String typeName);

    VariableTypes removeType(VariableType type);
}
