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

/**
 * Custom serializer for Date objects to simplify parceling
 *
 * @author Dallas Gutauckis [dallas@gutauckis.com]
 */
public class UriSerializer implements TypeSerializer {

    @Override
    public String writeValue(PsiField field, String out) {
        String fieldName = "this." + field.getName();
        return String.format("%1$s.writeObject(%2$s == null ? null : %2$s.toString());", out, fieldName);
    }

    @Override
    public String readValue(PsiField field, String in) {
        return String.format("this.%2$s = Uri.parse((String) %1$s.readObject());", in, field.getName());
    }
}
