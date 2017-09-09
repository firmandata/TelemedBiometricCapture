package constants;

public class Config {
    public static final String TELEMED_URL = "http://192.168.5.89/telemed/index.php/member/index";
    
    public static final int FINGER_SDK = Constant.FINGER_SDK_NEUROTEC;
    
    public static final String NEUROTECT_SERVICE_HOST = "192.168.5.87";
    public static final int NEUROTECT_SERVICE_PORT = 9050;
    public static final String NEUROTECT_NSERVER_HOST = "192.168.5.55";
    
    public static final boolean RUN_AS_SERVICE = (FINGER_SDK == Constant.FINGER_SDK_DIGITAL_PERSONA_ONE_TOUCH ? false : true);
    
    public static final int BROWSER_PROVIDER = Constant.BROWSER_PROVIDER_JXBROWSER;
}
