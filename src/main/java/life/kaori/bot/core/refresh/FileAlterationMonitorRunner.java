package life.kaori.bot.core.refresh;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 *
 * author: origin 2023/7/7 14:37
 */
@Component
public class FileAlterationMonitorRunner implements CommandLineRunner {
    /* file:./; file:./config/; */
    private static final List<String> DEFAULT_EXTERNAL_CONFIG_FILE_LOCATIONS = Arrays.asList("file:./", "file:./config/");
    /* config file suffix */
    private static final String[] CONFIG_FILE_SUFFIX = new String[]{".yml", ".yaml", ".properties"};

    private ConfigFileAlterationListener configFileAlterationListener;

    public FileAlterationMonitorRunner(ConfigFileAlterationListener configFileAlterationListener) {
        this.configFileAlterationListener = configFileAlterationListener;
    }

    @Override
    public void run(String... args) throws Exception {
        // yml 或 properties 文件
        SuffixFileFilter filter = new SuffixFileFilter(CONFIG_FILE_SUFFIX);
        // 创建一个监视线程，以指定的间隔触发任何已注册的FileAlternationObserver。
        FileAlterationMonitor monitor = new FileAlterationMonitor(2000);
        for (String configFileLocation : DEFAULT_EXTERNAL_CONFIG_FILE_LOCATIONS) {
            // 当前目录下的外部配置文件
            File file = ResourceUtils.getFile(configFileLocation);
            // 创建文件修改观察者
            FileAlterationObserver observer = new FileAlterationObserver(file, filter);
            // 给观察者添加监听器
            observer.addListener(configFileAlterationListener);
            // 给监视线程创建观察者
            monitor.addObserver(observer);
            // 启动监视线程
        }
        monitor.start();
    }
}
