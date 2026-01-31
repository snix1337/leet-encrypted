package obf.uscate.classes;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import obf.uscate.annotations.VMProtect;
import obf.uscate.enums.VMProtectType;

@UtilityClass
@VMProtect(type = VMProtectType.ULTRA)
public class Profile {
    @Getter
    public int uid = 1;
}