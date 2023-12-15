package life.kaori.bot.common.constant;

import life.kaori.bot.core.exception.BotException;
import lombok.Data;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * author: origin
 */
@Data
public class BotStrings {

    private String name;
    private int seq;
    private String defaultMessage;
    private static final Map<String, BotStrings> BOT_STRINGS_MAP = new HashMap<String, BotStrings>();
    private static ReloadableResourceBundleMessageSource messageSource = createMessageSource();

    public BotStrings(String defaultString) {
        this.defaultMessage = defaultString;
    }

    public static final BotStrings PLUGIN = new BotStrings("插件");
    public static final BotStrings OPEN = new BotStrings("开启");
    public static final BotStrings CLOSE = new BotStrings("关闭");
    public static final BotStrings PLUGIN_OPERATE = new BotStrings("插件 {0}");
    public static final BotStrings PLUGIN_ENABLE_USE = new BotStrings("插件 {0} 未开启");

    public static final BotStrings RESOURCE_FILE_NOT_EXISTS = new BotStrings("资源 {} 文件不存在");
    public static final BotStrings RESOURCE_FILE_PARSE_ERROR = new BotStrings("资源文件错误或格式不对");
    public static final BotStrings PLUGIN_FIND_ERROR = new BotStrings("未找到插件 {0}");


    public BotException exception(Object... args) {
        return BotException.create(this, args);
    }

    static {
        AtomicInteger count = new AtomicInteger(100);
        ReflectionUtils.doWithFields(BotStrings.class, f -> {
            int mod = f.getModifiers();
            if (f.getType() == BotStrings.class && Modifier.isPublic(mod) && Modifier.isStatic(mod) && Modifier.isFinal(mod)) {
                BotStrings strings = (BotStrings) f.get(BotStrings.class);
                strings.name = f.getName();
                strings.seq = count.incrementAndGet();
                BOT_STRINGS_MAP.put(strings.name, strings);
            }
        });
    }

    private static ReloadableResourceBundleMessageSource createMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding(Constant.DEFAULT_ENCODING);
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setDefaultLocale(Locale.CHINESE);
        return messageSource;
    }

    public String getMessage(Object... args) {
        return messageSource.getMessage(name, args, defaultMessage, Locale.CHINESE);
    }
}
