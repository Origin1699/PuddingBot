package top.ikaori.bot.plugins;

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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import top.ikaori.bot.common.CommonUtil;
import top.ikaori.bot.common.constant.BotStrings;
import top.ikaori.bot.common.util.AuthUtil;
import top.ikaori.bot.common.util.FileUtils;
import top.ikaori.bot.common.util.MessageUtil;
import top.ikaori.bot.config.BotConfig;
import top.ikaori.bot.core.ExecutorUtil;
import top.ikaori.bot.entity.chatgpt.ChatGPTEntity;
import top.ikaori.bot.entity.chatgpt.ChatGPTPlayer;
import top.ikaori.bot.entity.chatgpt.ChatGPTPrompt;
import top.ikaori.bot.repository.ChatGPTPlayerRepository;
import top.ikaori.bot.repository.ChatGPTRepository;

import javax.transaction.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;

/**
 * @author origin
 */
@Component
@Shiro
@Transactional
@RequiredArgsConstructor
public class ChatGPT implements AbstractPlugin {

    private final AuthUtil authUtil;
    private final ChatGPTRepository repository;
    private final ChatGPTPlayerRepository playerRepository;
    private final OpenAiService service;
    private final BotConfig botConfig;
    private final Vector lock = new Vector<>();

    @Getter
    private final List<String> nickName = List.of("ChatGPT");
    @Getter
    private final String help = """
                        chat [set|del|show|reload|add] prompt ; prompt修改操作
                        chat prompt ; chat对话
            """;


    @GroupMessageHandler
    @MessageHandlerFilter(cmd = "^(?i)chat\\s+(?<action>set|del|show|reload|add)?\\s?(?<prompt>[\\s\\S]+?)?$")
    public void chat(Bot bot, GroupMessageEvent event, Matcher matcher) {
        ExecutorUtil.exec(bot, event, ChatGPT.class.getSimpleName(), () -> {
            String prompt = matcher.group("prompt");
            String action = matcher.group("action");
            if (action == null) {
                if (Strings.isBlank(prompt))
                    return;
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
        list.forEach(player -> text.text("\n" + player.getName()));
        MessageUtil.sendGroupMsg(bot, event, text.build());
    }


    public void chat(Bot bot, GroupMessageEvent event, String cmd) {
        var chatGPT = botConfig.getPlugins().getChatGPTConfig();
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
                playerRepository.save(new ChatGPTPlayer(map.get("act"), List.of(new ChatGPTPrompt(ChatMessageRole.SYSTEM, map.get("prompt"))), 0));
            });
        });
    }
}
