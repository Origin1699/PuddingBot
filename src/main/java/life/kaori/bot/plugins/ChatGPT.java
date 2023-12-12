package life.kaori.bot.plugins;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mikuac.shiro.annotation.GroupMessageHandler;
import com.mikuac.shiro.annotation.MessageHandlerFilter;
import com.mikuac.shiro.annotation.common.Shiro;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.MessageEvent;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import life.kaori.bot.common.CommonUtil;
import life.kaori.bot.common.constant.BotStrings;
import life.kaori.bot.common.util.AuthUtil;
import life.kaori.bot.common.util.FileUtils;
import life.kaori.bot.common.util.MessageUtil;
import life.kaori.bot.config.BotConfig;
import life.kaori.bot.core.ExecutorUtil;
import life.kaori.bot.entity.chatgpt.ChatGPTEntity;
import life.kaori.bot.entity.chatgpt.ChatGPTPrompt;
import life.kaori.bot.entity.chatgpt.ChatGPTPlayer;
import life.kaori.bot.repository.ChatGPTPlayerRepository;
import life.kaori.bot.repository.ChatGPTRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;

/**
 * author: origin
 */
@Component
@Shiro
public class ChatGPT {

    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private ChatGPTRepository repository;
    @Autowired
    private ChatGPTPlayerRepository playerRepository;
    @Autowired
    private OpenAiService service;
    @Autowired
    private BotConfig botConfig;

    private Vector lock = new Vector<>();


    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(?i)chat\\s(?<action>set|del|show|reload|add)?\\s?(?<prompt>[\\s\\S]+?)?$")
    public void chat(Bot bot, GroupMessageEvent event, Matcher matcher) {
        ExecutorUtil.exec(bot, event, ChatGPT.class.getSimpleName(), () -> {
            String prompt = matcher.group("prompt").trim();
            String action = matcher.group("action");
            if (action == null) {
                chat(bot, event, prompt);
            } else {
                switch (action) {
                    case "show" -> show(bot, event);
                    case "add" -> add(bot, event, prompt);
                    case "set" -> set(bot, event, prompt);
                    case "reload" -> reload(bot, event);
                    case "delete" -> delete(bot, event, prompt);
                }
            }

        });
    }

    private void show(Bot bot, GroupMessageEvent event) {
        List<ChatGPTPlayer> list = playerRepository.findAll();
        MsgUtils text = MsgUtils.builder().text("当前所有prompt:");
        if (list != null) {
            list.forEach(player -> text.text("\n" + player.getName()));
        }
        MessageUtil.sendGroupMsg(bot, event, text.build());
    }


    public void chat(Bot bot, GroupMessageEvent event, String cmd) {
        BotConfig.ChatGPT chatGPT = botConfig.getChatGPT();
        Long userId = event.getUserId();
        if (lock.contains(userId)) {
            MessageUtil.sendGroupMsg(bot, event, "上次结果未响应");
            return;
        }
        ArrayList<ChatMessage> prompts = new ArrayList<>();
        ChatGPTEntity byUserid = repository.findByUserid(userId);
        if (byUserid != null) {
            byUserid.getPlayer().getList().forEach(prompt -> prompts.add(new ChatMessage(prompt.getRole().value(), prompt.getContent())));
        }
        prompts.add(new ChatMessage(ChatMessageRole.USER.value(), cmd));
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(chatGPT.getModule())
                .messages(prompts)
                .build();
        ChatCompletionResult chatCompletion = null;
        try {
            lock.add(userId);
            chatCompletion = service.createChatCompletion(request);
        } finally {
            lock.remove(userId);
        }
        if (!ObjectUtils.isEmpty(chatCompletion)) {
            MessageUtil.sendGroupMsg(bot, event, chatCompletion.getChoices().get(0).getMessage().getContent().trim());
        }
    }

    private void set(Bot bot, GroupMessageEvent event, String prompt) {
    }

    public void add(Bot bot, GroupMessageEvent event, String cmd) {

    }

    public void delete(Bot bot, GroupMessageEvent event, String cmd) {

    }

    public void reload(Bot bot, MessageEvent event) {
        ExecutorUtil.exec(bot, event, ChatGPT.class.getSimpleName(), () -> {
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
            playerRepository.deleteByType(0);
            list.forEach(map -> {
                playerRepository.save(new ChatGPTPlayer(map.get("act"), Arrays.asList(new ChatGPTPrompt(ChatMessageRole.SYSTEM, map.get("prompt"))), 0));
            });
        });
    }

    public void callback(Bot bot, GroupMessageEvent event) {

    }
}
