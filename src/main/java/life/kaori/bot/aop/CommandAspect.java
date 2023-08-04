package life.kaori.bot.aop;

import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import life.kaori.bot.common.util.BanUtil;
import life.kaori.bot.config.PluginConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static life.kaori.bot.common.CommonUtil.*;

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
     * 插件命令切点
     */
    @Pointcut("execution(* life.kaori.bot.plugins..*.*(..)))")
    private void groupPrefixPoint() {
    }
    /**
     * 部分排除插件命令切点
     */
    @Pointcut("execution(* life.kaori.bot.plugins.internal..*(..))")
    private void internalPrefixPoint() {
    }

    @Around(value = "groupPrefixPoint() && !internalPrefixPoint()")
    public Object groupPrefixCheck(ProceedingJoinPoint pjp) throws Throwable {
        String pluginName = pjp.getTarget().getClass().getSimpleName();
        Object[] args = pjp.getArgs();
        if (checkAuth(getMessageEvent(args), pluginName)) {
            return pjp.proceed(args);
        }
        return pjp.proceed(args);
    }

    private boolean checkAuth(MessageEvent messageEvent, String pluginName) {
        if (messageEvent instanceof GroupMessageEvent event) {
            Long groupId = event.getGroupId();
            String userId = event.getSender().getUserId();
            if (banUtil.isBan(userId)) {
                return false;
            }
            if (!pluginConfig.pluginEnableCheck(pluginName, groupId)) {
                return false;
            }
            return true;
        } else if (messageEvent instanceof PrivateMessageEvent event) {
            return true;
        }
        return false;
    }
}
