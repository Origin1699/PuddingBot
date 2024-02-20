package top.ikaori.bot.plugins;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import lombok.Getter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.ikaori.bot.core.ExecutorUtil;
import top.ikaori.bot.core.exception.ExceptionMsg;
import top.ikaori.bot.entity.dto.EpicDTO;


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author origin
 */
@Getter
@Component
@Shiro
public class Epic implements Plugin {

    private String name = this.getClass().getSimpleName();
    private List<String> nickName = List.of("epic周免");
    private String help = """
            发送 epic周免 查询免费游戏
            """;
    private ExpiringMap expiringMap = ExpiringMap.builder().
            variableExpiration().
            expirationPolicy(ExpirationPolicy.CREATED).
            expiration(1, TimeUnit.HOURS)
            .build();

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    public Epic(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    private final String url = "https://store-site-backend-static-ipv4.ak.epicgames.com/freeGamesPromotions?locale={locale}&country={country}&allowCountries={allowCountries}";


    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^(epic)?周免")
    public void free(Bot bot, AnyMessageEvent event) {
        ExecutorUtil.exec(bot, event, name, () -> {
            List<Map<String, Object>> msg = ShiroUtils.generateForwardMsg(buildMsg(getFreeGame()));
            bot.sendForwardMsg(event, msg);
        });
    }

    private List<String> buildMsg(EpicDTO freeGame) {
        var list = freeGame.getData().getCatalog().getSearchStore().getElements();
        if (list.size() == 0) {
            return List.of("未查询到免费游戏。");
        }
        ArrayList<String> msgList = new ArrayList<>();
        list.stream().forEach(game -> {
            var gameName = game.getTitle();
            var gameCorp = game.getSeller().getName();
            var gameThumbnail = "";
            if (!game.getKeyImages().isEmpty()) gameThumbnail = game.getKeyImages().get(0).getUrl();
            var gamePrice = game.getPrice().getTotalPrice().getFmtPrice().getOriginalPrice();
            try {
                var promotions = game.getPromotions();
                var gamePromotions = promotions.getPromotionalOffers();
                var upcomingPromotions = promotions.getUpcomingPromotionalOffers();
                if (gamePromotions.isEmpty() && !upcomingPromotions.isEmpty()) {
                    // Promotion is not active yet, but will be active soon.
                    var promotionData = upcomingPromotions.get(0).getPromotionalOffers().get(0);
                    var startDate = formatDate(promotionData.getStartDate());
                    var endDate = formatDate(promotionData.getEndDate());
                    var msg = MsgUtils.builder().img(gameThumbnail)
                            .text("\n" + gameName + " (" + gamePrice + ") 即将在 " + startDate + " 推出免费游玩，预计截止时间为 " + endDate + " ，该游戏由 " + gameCorp + " 发行。")
                            .build();
                    msgList.add(msg);
                } else {
                    var gameDesc = game.getDescription();

                    var publisherName = game.getCustomAttributes().stream().filter(it -> it.getKey().equals("publisherName")).toList();

                    var publisher = "";
                    if (!publisherName.isEmpty())
                        publisher = publisherName.get(0).getValue();
                    else {
                        publisher = gameCorp;
                    }
                    var developerName = game.getCustomAttributes().stream().filter(it -> it.getKey().equals("developerName")).toList();
                    var developer = "";
                    if (!developerName.isEmpty()) {
                        developer = developerName.get(0).getValue();
                    } else {
                        developer = gameCorp;
                    }
                    var endDate = formatDate(game.getPromotions().getPromotionalOffers().get(0).getPromotionalOffers().get(0).getEndDate());

                    var gamePage = "https://store.epicgames.com/fr/p/" + game.getCatalogNs().getMappings().get(0).getPageSlug();

                    var msg = MsgUtils.builder().img(gameThumbnail)
                            .text("\n" + gameName + " ( " + gamePrice + ") 当前免费，" + endDate + "截止。").text("\n\n " + gameDesc)
                            .text("\n\n该游戏由 " + developer + " 制作，并由 " + publisher + " 发行。")
                            .text("\n\n感兴趣的小伙伴可以点击下方链接免费领取啦～").text("\n" + gamePage).build();
                    msgList.add(msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return msgList;
    }

    private String formatDate(String rawDate) {
        var date = DateUtil.parse(rawDate, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        date = DateUtil.offset(date, DateField.HOUR, 8);
        return DateUtil.format(date, "yyy年MM月dd日 HH时mm分");
    }

    private EpicDTO getFreeGame() throws JsonProcessingException {
        var epic = (EpicDTO) expiringMap.get("epic");
        if (epic != null) {
            return epic;
        }

        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("locale", "zh-CN");
        parameter.put("country", "CN");
        parameter.put("allowCountries", "CN");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8));
        headers.set("Referer", "https://www.epicgames.com/store/zh-CN/");
        headers.set("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.80 Safari/537.36");

        HttpEntity<Object> request = new HttpEntity<>(headers);
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, request, String.class, parameter);

        if (resp.getStatusCode() == HttpStatus.OK) {
            return objectMapper.readValue(resp.getBody(), EpicDTO.class);
        }
        throw ExceptionMsg.EPIC_ERROR;
    }
}
