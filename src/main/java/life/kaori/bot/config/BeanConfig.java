package life.kaori.bot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.client.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;

/**
 * author: origin
 */
@Configuration
@Component
@AllArgsConstructor
public class BeanConfig {

    @Bean
    public OpenAiService openAiService(BotConfig botConfig) {
        var config = botConfig.getChatGPT();
        var proxy = botConfig.getProxy();
        OkHttpClient client = OpenAiService.defaultClient(config.getToken(), Duration.ofSeconds(config.getTimeout())).newBuilder()
                .proxy(new Proxy(Proxy.Type.valueOf(proxy.getType()), new InetSocketAddress(proxy.getHost(), proxy.getPort()))).build();
        ObjectMapper mapper = OpenAiService.defaultObjectMapper();
        Retrofit retrofit = OpenAiService.defaultRetrofit(client, mapper);
        OpenAiApi openAiApi = retrofit.create(OpenAiApi.class);
        return new OpenAiService(openAiApi);
    }
}
