package life.kaori.bot.common.util;

import com.sun.istack.Nullable;
import life.kaori.bot.core.exception.BotException;

import java.util.Collection;

/**
 * author: origin
 */
public class AssertUtil {

    public static <T> void notEmpty(@Nullable Collection<T> collection, String exceptionMsg) {
        if (collection == null || collection.isEmpty()) {
            throw new BotException(exceptionMsg);
        }
    }

    public static <T> void isNull(@Nullable Object object, String exceptionMsg) {
        if (object == null) {
            throw new BotException(exceptionMsg);
        }
    }
}
