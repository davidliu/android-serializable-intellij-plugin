package pl.charmas.serializablegenerator.typeserializers;

import com.intellij.psi.PsiType;

import pl.charmas.serializablegenerator.typeserializers.serializers.JSONObjectSerializer;
import pl.charmas.serializablegenerator.typeserializers.serializers.UriSerializer;

/**
 * Custom serializer factory for Uri objects
 *
 * @author Dallas Gutauckis [dallas@gutauckis.com]
 */
public class UriSerializerFactory implements TypeSerializerFactory {
    private final UriSerializer mSerializer;

    public UriSerializerFactory() {
        mSerializer = new UriSerializer();
    }

    @Override
    public TypeSerializer getSerializer(PsiType psiType) {
        if ("android.net.Uri".equals(psiType.getCanonicalText())) {
            return mSerializer;
        }

        return null;
    }
}
