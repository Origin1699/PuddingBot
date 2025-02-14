package top.ikaori.bot.plugins.picSearch;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.ikaori.bot.config.BotConfig;
import top.ikaori.bot.plugins.Plugin;
import top.ikaori.bot.plugins.management.ChatModeUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author origin
 */
@Shiro
@Component
@RequiredArgsConstructor
public class PicSearch implements Plugin {

    @Getter
    private final List<String> nickName = List.of("搜图");
    @Getter
    private final String help = """
            """;
    private final ChatModeUtil chatModeUtil;

    private final BotConfig botConfig;

    private final RestTemplate restTemplate;

    private final SauceNao sauceNao;

    private final Ascii2d ascii2d;


    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^(搜图|识图)$")
    public void search(Bot bot, AnyMessageEvent event) {
        chatModeUtil.setChatMode(bot, event, botConfig.getPlugins().getPicSearchConfig().getTimeout(), getName(), "您已经很久没有发送图片啦，帮您退出检索模式了哟～");
        bot.sendMsg(event, "您已经进入搜图模式，请直接发送图片。", false);
    }

    public void chat(Bot bot, AnyMessageEvent event, List<ArrayMsg> images) throws IOException {
        bot.sendMsg(event, MsgUtils.builder().reply(event.getMessageId()).text("正在搜索图片...").build(), false);
        for (ArrayMsg image : images) {
            List<Map<String, Object>> msg = ShiroUtils.generateForwardMsg(search(image));
            bot.sendForwardMsg(event, msg);
        }
    }

    public List<String> search(ArrayMsg image) throws IOException {
        String url = image.getData().get("url");
        String[] split = url.split("-");
        String md5 = split[split.length - 1];
        var search = sauceNao.search(url);
        double similarity = Double.parseDouble(search.getFirst());
        if (similarity > botConfig.getPlugins().getPicSearchConfig().getSimilarity() && !botConfig.getPlugins().getPicSearchConfig().isAlwaysUseAscii2d()) {
            return List.of(buildDefMsg(url), search.getSecond());
        }
        try {
            Pair<String, String> search2 = ascii2d.search(url);
            return List.of(buildDefMsg(url), search.getSecond(), search2.getFirst(), search2.getSecond());
        } catch (ArrayIndexOutOfBoundsException e) {
            return List.of(buildDefMsg(url), search.getSecond(),
                    MsgUtils.builder().text("Ascii2d未检索到相似内容···").build());
        }
    }

    private String buildDefMsg(String url) {
        return MsgUtils.builder().img(url).text("图片查询结果如下:").build();
    }
}
