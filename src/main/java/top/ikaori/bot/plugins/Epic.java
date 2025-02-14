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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.ikaori.bot.core.ExecutorUtil;
import top.ikaori.bot.core.exception.ExceptionMsg;
import top.ikaori.bot.entity.dto.EpicDTO;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author origin
 */
@Getter
@Component
@Shiro
public class Epic implements Plugin {

    private final List<String> nickName = List.of("epic周免");
    private final String help = """
            发送 epic周免 查询免费游戏
            """;
    private final ExpiringMap expiringMap = ExpiringMap.builder().
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
    @MessageHandlerFilter(cmd = "^(epic){0,1}(周免){0,1}$")
    public void free(Bot bot, AnyMessageEvent event) {
        ExecutorUtil.exec(bot, event, getName(), () -> {
            List<Map<String, Object>> msg = ShiroUtils.generateForwardMsg(buildMsg(getFreeGame()));
            bot.sendForwardMsg(event, msg);
        });
    }

    private List<String> buildMsg(EpicDTO freeGame) {
        var elements = Optional.ofNullable(freeGame)
                .map(EpicDTO::getData)
                .map(EpicDTO.Data::getCatalog)
                .map(EpicDTO.Catalog::getSearchStore)
                .map(EpicDTO.SearchStore::getElements)
                .orElse(Collections.emptyList());

        if (elements.isEmpty()) {
            return List.of("未查询到免费游戏。");
        }

        ArrayList<String> msgList = new ArrayList<>();
        elements.forEach(game -> {
            var gameName = Optional.ofNullable(game.getTitle()).orElse("Unknown Game");
            var gameCorp = Optional.ofNullable(game.getSeller()).map(EpicDTO.Seller::getName).orElse("Unknown Seller");
            var gameThumbnail = game.getKeyImages().stream().findFirst().map(EpicDTO.KeyImage::getUrl).orElse("");
            var gamePrice = Optional.ofNullable(game.getPrice())
                    .map(EpicDTO.Price::getTotalPrice)
                    .map(EpicDTO.TotalPrice::getOriginalPrice)
                    .orElse(0);

            var gamePromotions = Optional.ofNullable(game.getPromotions())
                    .map(EpicDTO.Promotions::getPromotionalOffers)
                    .orElse(Collections.emptyList());

            var upcomingPromotions = Optional.ofNullable(game.getPromotions())
                    .map(EpicDTO.Promotions::getUpcomingPromotionalOffers)
                    .orElse(Collections.emptyList());

            if (gamePromotions.isEmpty() && !upcomingPromotions.isEmpty()) {
                handleUpcomingPromotion(game, gameName, gamePrice, gameCorp, gameThumbnail, upcomingPromotions, msgList);
            } else {
                handleActivePromotion(game, gameName, gamePrice, gameCorp, gameThumbnail, msgList);
            }
        });

        return msgList;
    }

    private void handleUpcomingPromotion(EpicDTO.Element game, String gameName, int gamePrice, String gameCorp,
                                         String gameThumbnail, List<EpicDTO.PromotionalOfferContainer> upcomingPromotions,
                                         List<String> msgList) {
        upcomingPromotions.stream()
                .findFirst()
                .flatMap(container -> container.getPromotionalOffers().stream().findFirst())
                .ifPresent(promotionData -> {
                    var startDate = formatDate(promotionData.getStartDate());
                    var endDate = formatDate(promotionData.getEndDate());
                    var msg = MsgUtils.builder().img(gameThumbnail)
                            .text("\n" + gameName + " (" + gamePrice + ") 即将在 " + startDate + " 推出免费游玩，预计截止时间为 " + endDate + " ，该游戏由 " + gameCorp + " 发行。")
                            .build();
                    msgList.add(msg);
                });
    }

    private void handleActivePromotion(EpicDTO.Element game, String gameName, int gamePrice, String gameCorp,
                                       String gameThumbnail, List<String> msgList) {
        var gameDesc = Optional.ofNullable(game.getDescription()).orElse("No description available");

        var publisher = game.getCustomAttributes().stream()
                .filter(it -> "publisherName".equals(it.getKey()))
                .findFirst()
                .map(EpicDTO.CustomAttribute::getValue)
                .orElse(gameCorp);

        var developer = game.getCustomAttributes().stream()
                .filter(it -> "developerName".equals(it.getKey()))
                .findFirst()
                .map(EpicDTO.CustomAttribute::getValue)
                .orElse(gameCorp);

        var endDate = Optional.ofNullable(game.getPromotions())
                .flatMap(promo -> promo.getPromotionalOffers().stream().findFirst())
                .flatMap(container -> container.getPromotionalOffers().stream().findFirst())
                .map(offer -> formatDate(offer.getEndDate()))
                .orElse("Unknown end date");

        Optional<EpicDTO.CatalogNs> catalogNs = Optional.ofNullable(game.getCatalogNs());

        var gamePage = "https://store.epicgames.com";
        if (!catalogNs.isEmpty()) {
            gamePage = Optional.ofNullable(catalogNs.get().getMappings())
                    .flatMap(mappings -> mappings.stream().findFirst())
                    .map(mapping -> "https://store.epicgames.com/fr/p/" + mapping.getPageSlug())
                    .orElse("https://store.epicgames.com");
        }

        var msg = MsgUtils.builder().img(gameThumbnail)
                .text("\n" + gameName + " ( " + gamePrice + ") 当前免费，" + endDate + "截止。")
                .text("\n\n " + gameDesc)
                .text("\n\n该游戏由 " + developer + " 制作，并由 " + publisher + " 发行。")
                .text("\n\n感兴趣的小伙伴可以点击下方链接免费领取啦～")
                .text("\n" + gamePage)
                .build();
        msgList.add(msg);
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
