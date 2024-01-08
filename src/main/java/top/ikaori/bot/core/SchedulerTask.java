package top.ikaori.bot.core;

import top.ikaori.bot.plugins.Plugin;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;

@Configuration
@EnableScheduling
public class SchedulerTask {


    private ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Scheduled(cron = "0 0 0 ? * ? ")
    public void Task() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(EnableCleanMap.class);
        for (Object bean : beans.values()) {
            try {
                Plugin plugin = (Plugin) bean;
                plugin.cleanExpiringMap();
            } catch (Throwable t){

            }

        }
    }
}
