package top.ikaori.bot.plugins;

import com.google.gson.JsonParser;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.ikaori.bot.common.constant.Api;
import top.ikaori.bot.common.constant.RegexConst;
import top.ikaori.bot.common.util.AssertUtil;
import top.ikaori.bot.common.util.RegexUtils;
import top.ikaori.bot.core.ExecutorUtil;
import top.ikaori.bot.entity.dto.BiliMiniAppDTO;

import java.util.List;
import java.util.regex.Matcher;

/**
 * @author origin
 */
@Component
@Shiro
@Slf4j
public class MiniApp implements Plugin {
    private RestTemplate restTemplate;
    @Getter
    private final String name = this.getClass().getSimpleName();
    @Getter
    private final List<String> nickName = List.of("小程序解析");
    @Getter
    private final String help = """
            自动解析BiliBili小程序
            命令: https://b23.tv/xxxxxx
            """;

    @GroupMessageHandler
    public void miniAppParse(Bot bot, GroupMessageEvent event) {
        String message = event.getMessage();
        if (!message.contains("com.tencent.miniapp_01")) return;
        if (message.contains("哔哩哔哩")) {
            biliMiniAppParse(bot, event);
        }
    }
    private final String prefix = "https://%s";
    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "(?s).*(b23.tv/\\w+).*")
    public void parseShortUrl(Bot bot, GroupMessageEvent event, Matcher matcher) {
        ExecutorUtil.exec(bot, event, name, () -> {
            String group = matcher.group(1);
            String bv = getBv(String.format(prefix, group));
            bot.sendGroupMsg(event.getGroupId(), buildBiliMsg(bv), false);
        });
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "(?s).*/(?<BVId>BV\\w+).*")
    public void parseUrl(Bot bot, GroupMessageEvent event, Matcher matcher) {
        ExecutorUtil.exec(bot, event, name, () -> {
            String bv = matcher.group(1);
            bot.sendGroupMsg(event.getGroupId(), buildBiliMsg(bv), false);
        });
    }

    private void biliMiniAppParse(Bot bot, GroupMessageEvent event) {
        ExecutorUtil.exec(bot, event, name, () -> {
            List<ArrayMsg> json = event.getArrayMsg().stream().filter(msg -> msg.getType() == MsgTypeEnum.json).toList();
            String data = json.get(0).getData().get("data");
            String shortURL = JsonParser.parseString(data).getAsJsonObject().getAsJsonObject("meta").getAsJsonObject("detail_1").get("qqdocurl").getAsString();
            String bv = getBv(shortURL);
            bot.sendGroupMsg(event.getGroupId(), buildBiliMsg(bv), false);
        });
    }

    private String getBv(String shortUrl) {
        return RegexUtils.regexGroup(RegexConst.GET_URL_BVID, restTemplate.postForLocation(shortUrl, null).toString(), 1);
    }

    private String buildBiliMsg(String bv) {
        BiliMiniAppDTO dto = restTemplate.getForObject(Api.BiLI + bv, BiliMiniAppDTO.class);
        AssertUtil.isNull(dto, "");
        var data = dto.getData();
        var stat = data.getStat();
        var owner = data.getOwner();
        return MsgUtils.builder()
                .img(data.getPic())
                .text(data.getTitle())
                .text("\nUP: " + owner.getName())
                .text("\n观看: " + stat.getView() + "  弹幕: " + stat.getDanmaku())
                .text("\nhttps://www.bilibili.com/video/" + data.getBvid())
                .build();
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
