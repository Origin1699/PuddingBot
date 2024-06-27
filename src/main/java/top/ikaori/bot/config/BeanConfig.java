package top.ikaori.bot.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * @author origin
 */
@Configuration
@Component
@AllArgsConstructor
public class BeanConfig {

    @Bean
    public OpenAiService openAiService(BotConfig botConfig) {
        var config = botConfig.getPlugins().getChatGPTConfig();
        var proxy = botConfig.getBase().getProxy();
        OkHttpClient client = OpenAiService.defaultClient(config.getToken(), Duration.ofSeconds(config.getTimeout())).newBuilder()
                .proxy(new Proxy(Proxy.Type.valueOf(proxy.getType()), new InetSocketAddress(proxy.getHost(), proxy.getPort()))).build();
        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        Retrofit retrofit = OpenAiService.defaultRetrofit(client, mapper);
        OpenAiApi openAiApi = retrofit.create(OpenAiApi.class);
        return new OpenAiService(openAiApi);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().set(1, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        return restTemplate;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    //bot配置bean注册
    @Bean
    public BotConfig.Base base(BotConfig botConfig) {
        return botConfig.getBase();
    }

    @Bean
    public BotConfig.Base.Proxy proxy(BotConfig botConfig) {
        return botConfig.getBase().getProxy();
    }

    @Bean
    public BotConfig.Plugins.ChatGPTConfig chatGPTConfig(BotConfig botConfig) {
        return botConfig.getPlugins().getChatGPTConfig();
    }

    @Bean
    public BotConfig.Plugins.PicSearchConfig picSearchConfig(BotConfig botConfig) {
        return botConfig.getPlugins().getPicSearchConfig();
    }

    @Bean
    public BotConfig.Plugins.TarotConfig tarotConfig(BotConfig botConfig) {
        return botConfig.getPlugins().getTarotConfig();
    }

    @Bean
    public BotConfig.Plugins.Aria2Config aria2Config(BotConfig botConfig) {
        return botConfig.getPlugins().getAria2Config();
    }

    @Bean
    public BotConfig.Plugins.SteamConfig steamConfig(BotConfig botConfig) {
        return botConfig.getPlugins().getSteamConfig();
    }
}
