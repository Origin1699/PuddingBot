package top.ikaori.bot.plugins;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.ikaori.bot.common.util.AuthUtil;
import top.ikaori.bot.common.util.NetUtil;
import top.ikaori.bot.config.Global;
import top.ikaori.bot.core.ExecutorUtil;
import top.ikaori.bot.core.exception.BotException;
import top.ikaori.bot.core.exception.ExceptionMsg;
import top.ikaori.bot.entity.dto.SteamGamePriceDTO;
import top.ikaori.bot.entity.steam.SteamGameEntity;
import top.ikaori.bot.entity.steam.SteamGameSubsEntity;
import top.ikaori.bot.repository.SteamGameRepository;
import top.ikaori.bot.repository.SteamGameSubsRepository;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;

@Component
@Shiro
@Slf4j
@RequiredArgsConstructor
public class SteamGame implements AbstractPlugin {
    @Getter
    private final List<String> nickName = List.of("Steam促销", "Steam打折");
    @Getter
    private final String help = """
            订阅Steam游戏打折
            命令:
            dz bd [appid]     //订阅Steam游玩打折
            dz td [appid]     //退订Steam游玩打折
            """;

    private final AuthUtil authUtil;

    private final Global global;

    private final SteamGameRepository gameRepository;

    private final SteamGameSubsRepository subsRepository;

    private final ObjectMapper objectMapper;

    private static final String gameUrl = "https://store.steampowered.com/api/appdetails?cc=cn&appids=%s";
    private static final String priceUrl = "https://store.steampowered.com/api/appdetails?cc=cn&appids=%s&filters=price_overview";

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(?i)dz\\s+(?<action>bd|td|td all|subs)?\\s?(?<prompt>[\\d]+?)?$")
    public void operate(Bot bot, GroupMessageEvent event, Matcher matcher) {
        ExecutorUtil.exec(bot, event, this.getClass().getSimpleName(), () -> {
            String prompt = matcher.group("prompt");
            String action = matcher.group("action");
            if (!StringUtils.hasText(action) || !StringUtils.hasText(prompt)) {
                if (!StringUtils.hasText(prompt) && "bd".equals(action)) {
                    return;
                }
            }
            switch (action) {
                case "bd" -> bd(bot, event, prompt);
                case "td" -> td(bot, event, prompt);
                case "td all" -> tdAll(bot, event, prompt);
                case "subs" -> subs(bot, event);
            }
        });
    }

    private void tdAll(Bot bot, GroupMessageEvent event, String prompt) {
        int appid = Integer.parseInt(prompt);
        if (!authUtil.groupMasterAuth(event)) {
            throw ExceptionMsg.AUTH_ERROR;
        }
        if (authUtil.masterAuth(event)) {
            subsRepository.deleteByAppid(appid);
            gameRepository.deleteByAppid(appid);
        } else {
            subsRepository.deleteByGroupIdAndAppid(event.getGroupId(), appid);
            List<SteamGameSubsEntity> subapp = subsRepository.findByAppid(appid);
            if (subapp.isEmpty()) {
                gameRepository.deleteByAppid(appid);
            }
        }
        bot.sendGroupMsg(event.getGroupId(), "取消订阅成功", false);
    }

    private void subs(Bot bot, GroupMessageEvent event) {
        Long groupId = event.getGroupId();
        MsgUtils builder = MsgUtils.builder();
        builder.text(event.getSender().getNickname() + "订阅的游戏有:");
        subsRepository.findAll().forEach(e -> {
            Optional<SteamGameEntity> op = gameRepository.findByAppid(e.getAppid());
            op.ifPresent(game -> {
                builder.text(String.format("[%s]%s", game.getAppid(), game.getName()));
            });
        });
        bot.sendGroupMsg(groupId, builder.build(), false);
    }

    private void bd(Bot bot, GroupMessageEvent event, String prompt) {
        Long groupId = event.getGroupId();
        Long userId = event.getSender().getUserId();
        int appid = Integer.parseInt(prompt);
        Optional<SteamGameEntity> op = gameRepository.findByAppid(appid);
        op.ifPresentOrElse(entity -> {
            Optional<SteamGameSubsEntity> subOp = subsRepository.findByGroupIdAndUserIdAndAppid(groupId, userId, appid);
            subOp.ifPresentOrElse(e -> {
                bot.sendGroupMsg(groupId, "你已经订阅该游戏", false);
            }, () -> {
                subGame(appid, groupId, userId);
                bot.sendGroupMsg(groupId, String.format("成功订阅游戏 %s 价格, 当有折扣时会在群里通知.", op.get().getName()), false);
            });
        }, () -> {
            SteamGameEntity entity = new SteamGameEntity();
            entity.setAppid(appid);
            try {
                Response response = NetUtil.get(String.format(gameUrl, prompt));
                String body = response.body().string();
                if ("null".contains(body)) {
                    throw ExceptionMsg.ERROR_PARAMETER;
                }
                JsonNode node = objectMapper.readTree(body);
                JsonNode app = node.get(prompt);
                if (!Boolean.parseBoolean(app.get("success").asText())) {
                    throw ExceptionMsg.ERROR_PARAMETER;
                }
                JsonNode data = app.get("data");
                if (Boolean.parseBoolean(data.get("is_free").asText())) {
                    throw ExceptionMsg.STEAM_GAME_FREE_ERROR;
                }
                String name = data.get("name").asText();
                entity.setName(name);
                JsonNode screenshots = data.get("screenshots");

                JsonNode pic1 = screenshots.get(0);
                entity.setPicUrl(pic1.get("path_thumbnail").asText());
                gameRepository.save(entity);
                subGame(appid, groupId, userId);
                bot.sendGroupMsg(groupId, String.format("成功订阅游戏 %s , 当有折扣时会在群里通知.", name), false);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new BotException(e.getMessage());
            }
        });
    }

    private void subGame(int appid, Long groupId, Long userId) {
        SteamGameSubsEntity entity = new SteamGameSubsEntity();
        entity.setGroupId(groupId).setUserId(userId).setAppid(appid);
        subsRepository.save(entity);
    }

    private void td(Bot bot, GroupMessageEvent event, String prompt) {
        Long groupId = event.getGroupId();
        Long userId = event.getSender().getUserId();
        int appid = Integer.parseInt(prompt);
        Optional<SteamGameEntity> app = gameRepository.findByAppid(appid);
        if (app.isEmpty()) {
            bot.sendGroupMsg(groupId, String.format("未找到id为 %s 的游戏", appid), false);
            return;
        }
        Optional<SteamGameSubsEntity> op = subsRepository.findByGroupIdAndUserIdAndAppid(groupId, userId, appid);
        op.ifPresentOrElse(e -> {
            subsRepository.delete(e);
            List<SteamGameSubsEntity> subapp = subsRepository.findByAppid(appid);
            if (subapp.isEmpty()) {
                gameRepository.deleteByAppid(appid);
            }
            bot.sendGroupMsg(groupId, String.format("取消订阅游戏 %s 成功", app.get().getName()), false);
        }, () -> {
            bot.sendGroupMsg(groupId, String.format("你未订阅游戏: %s ", app.get().getName()), false);
        });
    }

    @Scheduled(cron = "0 0/1 * * * ?", zone = "Asia/Shanghai")
    public void handler() {
        List<SteamGameEntity> games = gameRepository.findAll();
        if (games.isEmpty()) {
            return;
        }
        StringJoiner joiner = new StringJoiner(",");
        games.forEach(game -> joiner.add(game.getAppid() + ""));
        try {
            Response response = NetUtil.get(String.format(priceUrl, joiner));
            if (response.code() != HttpStatus.OK.value()) {
                log.warn(response.message());
            }
            String result = response.body().string();
            JsonNode tree = objectMapper.readTree(result);
            games.forEach(game -> {
                JsonNode jsonNode = tree.get(game.getAppid().toString());
                SteamGamePriceDTO entity = null;
                try {
                    entity = objectMapper.readValue(jsonNode.toString(), SteamGamePriceDTO.class);
                } catch (JsonProcessingException e) {
                    log.error(e.getMessage(), e);
                    return;
                }
                if (entity.getSuccess()) {
                    var data = entity.getData();
                    var priceOverview = data.getPriceOverview();
                    if (priceOverview == null) {
                        return;
                    }
                    game.setInitialFormatted(priceOverview.getInitialFormatted());
                    game.setFinalFormatted(priceOverview.getFinalFormatted());
                    game.setDiscountPercent(priceOverview.getDiscountPercent());
                }
            });
            gameRepository.saveAll(games);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //@Scheduled(cron = "0 0 10,18 * * ?", zone = "Asia/Shanghai")
    @Scheduled(cron = "30 0/1 * * * ?", zone = "Asia/Shanghai")
    public void groupNotify() {
        Map<Long, List<SteamGameEntity>> groupMap = new HashMap<>();
        gameRepository.findAll().forEach(game -> {
            Integer discountPercent = game.getDiscountPercent();
            if (discountPercent != null && discountPercent > 0) {
                List<SteamGameSubsEntity> subList = subsRepository.findByAppid(game.getAppid());
                HashMap<Long, List<SteamGameSubsEntity>> subMap = new HashMap<>();
                subList.forEach(sub -> {
                    List<SteamGameEntity> l = groupMap.computeIfAbsent(sub.getGroupId(), key -> new ArrayList<>());
                    if (!l.contains(game)) {
                        l.add(game);
                    }
                    subMap.computeIfAbsent(sub.getGroupId(), key -> new ArrayList<>()).add(sub);
                });
                atNotify(game, subMap);
            }
        });
        forwardNotify(groupMap);
    }

    public void forwardNotify(Map<Long, List<SteamGameEntity>> groupMap) {
        groupMap.forEach((groupId, games) -> {
            List<String> messages = games.stream().map(game -> {
                return MsgUtils.builder().img(game.getPicUrl())
                        .text("\n").text(String.format("游戏 %s 打折啦!", game.getName()))
                        .text("\n原价 " + game.getInitialFormatted()).text(String.format("  【-%s】", game.getDiscountPercent()))
                        .text("\n现价 " + game.getFinalFormatted()).build();
            }).toList();
            global.bot().sendGroupForwardMsg(groupId, ShiroUtils.generateForwardMsg(messages));
        });
    }

    private void atNotify(SteamGameEntity game, HashMap<Long, List<SteamGameSubsEntity>> subMap) {
        subMap.forEach((groupId, subs) -> {
            Map<Integer, List<Long>> l = new HashMap<>();
            subs.forEach(sub -> {
                if (!sub.isInformed()) {
                    List<Long> longs = l.computeIfAbsent(sub.getAppid(), k -> new ArrayList<>());
                    if (!longs.contains(sub.getUserId())) {
                        longs.add(sub.getUserId());
                    }
                    sub.setInformed(true);
                }
            });
            l.forEach((k, v) -> {
                MsgUtils msgUtils = MsgUtils.builder();
                v.forEach(msgUtils::at);
                msgUtils.text("\n").text(String.format("游戏 %s 打折啦!", game.getName()));
                global.bot().sendGroupMsg(groupId, msgUtils.build(), false);
            });
            subsRepository.saveAll(subs);
        });
    }

}
