package life.kaori.bot.plugins;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.AnyMessageEvent;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import life.kaori.bot.common.CommonUtil;
import life.kaori.bot.common.constant.BotStrings;
import life.kaori.bot.common.util.AuthUtil;
import life.kaori.bot.common.util.FileUtils;
import life.kaori.bot.common.util.MessageUtil;
import life.kaori.bot.core.OperationUtil;
import life.kaori.bot.entity.chatgpt.ChatGPTEntity;
import life.kaori.bot.entity.chatgpt.ChatGPTPrompt;
import life.kaori.bot.entity.chatgpt.ChatGPTPlayer;
import life.kaori.bot.repository.ChatGPTPlayerRepository;
import life.kaori.bot.repository.ChatGPTRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;

/**
 * author: origin
 */
@Component
@Shiro
@AllArgsConstructor
public class ChatGPT {

    private AuthUtil authUtil;
    private ChatGPTRepository repository;
    private ChatGPTPlayerRepository playerRepository;
    private OpenAiService service;


    @GroupMessageHandler(cmd = "^(?i)chat\\s(?<action>set|del|show|reload|add)?\\s?(?<prompt>[\\s\\S]+?)?$")
    public void chat(Bot bot, GroupMessageEvent event, Matcher matcher) {
        OperationUtil.exec(bot, event, ChatGPT.class.getSimpleName(), () -> {
            String prompt = matcher.group("prompt");
            String action = matcher.group("action");
            switch (action) {
                case "reload" -> reload(bot, event);
                case "add" -> add(bot, event, prompt);
                case "delete" -> delete(bot, event, prompt);
                case "set" -> set(bot, event, prompt);
                case "show" -> show(bot, event);
                default -> chat(bot, event, prompt);
            }

        });
    }

    private void show(Bot bot, GroupMessageEvent event) {
        List<ChatGPTPlayer> list = playerRepository.findAll();
        StringBuilder builder = new StringBuilder();
        MsgUtils text = MsgUtils.builder().text("当前所有prompt:");
        if (list!=null){
        }
//        MessageUtil.sendMsg(bot, event, new);
    }


    public void chat(Bot bot, GroupMessageEvent event, String cmd) {

    }
    private void set(Bot bot, GroupMessageEvent event, String prompt) {
    }

    public void add(Bot bot, GroupMessageEvent event, String cmd) {

    }

    public void delete(Bot bot, GroupMessageEvent event, String cmd) {

    }

    public void reload(Bot bot, MessageEvent event) {
        OperationUtil.exec(bot, event, ChatGPT.class.getSimpleName(), () -> {
            Long userId = event.getUserId();
            authUtil.masterCheck(event);
            File file = CommonUtil.getResourceFile("prompts.json");
            String json = FileUtils.readFile(file);

            Gson gson = new Gson();
            List<HashMap<String, String>> list = gson.fromJson(json, new TypeToken<List<HashMap>>() {
            }.getType());
            if (list.isEmpty()) {
                throw BotStrings.RESOURCE_FILE_PARSE_ERROR.exception();
            }
            repository.deleteByUserid(0L);
            list.forEach(map -> {
                repository.save(new ChatGPTEntity(0L, new ChatGPTPlayer(map.get("act"), Arrays.asList(new ChatGPTPrompt(ChatMessageRole.SYSTEM, map.get("prompt"))))));
            });
        });
    }

    public void callback(Bot bot, GroupMessageEvent event) {

    }
}
