package top.ikaori.bot.common;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import top.ikaori.bot.common.constant.BotStrings;
import top.ikaori.bot.core.exception.BotException;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * @author origin
 */
public class CommonUtil {

    private static final String FILE_PREFIX = "file:///";

    public static String jarDir;
    public static File resourceDir;
    public static RandomGenerator randomGenerator;

    static {
        ApplicationHome home = new ApplicationHome(CommonUtil.class.getClass());
        File dir = home.getDir();
        jarDir = dir.toString();
        resourceDir = new File(jarDir, "resources");
        RandomGeneratorFactory<RandomGenerator> l128X256MixRandom = RandomGeneratorFactory.of("L128X256MixRandom");
        // 使用时间戳作为随机数种子
        randomGenerator = l128X256MixRandom.create(System.currentTimeMillis());
    }

    public static File getPluginResourceDir(String pluginName) {
        return new File(resourceDir, pluginName);
    }

    public static File getResourceFile(String filename) {
        File file = new File(resourceDir, filename);
        if (file.exists()) {
            return file;
        } else {
            throw BotException.create(BotStrings.RESOURCE_FILE_NOT_EXISTS, filename);
        }
    }

    public static File getRandomFile(File file) {
        File[] list = file.listFiles();
        if (list != null && list.length > 0) {
            return list[randomGenerator.nextInt(list.length)];
        }
        throw new RuntimeException();
    }

    public static String getCurrentDate() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public static String imgPathCover(File file) {
        return FILE_PREFIX + file.getAbsolutePath().replace("\\", "/");
    }

    public static GroupMessageEvent getGroupMessageEvent(Object[] args) {
        return getMessageEvent(args, GroupMessageEvent.class);
    }

    public static PrivateMessageEvent getPrivateMessageEvent(Object[] args) {
        return getMessageEvent(args, PrivateMessageEvent.class);
    }

    public static AnyMessageEvent getAnyMessageEvent(Object[] args) {
        return getMessageEvent(args, AnyMessageEvent.class);
    }

    public static <T> T getMessageEvent(Object[] args, Class<T> type) {
        for (Object obj : args) {
            if (type.isInstance(obj)) {
                return type.cast(obj);
            }
        }
        return null;
    }

    public static MessageEvent getMessageEvent(Object[] args) {
        for (Object obj : args) {
            if (obj instanceof MessageEvent event)
                return event;
        }
        return null;
    }

    public static Bot getBot(Object[] args) {
        for (Object obj : args) {
            if (obj instanceof Bot bot)
                return bot;
        }
        return null;
    }

}
