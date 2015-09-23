package pl.charmas.serializablegenerator.typeserializers;

import com.intellij.psi.PsiType;

import pl.charmas.serializablegenerator.typeserializers.serializers.SerializableArraySerializer;
import pl.charmas.serializablegenerator.typeserializers.serializers.ParcelableListSerializer;
import pl.charmas.serializablegenerator.typeserializers.serializers.ParcelableObjectSerializer;
import pl.charmas.serializablegenerator.util.PsiUtils;

import java.util.List;

/**
 * Serializer factory for Parcelable objects
 *
 * @author Dallas Gutauckis [dallas@gutauckis.com]
 * @author Michał Charmas [micha@charmas.pl]
 */
public class ParcelableSerializerFactory implements TypeSerializerFactory {
    private TypeSerializer mSerializer = new ParcelableObjectSerializer();
    private TypeSerializer listSerializer = new ParcelableListSerializer();
    private TypeSerializer arraySerializer = new SerializableArraySerializer();

    @Override
    public TypeSerializer getSerializer(PsiType psiType) {
        if (PsiUtils.isOfType(psiType, "android.os.Parcelable[]")) {
            return arraySerializer;
        }

        if (PsiUtils.isOfType(psiType, "android.os.Parcelable")) {
            return mSerializer;
        }

        if (PsiUtils.isOfType(psiType, "java.util.List")) {
            List<PsiType> resolvedGenerics = PsiUtils.getResolvedGenerics(psiType);
            for (PsiType resolvedGeneric : resolvedGenerics) {
                if (PsiUtils.isOfType(resolvedGeneric, "android.os.Parcelable")) {
                    return listSerializer;
                }
            }
        }

        return null;
    }

}
