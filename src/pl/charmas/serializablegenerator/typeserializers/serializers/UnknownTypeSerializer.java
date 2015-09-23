/*
 * Copyright (C) 2013 Michał Charmas (http://blog.charmas.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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
package pl.charmas.serializablegenerator.typeserializers.serializers;

import com.intellij.psi.PsiField;

import pl.charmas.serializablegenerator.typeserializers.TypeSerializer;

public class UnknownTypeSerializer implements TypeSerializer {

    @Override
    public String writeValue(PsiField field, String out) {
        return out + ".writeObject(this." + field.getName() + ");";
    }

    @Override
    public String readValue(PsiField field, String in) {
        return "this." + field.getName() + " = (" + field.getType().getCanonicalText() + ") " + in + ".readObject();";
    }
}
