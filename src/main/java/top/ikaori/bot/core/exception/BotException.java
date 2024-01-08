package top.ikaori.bot.core.exception;

import top.ikaori.bot.common.constant.BotStrings;

/**
 * @author origin
 */
public class BotException extends RuntimeException {

    public BotException(String message) {
        super(message);
    }


    public static BotException create(BotStrings botStrings, Object... args) {
        return new BotException(botStrings.getMessage(args));
    }
}
