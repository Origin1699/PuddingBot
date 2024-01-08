package top.ikaori.bot.plugins.aria2;

import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import top.ikaori.bot.common.util.MessageUtil;
import top.ikaori.bot.core.ExecutorUtil;
import top.ikaori.bot.entity.dto.Aria2DTO;
import top.ikaori.bot.plugins.Plugin;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author origin
 */
@Shiro
@Component
public class Aria2 implements Plugin {

    @Getter
    private final String name = this.getClass().getSimpleName();
    @Getter
    private final List<String> nickName = List.of("aria2");
    @Getter
    private final String help = "";

    private Aria2Util util;

    private DecimalFormat df = new DecimalFormat("0.00");

    @Autowired
    public void setUtil(Aria2Util aria2Util) {
        this.util = aria2Util;
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^(?i)aria\\s+(?<action>show|add|delete)?\\s?(?<prompt>[\\s\\S]+?)?$")
    public void list(Bot bot, AnyMessageEvent event, Matcher matcher) {
        ExecutorUtil.exec(bot, event, Plugin.class.getSimpleName(), () -> {
            String prompt = matcher.group("prompt");
            String action = matcher.group("action");
            if (!StringUtils.hasText(action) || !StringUtils.hasText(prompt)) {
                if (!StringUtils.hasText(prompt) && !"show".equals(action)) {
                    return;
                }
            }
            switch (action) {
                case "show" -> show(bot, event);
                case "add" -> add(bot, event, prompt);
                case "delete" -> delete(bot, event, prompt);
            }
        });

    }

    private void delete(Bot bot, AnyMessageEvent event, String prompt) {
    }

    private void add(Bot bot, AnyMessageEvent event, String prompt) {
        prompt = prompt.trim();
        if (!prompt.startsWith("magnet:?xt=urn:btih:")){
            return;
        }
        Aria2DTO aria2DTO = util.addUrl(prompt);
        MessageUtil.sendMsg(bot,event,"已添加任务");
    }

    private void show(Bot bot, AnyMessageEvent event) {
        List<String> messages = new ArrayList<>();
        Aria2DTO tellWaiting = util.command(Aria2CommandType.tellWaiting);
        tellWaiting.getResult().forEach(result -> {

            double totalBytes = Double.parseDouble(result.getTotalLength());
            double percentage = (Double.parseDouble(result.getCompletedLength()) / totalBytes) * 100;
            String formattedPercentage = df.format(percentage);
            String size;
            double totalKb = totalBytes / 1024;
            if (totalKb > 1024) {
                double totalMb = totalKb / 1024;
                if (totalMb > 1024) {
                    size = df.format(totalMb / 1024) + "GB";
                } else {
                    size = df.format(totalMb + "MB");
                }
            } else {
                size = df.format(totalKb) + "KB";
            }

            messages.add(MsgUtils.builder().text(result.getBittorrent().getInfo().getName()).text("\n" + "已暂停  大小:  " + size + "  进度:  " + formattedPercentage + "%").build());
        });
        Aria2DTO tellActive = util.getTellActive();
        tellActive.getResult().forEach(result -> {
            double totalBytes = Double.parseDouble(result.getTotalLength());
            double percentage = (Double.parseDouble(result.getCompletedLength()) / totalBytes) * 100;
            String formattedPercentage = df.format(percentage);
            String downloadSpeed;
            double bytes = Double.parseDouble(result.getDownloadSpeed());
            double kb = bytes / 1024;
            if (kb > 1024) {
                downloadSpeed = df.format(kb / 1024) + "MB/s";
            } else {
                downloadSpeed = df.format(kb) + "KB/s";
            }
            String size;
            double totalKb = totalBytes / 1024;
            if (totalKb > 1024) {
                double totalMb = totalKb / 1024;
                if (totalMb > 1024) {
                    size = df.format(totalMb / 1024) + "GB";
                } else {
                    size = df.format(totalMb + "MB");
                }
            } else {
                size = df.format(totalKb) + "KB";
            }
            messages.add(MsgUtils.builder().text(result.getBittorrent().getInfo().getName()).
                    text("\n" + "正在下载  大小:  " + size + "  进度:  " + formattedPercentage + "%  速度:  " + downloadSpeed).build());
        });
        MessageUtil.sendGroupMsg(bot, event, messages);
    }
}
