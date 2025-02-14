package top.ikaori.bot.plugins;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.KumoFont;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.image.AngleGenerator;
import com.kennycason.kumo.nlp.FrequencyAnalyzer;
import com.kennycason.kumo.nlp.tokenizer.api.WordTokenizer;
import com.kennycason.kumo.palette.ColorPalette;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.enums.MsgTypeEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.ikaori.bot.common.CommonUtil;
import top.ikaori.bot.common.util.AuthUtil;
import top.ikaori.bot.config.BotConfig;
import top.ikaori.bot.config.Global;
import top.ikaori.bot.core.ExecutorUtil;
import top.ikaori.bot.entity.MessageRecordEntity;
import top.ikaori.bot.repository.MessageRecordRepository;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author origin
 */
@Shiro
@Component
@RequiredArgsConstructor
@Slf4j
@Lazy
public class WordCloud implements Plugin {
    private static final String ZONE = "Asia/Shanghai";

    private final MessageRecordRepository repository;

    private final BotConfig.Plugins.WordCloudConfig config;

    private final AuthUtil authUtil;
    @Getter
    private final List<String> nickName = List.of("词云");
    @Getter
    private final String help = """
            """;
    private final File font = CommonUtil.getPluginResourceDir("font/loli.ttf");
    @Autowired
    private Global global;

    private String generateWordCloud(List<String> text) throws IOException {
        FrequencyAnalyzer frequencyAnalyzer = new FrequencyAnalyzer();
        frequencyAnalyzer.setWordFrequenciesToReturn(300);
        frequencyAnalyzer.setMinWordLength(2);
        frequencyAnalyzer.setWordTokenizer(new JieBaTokenizer());
        List wordFrequencies = frequencyAnalyzer.load(text);
        Dimension dimension = new Dimension(1000, 1000);
        com.kennycason.kumo.WordCloud wordCloud = new com.kennycason.kumo.WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        wordCloud.setPadding(2);
        wordCloud.setAngleGenerator(new AngleGenerator(0));
        wordCloud.setKumoFont(new KumoFont(Files.newInputStream(font.toPath())));
        List<Color> colors = Arrays.asList(
                new Color(0x0000FF),
                new Color(0x40D3F1),
                new Color(0x40C5F1),
                new Color(0x40AAF1),
                new Color(0x408DF1),
                new Color(0x4055F1)
        );
        wordCloud.setBackground(new CircleBackground((1000 + 1000) / 4));
        wordCloud.setBackgroundColor(Color.WHITE);
        wordCloud.setColorPalette(new ColorPalette(colors));
        wordCloud.setFontScalar(new LinearFontScalar(config.getMinFontSize(), config.getMaxFontSize()));
        wordCloud.build(wordFrequencies);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        wordCloud.writeToStreamAsPNG(stream);

        return Base64.getEncoder().encodeToString(stream.toByteArray());
    }

    private List<String> query(Long userId, Long groupId, LocalDate start, LocalDate end) {
        return repository.findAllByUserIdAndGroupIdAndTimeBetween(userId, groupId, start, end).stream()
                .map(MessageRecordEntity::getMessage)
                .toList();
    }

    private List<String> query(Long groupId, LocalDate start, LocalDate end) {
        return repository.findAllByGroupIdAndTimeBetween(groupId, start, end).stream()
                .map(MessageRecordEntity::getMessage)
                .toList();
    }

    private List<String> getWordsForRange(Long userId, Long groupId, String type, String range) {
        // 获取日期范围的方法
        Pair<LocalDate, LocalDate> dateRange = getDateRange(range);
        if (dateRange == null) {
            return Collections.emptyList();
        }
        LocalDate startDate = dateRange.getFirst();
        LocalDate endDate = dateRange.getSecond();

        // 判断查询类型
        if ("我的".equals(type)) {
            return query(userId, groupId, startDate, endDate);
        } else if ("本群".equals(type)) {
            return query(groupId, startDate, endDate);
        }

        return Collections.emptyList();
    }

    private Pair<LocalDate, LocalDate> getDateRange(String range) {
        LocalDate now = LocalDate.now();
        LocalDate start;
        LocalDate end;
        switch (range) {
            case "今日":
                return Pair.of(now, now);
            case "本周":
                start = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                end = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                return Pair.of(start, end);
            case "本月":
                start = now.with(TemporalAdjusters.firstDayOfMonth());
                end = now.with(TemporalAdjusters.lastDayOfMonth());
                return Pair.of(start, end);
            case "本年":
                start = now.with(TemporalAdjusters.firstDayOfYear());
                end = now.with(TemporalAdjusters.lastDayOfYear());
                return Pair.of(start, end);
            default:
                return null;
        }
    }

    private List<String> getWords(Long userId, Long groupId, String type, String range) {
        String filterRule = StringUtils.join(config.getFilterRule(), "|");
        List<String> contents = new ArrayList<>();
        getWordsForRange(userId, groupId, type, range).forEach(raw -> {
            contents.addAll(ShiroUtils.rawToArrayMsg(raw).stream()
                    .filter(it -> it.getType() == MsgTypeEnum.text)
                    .map(it -> it.getData().get("text").trim())
                    .filter(it -> !it.contains(filterRule))
                    .toList());
        });
        return contents;
    }

    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(我的|本群)(今日|本周|本月|本年)词云?$")
    public void handler(GroupMessageEvent event, Bot bot, Matcher matcher) {
        Integer msgId = event.getMessageId();

        ExecutorUtil.exec(bot, event, getName(), () -> {
            String type = matcher.group(1);
            String range = matcher.group(2);
            List<String> contents = getWords(event.getUserId(), event.getGroupId(), type, range);
            if (contents.isEmpty()) {
                bot.sendGroupMsg(event.getGroupId(), "暂无聊天记录", false);
                return;
            }
            String msg = MsgUtils.builder().img("base64://" + generateWordCloud(contents)).build();
            bot.sendGroupMsg(event.getGroupId(), msg, false);
        });
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^词云\\s(day|week|month)\\$")
    public void handler(AnyMessageEvent event, Bot bot, Matcher matcher) {
        if (authUtil.masterAuth(event)) {
            bot.sendMsg(event, "此操作需要管理员权限", false);
        }
        switch (matcher.group(1)) {
            case "day":
                taskForDay();
                break;
            case "week":
                taskForWeek();
                break;
            case "month":
                taskForMonth();
                break;
        }
    }

    @Scheduled(cron = "0 30 23 * * ?", zone = ZONE)
    public void taskForDay() {
        LocalDateTime now = LocalDateTime.now();
        if (now.getDayOfWeek() == DayOfWeek.SUNDAY) return;
        if (now.equals(now.with(TemporalAdjusters.lastDayOfMonth()))) return;
        task("今日");
    }

    @Scheduled(cron = "0 30 23 ? * SUN", zone = ZONE)
    public void taskForWeek() {
        LocalDateTime now = LocalDateTime.now();
        if (now.equals(now.with(TemporalAdjusters.lastDayOfMonth()))) return;
        task("本周");
    }

    @Scheduled(cron = "0 30 23 L * ?", zone = ZONE)
    public void taskForMonth() {
        task("本月");
    }

    private void task(String range) {
        long cronTaskRate = config.getCronTaskRate() * 1000L;

        global.bot().getGroupList().getData().forEach(it -> {
            try {
                Thread.sleep(cronTaskRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            List<String> contents = getWords(0L, it.getGroupId(), "本群", range);
            if (contents.isEmpty()) {
                return;
            }
            global.bot().sendGroupMsg(it.getGroupId(), "今天也是忙碌的一天呢，来康康群友" + range + "聊些什么奇怪的东西～", false);
            try {
                String msg = MsgUtils.builder().img("base64://" + generateWordCloud(contents)).build();
                global.bot().sendGroupMsg(it.getGroupId(), msg, false);
                log.info(range + "词云推送到群 [" + it.getGroupName() + "](" + it.getGroupId() + ") 成功");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    private static class JieBaTokenizer implements WordTokenizer {
        @Override
        public List<String> tokenize(String sentence) {
            return new JiebaSegmenter().process(sentence, JiebaSegmenter.SegMode.INDEX).stream()
                    .map(it -> it.word.trim())
                    .toList();
        }
    }

}
