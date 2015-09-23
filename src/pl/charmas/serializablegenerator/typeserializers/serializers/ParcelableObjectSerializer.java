package pl.charmas.serializablegenerator.typeserializers.serializers;

import com.intellij.psi.PsiField;
import pl.charmas.serializablegenerator.typeserializers.TypeSerializer;

/**
 * Serializer for types implementing Parcelable
 *
 * @author Dallas Gutauckis [dallas@gutauckis.com]
 */
public class ParcelableObjectSerializer implements TypeSerializer {
    @Override
    public String writeValue(PsiField field, String out) {
        return out + ".writeParcelable(this." + field.getName() + ", 0);";
    }

    @Override
    public String readValue(PsiField field, String in) {
        return "this." + field.getName() + " = " + in + ".readParcelable(" + field.getType().getCanonicalText() + ".class.getClassLoader());";
    }
}
