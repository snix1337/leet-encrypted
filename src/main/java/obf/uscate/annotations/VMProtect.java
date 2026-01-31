package obf.uscate.annotations;

import obf.uscate.enums.VMProtectType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface VMProtect {
    VMProtectType type();
}
