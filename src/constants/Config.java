package constants;

public class Config {
    public static final String TELEMED_URL = "http://192.168.0.8/telemed/index.php/member/index";
    //public static final String TELEMED_URL = "https://192.168.0.8/socket-client/index.html?id=11&type=1&name=Firman";
    
    public static final int FINGER_SDK = Constant.FINGER_SDK_DIGITAL_PERSONA_ONE_TOUCH;
    
    public static final String NEUROTECT_SERVICE_HOST = "192.168.0.5";
    public static final int NEUROTECT_SERVICE_PORT = 9050;
    
    public static final boolean RUN_AS_SERVICE = (FINGER_SDK == Constant.FINGER_SDK_DIGITAL_PERSONA_ONE_TOUCH ? false : false);
    
    public static final int BROWSER_PROVIDER = Constant.BROWSER_PROVIDER_JXBROWSER;
}
