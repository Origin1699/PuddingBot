package top.ikaori.bot.common.constant;

import java.nio.charset.Charset;

/**
 * @author origin
 */
public class Constant {

    public final static String ROLE_ADMIN = "admin";

    public final static String ROLE_OWNER = "owner";

    public static final String DEFAULT_ENCODING = "UTF-8";
    public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_ENCODING);
    public static final String RN = "\r\n";

    public static final String MESSAGE_TYPE_GROUP = "group";
    public static final String MESSAGE_TYPE_PRIVATE = "private";
}
