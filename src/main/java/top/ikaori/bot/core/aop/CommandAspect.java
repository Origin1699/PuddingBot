package top.ikaori.bot.core.aop;

import com.mikuac.shiro.constant.ActionParams;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import top.ikaori.bot.common.util.AuthUtil;
import top.ikaori.bot.common.util.BanUtil;
import top.ikaori.bot.common.util.MessageUtil;
import top.ikaori.bot.core.exception.BotException;
import top.ikaori.bot.plugins.management.PluginManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static top.ikaori.bot.common.CommonUtil.*;

/**
 * @author origin
 */
@Component
@Aspect
@Slf4j
public class CommandAspect {

    private BanUtil banUtil;

    @Autowired
    public void setBanUtil(BanUtil banUtil) {
        this.banUtil = banUtil;
    }

    private AuthUtil authUtil;

    @Autowired
    public void setAuthUtil(AuthUtil authUtil) {
        this.authUtil = authUtil;
    }

    private PluginManager pluginManager;

    @Autowired
    public void setPluginManager(PluginManager pluginManager) {
        this.pluginManager = pluginManager;
    }

    @Pointcut("@annotation(com.mikuac.shiro.annotation.GroupMessageHandler)")
    private void groupPoint() {
    }

    @Pointcut("@annotation(com.mikuac.shiro.annotation.PrivateMessageHandler)")
    private void privatePoint() {
    }

    @Pointcut("@annotation(com.mikuac.shiro.annotation.AnyMessageHandler)")
    private void anyPoint() {
    }

    @Around(value = "groupPoint()")
    public void groupPrefixCheck(ProceedingJoinPoint pjp) throws Throwable {
        String pluginName = pjp.getTarget().getClass().getSimpleName();
        GroupMessageEvent event = (GroupMessageEvent) getMessageEvent(pjp.getArgs());
        Bot bot = getBot(pjp.getArgs());
        try {
            Long groupId = event.getGroupId();
            Long userId = event.getSender().getUserId();
            if (!pluginManager.groupReply(groupId, pluginName) && !banUtil.isBan(userId))
                return;
            pjp.proceed(pjp.getArgs());
        } catch (BotException botException) {
            MessageUtil.sendMsg(bot, event, botException.getMessage());
        } catch (Exception e) {
            MessageUtil.sendMsg(bot, event, "插件 " + pluginName + " 执行失败, 请联系管理员查看后台日志。");
            log.error(e.getMessage(), e);
        }

    }

    @Around(value = "privatePoint()")
    public void privatePrefixCheck(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();
        authUtil.masterCheck(getMessageEvent(args));
        pjp.proceed(args);
    }

    @Around(value = "anyPoint()")
    public void AnyPrefixCheck(ProceedingJoinPoint pjp) throws Throwable {
        MessageEvent event = getMessageEvent(pjp.getArgs());
        switch (event.getMessageType()) {
            case ActionParams.PRIVATE -> privatePrefixCheck(pjp);
            case ActionParams.GROUP -> groupPrefixCheck(pjp);
        }
    }
}
