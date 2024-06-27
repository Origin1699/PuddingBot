package top.ikaori.bot.common.util;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import top.ikaori.bot.config.BeanConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author origin
 */
@Component
public class NetUtil {

    private static BeanConfig beanConfig;

    private final MediaType mediaType = MediaType.get("application/json; charset=utf-8");

    private static final OkHttpClient client = new OkHttpClient();
    @Value("${bot-config.base.proxy.host}")
    private String host;
    @Value("${bot-config.base.proxy.port}")
    private String port;
    @Value("${bot-config.base.proxy.type}")
    private String type;

    @Autowired
    public void setBeanConfig(BeanConfig beanConfig) {
        NetUtil.beanConfig = beanConfig;
    }

    public static Response get(String url) throws IOException {
        Request req = new Request.Builder().url(url).get().build();
        return client.newCall(req).execute();
    }

    public static Response get(String url, boolean proxy) throws IOException {
        Request req = new Request.Builder().url(url).get().build();
        var newC = client.newBuilder();
        if (proxy) {
            newC.setProxy$okhttp(new Proxy(Proxy.Type.valueOf("HTTP"), new InetSocketAddress("127.0.0.1", Integer.parseInt("7890"))));
        }
        return newC.build().newCall(req).execute();
    }
}
