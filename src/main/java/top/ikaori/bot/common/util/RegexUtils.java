package top.ikaori.bot.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author origin
 */
public class RegexUtils {

    public static String regex(String regex, String msg) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String regexGroup(String regex, String msg, int groupId) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group(groupId);
        }
        return null;
    }

}
