package pl.charmas.serializablegenerator.typeserializers.serializers;

import com.intellij.psi.PsiField;
import org.apache.xmlbeans.impl.common.NameUtil;
import pl.charmas.serializablegenerator.typeserializers.TypeSerializer;

/**
 * Modified by Dallas Gutauckis [dallas@gutauckis.com]
 */
public class EnumerationSerializer implements TypeSerializer {
    @Override
    public String writeValue(PsiField field, String out) {
        String fieldName = field.getName();
        return String.format("%s.writeInt(this.%s == null ? -1 : this.%s.ordinal());", out, fieldName, fieldName);
    }

    @Override
    public String readValue(PsiField field, String in) {
        String fieldName = field.getName();
        String tmpFieldName = NameUtil.upperCaseFirstLetter(fieldName);
        String format = "int tmp%s = %s.readInt();"
                + "this.%s = tmp%s == -1 ? null : %s.values()[tmp%s];";
        return String.format(format, tmpFieldName, in, fieldName, tmpFieldName, field.getType().getCanonicalText(), tmpFieldName);
    }
}
