package top.ikaori.bot.plugins;

import java.util.List;

/**
 * 需要管理的插件
 * <p>
 *
 * @author origin
 */
public interface Plugin {
    default String getName() {
        return this.getClass().getSimpleName();
    }
    List<String> getNickName();

    String getHelp();

    default void cleanExpiringMap() {
    }
}
