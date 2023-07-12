package life.kaori.bot.common;

import org.springframework.boot.system.ApplicationHome;

import java.io.File;

/**
 * author: origin
 */
public class CommonUtil {

    public static String jarDir;

    static {
        ApplicationHome home = new ApplicationHome(CommonUtil.class.getClass());
        File dir = home.getDir();
        jarDir = dir.toString();
    }
}
