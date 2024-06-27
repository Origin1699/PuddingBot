package top.ikaori.bot.plugins.picSearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.common.utils.MsgUtils;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import top.ikaori.bot.common.util.NetUtil;
import top.ikaori.bot.config.BotConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * @author origin
 */
@Component
public class Ascii2d {
    private BotConfig botConfig;

    private BotConfig.Base.Proxy proxy;
    private ObjectMapper objectMapper;
    private final String api = "https://ascii2d.net/search/url/%s";


    public Pair<String, String> search(String imgUrl) throws IOException {
        boolean isProxy = botConfig.getPlugins().getPicSearchConfig().isProxy();
        String url = String.format(api, imgUrl);
        Response resp = NetUtil.get(url, isProxy);
        String reqUrl = resp.request().url().toString();
        var colorSearchResult = request(0, reqUrl, isProxy);
        var bovwSearchResult = request(1, reqUrl.replace("/color/", "/bovw/"), isProxy);
        return Pair.of(colorSearchResult, bovwSearchResult);
    }

    public String request(int type, String url, boolean proxy) throws IOException {
        var connect = Jsoup.connect(url);
        if (proxy) {
            var p = botConfig.getBase().getProxy();
            connect.proxy(
                    new Proxy(Proxy.Type.valueOf(p.getType()), new InetSocketAddress(p.getHost(), p.getPort()))
            );
        }
        var header = connect.header("User-Agent", "PostmanRuntime/7.29.0");
        var document = header.get();

        var itemBox = document.getElementsByClass("item-box").get(1);
        var thumbnail = itemBox.select("div.image-box > img");
        var link = itemBox.select("div.detail-box > h6 > a").get(0);
        var author = itemBox.select("div.detail-box > h6 > a").get(1);
        return MsgUtils
                .builder()
                .img(thumbnail.attr("abs:src"))
                .text("\n标题：" + link.text())
                .text("\n作者：" + author.text())
                .text("\n链接：" + link.attr("abs:href"))
                .text("\n数据来源：Ascii2d " + (type == 0 ? "色合検索" : "特徴検索"))
                .build();
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setBotConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }
}
