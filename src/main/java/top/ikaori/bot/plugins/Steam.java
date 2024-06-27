package top.ikaori.bot.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import top.ikaori.bot.config.BotConfig;
import top.ikaori.bot.core.ExecutorUtil;
import top.ikaori.bot.entity.dto.SteamDTO;
import top.ikaori.bot.entity.steam.SteamEntity;
import top.ikaori.bot.repository.SteamRepository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.regex.Matcher;

@Shiro
@Component
public class Steam implements Plugin {

    @Getter
    private final List<String> nickName = List.of("Steam");
    @Getter
    private final String help = """
            康康群友Steam正在玩什么
            命令:
            steam bd [Steam 64位ID]  //订阅Steam游玩状态
            steam td    //退订Steam游玩状态
            steam subs  //查看Steam订阅
            """;

    private ObjectMapper objectMapper;

    @Resource
    private BotContainer botContainer;

    private Bot bot;

    private RestTemplate template;

    private BotConfig.Plugins.SteamConfig config;

    private BotConfig.Base base;

    private SteamRepository repository;

    private Map<Long, Map<String, SteamEntity>> subMap = new HashMap<>();
    private final Map<String, String> playGame = new HashMap<>();
    private final Map<String, LocalDateTime> playTime = new HashMap<>();

    private final String STEAM_URI = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=%s&steamids=%s";


    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(?i)steam\\s+(?<action>bd|td|subs)?\\s?(?<prompt>[\\s\\S]+?)?$")
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
                case "td" -> td(bot, event);
                case "subs" -> subs(bot, event);
            }
        });

    }

    private void td(Bot bot, GroupMessageEvent event) {
        Long groupId = event.getGroupId();
        repository.deleteByGroupIdAndUserId(groupId, event.getSender().getUserId());
        bot.sendGroupMsg(groupId, "取消订阅成功", false);
        update();
    }

    private void bd(Bot bot, GroupMessageEvent event, String prompt) {

        Long groupId = event.getGroupId();
        Optional<SteamEntity> op = repository.findByGroupIdAndUserId(groupId, event.getSender().getUserId());

        if (op.isEmpty()) {
            SteamEntity steamEntity = new SteamEntity(null, groupId, event.getSender().getUserId(), prompt, event.getSender().getNickname());
            repository.save(steamEntity);
            bot.sendGroupMsg(groupId, "订阅成功", false);
            update();
        } else {
            bot.sendGroupMsg(groupId, "该Steam账户已订阅", false);
        }
    }

    private void subs(Bot bot, GroupMessageEvent event) {
        Long groupId = event.getGroupId();
        List<SteamEntity> list = repository.findByGroupId(groupId);
        if (list.isEmpty()) {
            bot.sendGroupMsg(groupId, "暂无 Steam 订阅", false);
        } else {
            MsgUtils builder = MsgUtils.builder().text("Steam 订阅:");
            list.forEach(steamEntity -> {
                builder.text(String.format("\n[%s]%s", steamEntity.getNickname(), steamEntity.getSteamId()));
            });
            bot.sendGroupMsg(groupId, builder.build(), false);
        }
    }


    private void getStatus(Long groupId, Map<String, SteamEntity> groupMap) {
        Bot bot = getBot();
        StringJoiner joiner = new StringJoiner(",");
        groupMap.forEach((k, v) -> {
            joiner.add(k);
        });

        ResponseEntity<SteamDTO> response = template.getForEntity(String.format(STEAM_URI, config.getApikey(), joiner), SteamDTO.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            return;
        }
        SteamDTO dto = response.getBody();
        Optional<SteamDTO.Response> optional = Optional.ofNullable(dto.getResponse());
        if (optional.isEmpty()) {
            return;
        }
        optional.get().getPlayers().forEach(player -> {
            String steamid = player.getSteamid();
            String gameextrainfo = player.getGameextrainfo();

            Optional<SteamEntity> op = Optional.ofNullable(groupMap.get(steamid));
            if (op.isEmpty()) {
                //查询steam的id对不上订阅qq
                //需要对上吗?
                return;
            }

            Long userId = op.get().getUserId();

            String key = String.format("%s%s", groupId, userId);

            String game = playGame.get(key);
            LocalDateTime time = playTime.get(key);


            //判断当前steam账户是否在玩游戏
            if (StringUtils.hasText(gameextrainfo)) {
                //在玩游戏
                //判断是有有在玩游戏记录
                if (StringUtils.hasText(game)) {
                    //有正在游玩记录
                    if (game.equals(gameextrainfo)) {
                        // 在玩通一个游戏
                    } else {
                        //监控时间内换了其他游戏
                        bot.sendGroupMsg(groupId, String.format("%s不玩 %s 了%s, 现在开始玩新游戏 %s", op.get().getNickname(), game, getPlayTiming(time), gameextrainfo), false);
                        playGame.put(key, gameextrainfo);
                        playTime.put(key, LocalDateTime.now());
                    }
                } else {
                    ////之前没玩游戏
                    bot.sendGroupMsg(groupId, String.format("%s 正在游玩 %s", op.get().getNickname(), gameextrainfo), false);
                    playGame.put(key, gameextrainfo);
                    playTime.put(key, LocalDateTime.now());
                }
            } else {
                if (StringUtils.hasText(game)) {
                    //之前在玩游戏, 现在不玩了
                    bot.sendGroupMsg(groupId, String.format("%s 不玩%s了%s", op.get().getNickname(), game, getPlayTiming(time)), false);
                    playGame.remove(key);
                    playTime.remove(key);
                }
            }

        });
    }

    private String getPlayTiming(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        Duration between = Duration.between(time, LocalDateTime.now());
        return String.format(", 本次游玩时长 %s 分钟", between.toMinutes());
    }


    @Scheduled(cron = "0 0/1 * * * ?", zone = "Asia/Shanghai")
    public void handler() {
        subMap.forEach(this::getStatus);
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    private void init() {
        update();
        //getBot();
    }


    private void update() {
        HashMap<Long, Map<String, SteamEntity>> map = new HashMap<>();
        repository.findAll().stream().forEach(steamEntity -> {
            Map<String, SteamEntity> group = map.computeIfAbsent(steamEntity.getGroupId(), k -> new HashMap<>());
            group.put(steamEntity.getSteamId(), steamEntity);
        });
        this.subMap = map;
    }

    @Autowired
    public void setConfig(BotConfig.Plugins.SteamConfig config) {
        this.config = config;
    }

    @Autowired
    public void setRepository(SteamRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setTemplate(RestTemplate restTemplate) {
        this.template = restTemplate;
    }

    @Autowired
    public void setBase(BotConfig.Base base) {
        this.base = base;
    }

    private Bot getBot() {
        if (bot == null) {
            bot = botContainer.robots.get(base.getBotId());
        }
        return bot;
    }

}
