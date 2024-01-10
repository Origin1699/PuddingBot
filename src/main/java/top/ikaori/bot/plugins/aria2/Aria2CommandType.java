package top.ikaori.bot.plugins.aria2;

import lombok.AllArgsConstructor;

/**
 * @author origin
 */
@AllArgsConstructor
public enum Aria2CommandType {
    //获取已完成
    getGlobalStat("aria2.getGlobalStat"),
    //获取正在下载任务
    tellActive("aria2.tellActive"),
    //获取已暂停任务
    tellWaiting("aria2.tellWaiting"),
    //新建
    addUri("aria2.addUri"),
    //取消暂停
    unpause("aria2.unpause"),
    //强制暂停
    forcePause("aria2.forcePause");

    final String value;
}
