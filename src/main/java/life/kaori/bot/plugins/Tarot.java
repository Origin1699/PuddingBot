package life.kaori.bot.plugins;

import com.mikuac.shiro.annotation.common.Shiro;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author origin
 */
@Component
@Shiro
public class Tarot implements Plugin {
    @Getter
    private final String name = this.getClass().getSimpleName();
    @Getter
    private final List<String> nickName = List.of("塔罗牌", "占卜");
    @Getter
    private final String help = """
            帮助
            """;
}
