package pl.charmas.serializablegenerator.typeserializers;

import com.intellij.psi.PsiType;

import pl.charmas.serializablegenerator.typeserializers.serializers.JSONObjectSerializer;

/**
 * Custom serializer factory for JSON objects
 *
 * @author Dallas Gutauckis [dallas@gutauckis.com]
 */
public class JSONObjectSerializerFactory implements TypeSerializerFactory {
    private final JSONObjectSerializer mSerializer;

    public JSONObjectSerializerFactory() {
        mSerializer = new JSONObjectSerializer();
    }

    @Override
    public TypeSerializer getSerializer(PsiType psiType) {
        if ("org.json.JSONObject".equals(psiType.getCanonicalText())) {
            return mSerializer;
        }

        return null;
    }
}
