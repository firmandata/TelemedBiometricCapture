package constants;

public class Config {
    public static final String TELEMED_URL = "http://localhost:8088/member/index";
    
    public static final int FINGER_SDK = Constant.FINGER_SDK_DIGITAL_PERSONA_ONE_TOUCH;
    
    public static final String NEUROTECT_SERVICE_HOST = "192.168.0.4";
    public static final int NEUROTECT_SERVICE_PORT = 9050;
    
    public static final boolean RUN_AS_SERVICE = (FINGER_SDK == Constant.FINGER_SDK_DIGITAL_PERSONA_ONE_TOUCH ? false : false);
}
