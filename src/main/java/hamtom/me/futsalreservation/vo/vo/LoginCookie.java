package hamtom.me.futsalreservation.vo.vo;

import lombok.Data;

@Data
public class LoginCookie {
    public static final String JSESSIONID_KEY = "JSESSIONID";
    public static final String WMONID_KEY = "WMONID";

    private boolean loginStatus;
    private String JSESSIONID;
    private String WMONID;

    public String makeCookieForHeader() {
        return String.format("%s=%s; %s=%s", JSESSIONID_KEY, JSESSIONID, WMONID_KEY, WMONID);
    }
}
