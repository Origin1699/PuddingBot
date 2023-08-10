package life.kaori.bot.plugins;

import com.google.gson.JsonParser;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import com.mikuac.shiro.model.ArrayMsg;
import life.kaori.bot.common.constant.RegexConst;
import life.kaori.bot.common.util.AssertUtil;
import life.kaori.bot.common.util.RegexUtils;
import life.kaori.bot.common.constant.Api;
import life.kaori.bot.core.ExecutorUtil;
import life.kaori.bot.entity.dto.BiliMiniAppDTO;
import life.kaori.bot.core.PluginManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;

/**
 * author: origin
 */
@Component
@Shiro
@Slf4j
public class MiniApp implements PluginManage {
    private RestTemplate restTemplate;

    private final String name = this.getClass().getSimpleName();
    private final String nickName = "小程序解析";
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

    @GroupMessageHandler(cmd = "^https?://b23.tv/([A-Za-z1-9]+)")
    public void parseShortUrl(Bot bot, GroupMessageEvent event, Matcher matcher) {
        ExecutorUtil.exec(bot, event, name, () -> {
            String shortURL = matcher.group();
            shortURL = shortURL.replace("http:", "https:");
            String bv = getBv(shortURL);
            bot.sendGroupMsg(event.getGroupId(), buildBiliMsg(bv), false);
        });
    }

    @GroupMessageHandler(cmd = "https?://[w]{0,3}\\.?bilibili.com/video/([A-Za-z0-9]+)")
    public void parseUrl(Bot bot, GroupMessageEvent event, Matcher matcher) {
        ExecutorUtil.exec(bot, event, name, () -> {
            String shortURL = matcher.group();
            shortURL = shortURL.replace("http:", "https:");
            String bv = getBv(shortURL);
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
