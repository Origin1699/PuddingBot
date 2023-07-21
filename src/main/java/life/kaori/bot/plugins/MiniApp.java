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
import life.kaori.bot.common.util.RegexUtils;
import life.kaori.bot.config.Api;
import life.kaori.bot.entity.dto.BiliMiniAppDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;

/**
 * author: origin
 */
@Component
@Shiro
@Slf4j
public class MiniApp {
    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GroupMessageHandler
    public void miniAppParseGA(Bot bot, GroupMessageEvent event) {
        String message = event.getMessage();
        if (!message.contains("com.tencent.miniapp_01")) return;
        if (message.contains("哔哩哔哩")) {
            biliMiniAppParse(bot, event);
        }
    }

    @GroupMessageHandler(cmd = "")
    public void miniAppParse2GA(Bot bot, GroupMessageEvent event, Matcher matcher) {
        String message = event.getMessage();
        if (!message.contains("com.tencent.miniapp_01")) return;
        if (message.contains("哔哩哔哩")) {
            biliMiniAppParse(bot, event);
        }
    }

    @GroupMessageHandler(cmd = RegexConst.B23)
    public void biliUriParseGA(Bot bot, GroupMessageEvent event, Matcher matcher) {
        System.out.println(matcher);
        String group = matcher.group(1);
        System.out.println(group);
    }


    public void biliMiniAppParse(Bot bot, GroupMessageEvent event) {
        try {
            List<ArrayMsg> json = event.getArrayMsg().stream().filter(msg -> msg.getType() == MsgTypeEnum.json).toList();
            String data = json.get(0).getData().get("data");
//            String shortURL = RegexUtils.regexGroup(RegexConst.GET_URL_BV23, data, 1);
            String shortURL = JsonParser.parseString(data).getAsJsonObject().getAsJsonObject("meta").getAsJsonObject("detail_1").get("qqdocurl").getAsString();
            String url = RegexUtils.regexGroup(RegexConst.GET_URL_BVID, restTemplate.postForLocation(shortURL, null).toString(), 1);
            bot.sendGroupMsg(event.getGroupId(), buildBiliMsg(restTemplate.getForObject(Api.BiLI + url, BiliMiniAppDTO.class)), false);
//            System.out.println(buildBiliMsg(restTemplate.getForObject(Api.BiLI + url, BiliMiniAppDTO.class)));
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
    }

    public String buildBiliMsg(BiliMiniAppDTO dto) {
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
}
