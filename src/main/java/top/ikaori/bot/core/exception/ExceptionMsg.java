package top.ikaori.bot.core.exception;

/**
 * @author origin
 */
public class ExceptionMsg {
    public static BotException AUTH_ERROR = new BotException("你没有该命令操作权限！");
    public static BotException ARIA2_ERROR = new BotException("任务执行失败");
    public static BotException ARIA2_MAGNET_LINK_ERROR = new BotException("不正确的下载链接");
    public static BotException EPIC_ERROR = new BotException("获取Epic免费游戏失败");
    public static BotException ERROR_PARAMETER = new BotException("请输入正确的参数");
    public static BotException STEAM_GAME_FREE_ERROR = new BotException("该游戏是免费游戏");
}
