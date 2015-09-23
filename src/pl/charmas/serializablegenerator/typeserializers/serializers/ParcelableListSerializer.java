package pl.charmas.serializablegenerator.typeserializers.serializers;

import com.intellij.psi.PsiField;
import pl.charmas.serializablegenerator.typeserializers.TypeSerializer;
import pl.charmas.serializablegenerator.util.PsiUtils;

/**
 * @author Dallas Gutauckis [dallas@gutauckis.com]
 * @author Michał Charmas [michal@charmas.pl]
 */
public class ParcelableListSerializer implements TypeSerializer {
    @Override
    public String writeValue(PsiField field, String out) {
        return String.format("%s.writeTypedList(%s);", out, field.getName());
    }

    @Override
    public String readValue(PsiField field, String in) {
        String paramType = PsiUtils.getResolvedGenerics(field.getType()).get(0).getCanonicalText();
        return String.format("this.%s = %s.createTypedArrayList(%s.CREATOR);", field.getName(), in, paramType);
    }
}
