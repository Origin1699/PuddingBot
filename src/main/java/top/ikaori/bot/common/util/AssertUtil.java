package top.ikaori.bot.common.util;

import top.ikaori.bot.core.exception.BotException;

import java.util.Collection;

/**
 * @author origin
 */
public class AssertUtil {

    public static <T> void notEmpty(Collection<T> collection, String exceptionMsg) {
        if (collection == null || collection.isEmpty()) {
            throw new BotException(exceptionMsg);
        }
    }

    public static <T> void isNull(Object object, String exceptionMsg) {
        if (object == null) {
            throw new BotException(exceptionMsg);
        }
    }
}
