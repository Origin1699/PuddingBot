package life.kaori.bot.common;

import life.kaori.bot.plugins.MiniApp;
import life.kaori.bot.plugins.Dao;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * author: origin
 */
@AllArgsConstructor
public enum PluginEnum {
    Dao(Dao.class.getSimpleName(), Dao.class, "刀", "几点了？"),
    MiniApp(MiniApp.class.getSimpleName(), MiniApp.class, "小程序解析", "");
    public final String name;
    public final Class clazz;
    public final String alias;
    public final String help;

    public PluginEnum gatPlugin(String name) {
        return Arrays.stream(PluginEnum.values()).filter(p -> p.name.equalsIgnoreCase(name) || p.alias.contains(name)).toList().get(0);
    }
}
