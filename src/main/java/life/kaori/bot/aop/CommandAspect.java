package life.kaori.bot.aop;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import life.kaori.bot.common.util.BanUtil;
import life.kaori.bot.config.PluginConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author: origin
 */
@Component
@Aspect
public class CommandAspect {

    private BanUtil banUtil;

    @Autowired
    public void setBanUtil(BanUtil banUtil) {
        this.banUtil = banUtil;
    }

    private PluginConfig pluginConfig;

    @Autowired
    public void setPluginConfig(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    /**
     * 群组命令切点
     */
    @Pointcut("execution(* life.kaori.bot.plugins..*.*GA(..)))")
    private void groupPrefixPoint() {
    }

    /**
     * 私人命令切点
     */
    @Pointcut("execution(* life.kaori.bot.plugins..*.*PA(..)))")
    private void privatePrefixPoint() {
    }

    @Around(value = "groupPrefixPoint()")
    public Object groupPrefixCheck(ProceedingJoinPoint pjp) throws Throwable {
        String pluginName = pjp.getTarget().getClass().getSimpleName();
        Object[] args = pjp.getArgs();
        GroupMessageEvent event = getGroupMessageEvent(args);
        Long groupId = event.getGroupId();
        String userId = event.getSender().getUserId();
        if (banUtil.isBan(userId)) {

        }
        if (!pluginConfig.pluginEnableCheck(pluginName, groupId)) {

        }

        return pjp.proceed(args);
    }

    @Around(value = "privatePrefixPoint()")
    public Object privatePrefixCheck(ProceedingJoinPoint pjp) throws Throwable {
        Object[] args = pjp.getArgs();

        return pjp.proceed(args);
    }

    private GroupMessageEvent getGroupMessageEvent(Object[] args) {
        if (args.length >= 2) {
            if (args[1] instanceof GroupMessageEvent) {
                return (GroupMessageEvent) args[1];
            }
        }
        for (Object arg : args) {
            if (arg instanceof GroupMessageEvent) {
                return (GroupMessageEvent) arg;
            }
        }
        return null;
    }

    private PrivateMessageEvent getPrivateMessageEvent(Object[] args) {
        if (args.length >= 2) {
            if (args[1] instanceof PrivateMessageEvent) {
                return (PrivateMessageEvent) args[1];
            }
        }
        for (Object arg : args) {
            if (arg instanceof PrivateMessageEvent) {
                return (PrivateMessageEvent) arg;
            }
        }
        return null;
    }
}
