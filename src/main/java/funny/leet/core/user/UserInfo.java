package funny.leet.core.user;

import lombok.Getter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserInfo {
    @Getter
    public String username = System.getenv("username");
    @Getter
    public String role = "PREMIUM";
    /*@Getter
    public String expire = "2038-06-06";
     */
}