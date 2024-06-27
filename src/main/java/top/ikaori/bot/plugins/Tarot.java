package top.ikaori.bot.plugins;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.Getter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import top.ikaori.bot.common.constant.BotStrings;
import top.ikaori.bot.config.BotConfig;
import top.ikaori.bot.core.EnableCleanMap;

import javax.annotation.PostConstruct;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author origin
 */
@Component
@Shiro
@EnableCleanMap
public class Tarot implements Plugin {
    @Getter
    private final String name = this.getClass().getSimpleName();
    @Getter
    private final List<String> nickName = List.of("塔罗牌", "占卜");
    @Getter
    private final String help = """
            帮助
            """;

    private ExpiringMap expiringMap;

    private final BotConfig config;

    @PostConstruct
    private void init() throws UnsupportedEncodingException {
        expiringMap = ExpiringMap.builder().
                variableExpiration().
                expirationPolicy(ExpirationPolicy.CREATED).
                expiration(config.getPlugins().getTarotConfig().getCd(), TimeUnit.SECONDS)
                .build();
        Yaml yaml = new Yaml();
//        HashMap<String, Object>  load = yaml.load(new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("tarot.yaml"), "UTF-8")));
    }

    @Autowired
    public Tarot(BotConfig config) {
        this.config = config;
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^(塔罗牌|占卜)$")
    public void divination(Bot bot, AnyMessageEvent event) {
        var userId = event.getUserId();
        if (expiringMap.get(userId) != null) {
            throw BotStrings.PLUGIN_CD_ERROR.exception(nickName.get(0), expiringMap.getExpiration(userId));
        }
    }

    @Override
    public void cleanExpiringMap() {
        expiringMap.clear();
    }

}
