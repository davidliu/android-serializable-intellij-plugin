package pl.charmas.serializablegenerator.typeserializers.serializers;

import com.intellij.psi.PsiField;
import pl.charmas.serializablegenerator.typeserializers.TypeSerializer;

public class CharPrimitiveSerializer implements TypeSerializer {
    @Override
    public String writeValue(PsiField field, String out) {
        return out + ".writeInt(" + field.getName() + ");";
    }

    @Override
    public String readValue(PsiField field, String in) {
        return "this." + field.getName() + " = (char) " + in + ".readInt();";
    }
}
