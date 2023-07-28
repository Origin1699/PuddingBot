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

import java.util.List;

/**
 * author: origin
 */
@Component
@Shiro
@Slf4j
public class MiniApp {
    private RestTemplate restTemplate;

    @GroupMessageHandler
    public void miniAppParseGA(Bot bot, GroupMessageEvent event) {
        String message = event.getMessage();
        if (!message.contains("com.tencent.miniapp_01")) return;
        if (message.contains("哔哩哔哩")) {
            biliMiniAppParse(bot, event);
        }
    }

    private void biliMiniAppParse(Bot bot, GroupMessageEvent event) {
        try {
            List<ArrayMsg> json = event.getArrayMsg().stream().filter(msg -> msg.getType() == MsgTypeEnum.json).toList();
            String data = json.get(0).getData().get("data");
            String shortURL = JsonParser.parseString(data).getAsJsonObject().getAsJsonObject("meta").getAsJsonObject("detail_1").get("qqdocurl").getAsString();
            String url = RegexUtils.regexGroup(RegexConst.GET_URL_BVID, restTemplate.postForLocation(shortURL, null).toString(), 1);
            bot.sendGroupMsg(event.getGroupId(), buildBiliMsg(restTemplate.getForObject(Api.BiLI + url, BiliMiniAppDTO.class)), false);
        } catch (Throwable throwable) {
            log.error(throwable.getMessage(), throwable);
        }
    }

    private String buildBiliMsg(BiliMiniAppDTO dto) {
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
