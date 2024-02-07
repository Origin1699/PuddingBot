package top.ikaori.bot.plugins.aria2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mikuac.shiro.annotation.AnyMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import org.apache.logging.log4j.util.Strings;
import top.ikaori.bot.common.util.MessageUtil;
import top.ikaori.bot.core.ExecutorUtil;
import top.ikaori.bot.core.exception.ExceptionMsg;
import top.ikaori.bot.entity.Aria2Entity;
import top.ikaori.bot.entity.dto.Aria2DTO;
import top.ikaori.bot.plugins.Plugin;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import top.ikaori.bot.repository.Aria2Repository;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
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
    private final String help = """
            【磁力下载】
            aria show 显示正在下载内容
            aria add [磁力链]  下载
            aria del [编号]    删除
            """;

    private Aria2Util util;

    private DecimalFormat df = new DecimalFormat("0.00");

    @Autowired
    public void setUtil(Aria2Util aria2Util) {
        this.util = aria2Util;
    }

    private Aria2Repository repository;

    @Autowired
    public void setRepository(Aria2Repository repository) {
        this.repository = repository;
    }

    @AnyMessageHandler
    @MessageHandlerFilter(cmd = "^(?i)aria\\s+(?<action>show|add|del|start|stop)?\\s?(?<prompt>[\\s\\S]+?)?$")
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
                case "start", "stop", "del" -> startOrStopOrDelete(bot, event, action, prompt);
            }
        });

    }

    private void startOrStopOrDelete(Bot bot, AnyMessageEvent event, String action, String prompt) {
        prompt = prompt.trim();
        try {
            Optional<Aria2Entity> op = repository.findById(Long.parseLong(prompt));
            if (op.isPresent()) {
                String gid = op.get().getGid();
                if (action.equals("start")) {
                    util.start(gid);
                } else if (action.equals("stop")){
                    util.stop(gid);
                } else if (action.equals("del")){
                    util.delete(gid);
                }
            } else {
                bot.sendMsg(event, "请输入正确的编号", false);
            }
        } catch (JsonProcessingException e) {
            bot.sendMsg(event, "请输入正确的命令", false);
        }

    }

    private void add(Bot bot, AnyMessageEvent event, String prompt) {
        prompt = prompt.trim();
        if (!prompt.startsWith("magnet:?xt=urn:btih:")) {
            return;
        }
        try {
            String gid = util.addUrl(prompt);
            Aria2Entity entity = new Aria2Entity();
            entity.setGid(gid);
            entity.setExecutor(event.getUserId());
            entity.setMagnet(prompt);
            MessageUtil.sendMsg(bot, event, "已添加任务");
            repository.save(entity);
        } catch (JsonProcessingException e) {
            throw ExceptionMsg.ARIA2_MAGNET_LINK_ERROR;
        }
    }

    private void show(Bot bot, AnyMessageEvent event) {
        List<String> messages = new ArrayList<>();
        Aria2DTO tellWaiting = util.getTellWaiting();
        tellWaiting.getResult().forEach(result -> {
            messages.add(buildMsg(result, Aria2CommandType.tellWaiting));
        });
        Aria2DTO tellActive = util.getTellActive();
        tellActive.getResult().forEach(result -> {
            messages.add(buildMsg(result, Aria2CommandType.tellActive));
        });
        if (messages.isEmpty()) {
            MessageUtil.sendAnyMsg(bot, event, "当前无下载内容");
        } else {
            MessageUtil.sendGroupMsg(bot, event, messages);

        }
    }

    private String buildMsg(Aria2DTO.ResultBean result, Aria2CommandType type) {

        String totalLength = result.getTotalLength();
        String completedLength = result.getCompletedLength();

        String gid = result.getGid();
        Optional<Aria2Entity> op = repository.findByGid(gid);


        var info = result.getBittorrent().getInfo();
        Aria2Entity entity = new Aria2Entity();
        StringJoiner joiner;
        if (op.isPresent()) {
            joiner = new StringJoiner("  ", String.format("【%s】", op.get().getId()), "");
            entity = op.get();
            if (Strings.isNotBlank(entity.getName()) && info != null) {
                entity.setName(info.getName());
                entity.setSize(totalLength);
                repository.save(entity);
            }
        } else {
            entity.setGid(gid);
            entity.setName(info.getName());
            entity.setSize(totalLength);
            repository.save(entity);
            joiner = new StringJoiner("  ", String.format("【%s】", entity.getId()), "");
        }

        if (info == null) {
            joiner.add(result.getInfoHash() + "\n");
        } else {
            joiner.add(info.getName() + "\n");
        }

        if (type.equals(Aria2CommandType.tellActive)) {
            joiner.add("正在下载");
        } else {
            joiner.add("已暂停");
        }

        if (Strings.isNotBlank(totalLength) && Strings.isNotBlank(completedLength)) {
            double totalLengthBytes = Double.parseDouble(totalLength);
            double completedLengthBytes = (Double.parseDouble(completedLength) / totalLengthBytes) * 100;
            String size = " - ";
            double totalKb = totalLengthBytes / 1024;
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

            String percentage = df.format(completedLengthBytes);
            joiner.add("大小:").add(size);
            joiner.add("进度:").add(percentage);
        }
        if (type.equals(Aria2CommandType.tellActive)) {
            String downloadSpeed = result.getDownloadSpeed();
            if (Strings.isNotBlank(downloadSpeed)) {
                double bytes = Double.parseDouble(downloadSpeed);
                double kb = bytes / 1024;
                if (kb > 1024) {
                    downloadSpeed = df.format(kb / 1024) + "MB/s";
                } else {
                    downloadSpeed = df.format(kb) + "KB/s";
                }
                joiner.add("速度:").add(downloadSpeed);
            }
        }
        return joiner.toString();
    }


}
