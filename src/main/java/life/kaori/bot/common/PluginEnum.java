package life.kaori.bot.common;

import life.kaori.bot.plugins.Dao;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * author: origin
 */
@AllArgsConstructor
public enum PluginEnum {
    Dao(Dao.class.getSimpleName(), Dao.class, "刀", "几点了？");
    public String name;
    public Class clazz;
    public String alias;
    public String help;
}
