package top.ikaori.bot.common.util;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.ikaori.bot.config.BeanConfig;

import java.io.IOException;

/**
 * @author origin
 */
@Component
public class NetUtil {

    private static BeanConfig beanConfig;

    private MediaType mediaType = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();

    @Autowired
    public void setBeanConfig(BeanConfig beanConfig) {
        this.beanConfig = beanConfig;
    }

    public static Response get(String url) throws IOException {
        Request req = new Request.Builder().url(url).get().build();
        return client.newCall(req).execute();
    }
}
