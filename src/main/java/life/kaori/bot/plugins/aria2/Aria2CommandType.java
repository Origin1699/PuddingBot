package life.kaori.bot.plugins.aria2;

import lombok.AllArgsConstructor;

/**
 * @author origin
 */
@AllArgsConstructor
public enum Aria2CommandType {
    //获取已完成
    getGlobalStat("aria2.getGlobalStat"),
    //正在下载
    tellActive("aria2.tellActive"),
    //暂停
    tellWaiting("aria2.tellWaiting"),
    addUri("aria2.addUri");

    final String value;
}
