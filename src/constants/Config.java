package constants;

import java.io.FileInputStream;
import java.util.Properties;

public class Config {
    public static String TELEMED_URL = "https://127.0.0.1/telemed/index.php/member/index";
    
    public static int FINGER_SDK = Constant.FINGER_SDK_NEUROTEC;
    
    public static String NEUROTECT_SERVICE_HOST = "127.0.0.1";
    public static int NEUROTECT_SERVICE_PORT = 9050;
    
    public static String NEUROTECT_NSERVER_HOST = "127.0.0.1";
    public static int NEUROTECT_NSERVER_PORT = 25452;
    public static int NEUROTECT_NSERVER_PORT_ADMIN = 24932;
    
    public static boolean RUN_AS_SERVICE = false;
    
    public static int BROWSER_PROVIDER = Constant.BROWSER_PROVIDER_JXBROWSER;
    
    public static String BOOTH = "1";
    
    public static void extractConfigFile() {
        extractConfigFile("config.ini");
    }
    
    public static void extractConfigFile(final String fileName) {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(fileName));
            
            TELEMED_URL = properties.getProperty("TELEMED_URL");
            
            FINGER_SDK = Integer.parseInt(properties.getProperty("FINGER_SDK", String.valueOf(Constant.FINGER_SDK_NEUROTEC)));
            
            NEUROTECT_SERVICE_HOST = properties.getProperty("NEUROTECT_SERVICE_HOST");
            NEUROTECT_SERVICE_PORT = Integer.parseInt(properties.getProperty("NEUROTECT_SERVICE_PORT", "9050"));
            
            NEUROTECT_NSERVER_HOST = properties.getProperty("NEUROTECT_NSERVER_HOST");
            NEUROTECT_NSERVER_PORT = Integer.parseInt(properties.getProperty("NEUROTECT_NSERVER_PORT", "25452"));
            NEUROTECT_NSERVER_PORT_ADMIN = Integer.parseInt(properties.getProperty("NEUROTECT_NSERVER_PORT_ADMIN", "24932"));
            
            RUN_AS_SERVICE = Boolean.parseBoolean(properties.getProperty("RUN_AS_SERVICE", String.valueOf("false")));
            if (RUN_AS_SERVICE && FINGER_SDK == Constant.FINGER_SDK_DIGITAL_PERSONA_ONE_TOUCH)
                RUN_AS_SERVICE = false;
            
            BROWSER_PROVIDER = Integer.parseInt(properties.getProperty("BROWSER_PROVIDER", String.valueOf(Constant.BROWSER_PROVIDER_JXBROWSER)));
            
            BOOTH = properties.getProperty("BOOTH");
            
            properties.list(System.out);
        } catch (Exception e) {
            
        }
    }
}
