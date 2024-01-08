package top.ikaori.bot.core.refresh;

import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 *
 * @author origin 2023/7/7 14:38
 */
@Component
public class ConfigFileAlterationListener extends FileAlterationListenerAdaptor {

    private EnvironmentRefresher environmentRefresher;

    public ConfigFileAlterationListener(EnvironmentRefresher environmentRefresher) {
        this.environmentRefresher = environmentRefresher;
    }

    /**
     * File changed Event.
     *
     * @param file The file changed (ignored)
     */
    @Override
    public void onFileChange(final File file) {
        // file 在这用不到, 读取所有配置文件
        environmentRefresher.refreshEnvironment();
    }
}
