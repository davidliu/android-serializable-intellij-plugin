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
package pl.charmas.serializablegenerator;

import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.PsiParameterList;
import com.intellij.psi.PsiReferenceList;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;

import pl.charmas.serializablegenerator.typeserializers.*;
import pl.charmas.serializablegenerator.util.PsiUtils;

import java.util.List;


/**
 * Quite a few changes here by Dallas Gutauckis [dallas@gutauckis.com]
 */
public class CodeGenerator {
    public static final String TYPE_SERIALIZABLE = "java.io.Serializable";
    public static final String TYPE_OOS = "java.io.ObjectOutputStream";
    public static final String TYPE_OIS = "java.io.ObjectInputStream";

    private final PsiClass mClass;
    private final List<PsiField> mFields;
    private final TypeSerializerFactory mTypeSerializerFactory;

    public CodeGenerator(PsiClass psiClass, List<PsiField> fields) {
        mClass = psiClass;
        mFields = fields;

        this.mTypeSerializerFactory = new ChainSerializerFactory(
                new DateSerializerFactory(),
                new EnumerationSerializerFactory(),
                new PrimitiveTypeSerializerFactory(),
                new PrimitiveArraySerializerFactory(),
                new ListSerializerFactory(),
                new SerializableSerializerFactory()
        );
    }

    private String generateWriteObject(List<PsiField> fields) {
        StringBuilder sb = new StringBuilder("private void writeObject(ObjectOutputStream out) throws IOException {");

        for (PsiField field : fields) {
            sb.append(getSerializerForType(field).writeValue(field, "out"));
        }

        sb.append("}");

        return sb.toString();
    }

    private String generateReadObject(List<PsiField> fields) {
        StringBuilder sb = new StringBuilder("private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {");

        // Creates all of the deserialization methods for the given fields
        for (PsiField field : fields) {
            sb.append(getSerializerForType(field).readValue(field, "in"));
        }

        sb.append("}");
        return sb.toString();
    }

    private TypeSerializer getSerializerForType(PsiField field) {
        return mTypeSerializerFactory.getSerializer(field.getType());
    }

    public void generate() {
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(mClass.getProject());

        removeExistingSerializableImplementation(mClass);

        // Method for writing to the stream
        PsiMethod writeToStreamMethod = elementFactory.createMethodFromText(generateWriteObject(mFields), mClass);
        // Method for reading from the stream
        PsiMethod readFromStreamMethod = elementFactory.createMethodFromText(generateReadObject(mFields), mClass);

        // Default constructor if needed
        String defaultConstructorString = generateDefaultConstructor(mClass);
        PsiMethod defaultConstructor = null;

        if (defaultConstructorString != null) {
            defaultConstructor = elementFactory.createMethodFromText(defaultConstructorString, mClass);
        }

        JavaCodeStyleManager styleManager = JavaCodeStyleManager.getInstance(mClass.getProject());

        // Shorten all class references
        styleManager.shortenClassReferences(mClass.addBefore(writeToStreamMethod, mClass.getLastChild()));
        styleManager.shortenClassReferences(mClass.addBefore(readFromStreamMethod, mClass.getLastChild()));

        // Only adds if available
        if (defaultConstructor != null) {
            styleManager.shortenClassReferences(mClass.addBefore(defaultConstructor, mClass.getLastChild()));
        }

        makeClassImplementSerializable(elementFactory);
    }

    /**
     * Strips the
     *
     * @param psiClass
     */
    private void removeExistingSerializableImplementation(PsiClass psiClass) {
        PsiField[] allFields = psiClass.getAllFields();

        findAndRemoveMethod(psiClass, "describeContent");
        findAndRemoveMethod(psiClass, "writeToParcel", TYPE_SERIALIZABLE, "int");
    }

    private String generateDefaultConstructor(PsiClass clazz) {
        // Check for any constructors; if none exist, we'll make a default one
        if (clazz.getConstructors().length == 0) {
            // No constructors exist, make a default one for convenience
            return "public " + clazz.getName() + "(){}" + '\n';
        } else {
            return null;
        }
    }

    private boolean hasSerializableSuperclass() {
        PsiClassType[] superTypes = mClass.getSuperTypes();
        for (PsiClassType superType : superTypes) {
            if (PsiUtils.isOfType(superType, TYPE_SERIALIZABLE)) {
                return true;
            }
        }
        return false;
    }

    private void makeClassImplementSerializable(PsiElementFactory elementFactory) {
        if (hasSerializableSuperclass()) return;

        final PsiClassType[] implementsListTypes = mClass.getImplementsListTypes();
        final String implementsType = TYPE_SERIALIZABLE;

        for (PsiClassType implementsListType : implementsListTypes) {
            PsiClass resolved = implementsListType.resolve();

            // Already implements Parcelable, no need to add it
            if (resolved != null && implementsType.equals(resolved.getQualifiedName())) {
                return;
            }
        }

        PsiJavaCodeReferenceElement implementsReference = elementFactory.createReferenceFromText(implementsType, mClass);
        PsiReferenceList implementsList = mClass.getImplementsList();

        if (implementsList != null) {
            implementsList.add(implementsReference);
        }
    }


    private static void findAndRemoveMethod(PsiClass clazz, String methodName, String... arguments) {
        // Maybe there's an easier way to do this with mClass.findMethodBySignature(), but I'm not an expert on Psi*
        PsiMethod[] methods = clazz.findMethodsByName(methodName, false);

        for (PsiMethod method : methods) {
            PsiParameterList parameterList = method.getParameterList();

            if (parameterList.getParametersCount() == arguments.length) {
                boolean shouldDelete = true;

                PsiParameter[] parameters = parameterList.getParameters();

                for (int i = 0; i < arguments.length; i++) {
                    if (!parameters[i].getType().getCanonicalText().equals(arguments[i])) {
                        shouldDelete = false;
                    }
                }

                if (shouldDelete) {
                    method.delete();
                }
            }
        }
    }
}
