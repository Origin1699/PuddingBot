package life.kaori.bot.common;

import life.kaori.bot.plugins.AppletParse;
import life.kaori.bot.plugins.Dao;
import lombok.AllArgsConstructor;

/**
 * author: origin
 */
@AllArgsConstructor
public enum PluginEnum {
    Dao(Dao.class.getSimpleName(), Dao.class, "刀", "几点了？"),
    AppletParse(AppletParse.class.getSimpleName(), AppletParse.class, "小程序解析", "");
    public final String name;
    public final Class clazz;
    public final String alias;
    public final String help;
}
