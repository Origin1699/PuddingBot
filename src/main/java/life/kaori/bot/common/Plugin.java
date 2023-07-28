package life.kaori.bot.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * author: origin
 */
@AllArgsConstructor
@Getter
public enum Plugin {

    AntiBiliApp("AntiBiliApp", "b站小程序解析", "bilibili解析, b站解析, b站小程序解析, b站解析"),
    Dao("Dao", "刀", "刀, dao, 几点了");
    private final String name;
    private final String value;
    private final String alias;

    public static List<Plugin> getPlugins() {
        return Arrays.asList(Plugin.values());
    }

    public static Plugin getPlugin(String pluginName) {

        return Plugin.valueOf(pluginName);
    }
}
