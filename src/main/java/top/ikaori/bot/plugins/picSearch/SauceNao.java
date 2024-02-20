package top.ikaori.bot.plugins.picSearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.common.utils.MsgUtils;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.ikaori.bot.common.util.NetUtil;
import top.ikaori.bot.config.BotConfig;
import top.ikaori.bot.core.exception.BotException;
import top.ikaori.bot.entity.dto.SauceDTO;

import java.io.IOException;

/**
 * @author origin
 */
@Component
public class SauceNao {
    private BotConfig botConfig;

    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    private final String api = "https://saucenao.com/search.php?api_key=%s&output_type=2&numres=3&db=999&url=%s";

    public SauceDTO request(String imgUrl) throws IOException {
        String url = String.format(api, botConfig.getPicSearch().getToken(), imgUrl);
        Response resp = NetUtil.get(url);
        String bodyString = resp.body().string();
        SauceDTO body = objectMapper.readValue(bodyString, SauceDTO.class);
        var header = body.getHeader();
        if (header.getLongRemaining() <= 0) throw new BotException("今日的搜索配额已耗尽啦");
        if (header.getShortRemaining() <= 0) throw new BotException("短时间内搜索配额已耗尽");
        if (body.getResults().isEmpty()) throw new BotException("短时间内搜索配额已耗尽");
        return body;
    }

    public Pair<String, String> search(String imgUrl) throws IOException {
        SauceDTO sauceDTO = request(imgUrl);
        var results = sauceDTO.getResults();
        if (results.isEmpty()) {
            return Pair.of("0", MsgUtils.builder().text("SauceNao未能找到相似的内容，正在使用Ascii2d进行检索···").build());
        }
        var result = results.get(0);
        var data = result.getData();
        var header = result.getHeader();

        var msgUtils = MsgUtils.builder()
                .img(header.getThumbnail())
                .text("\n相似度：" + header.getSimilarity());
        switch (header.getIndexId()) {
            case 5 -> {
                msgUtils.text("\n标题：" + data.getTitle());
                ;
                msgUtils.text("\n画师：" + data.getMemberName());
                msgUtils.text("\n作品主页：https://pixiv.net/i/" + data.getPixivId());
                msgUtils.text("\n画师主页：https://pixiv.net/u/" + data.getMemberId());
                msgUtils.text("\n反代地址：https://i.loli.best/" + data.getPixivId());
                msgUtils.text("\n数据来源：SauceNao (Pixiv)");
            }

            case 41 -> {
                msgUtils.text("\n链接：" + data.getExtUrls().get(0));
                msgUtils.text("\n用户：https://twitter.com/" + data.getTwitterUserHandle());
                msgUtils.text("\n数据来源：SauceNao (Twitter)");
                msgUtils.text("\n来源：" + data.getSource());
            }

            case 18, 38 -> {
                msgUtils.text("\n来源：" + data.getSource());
                msgUtils.text("\n日文名：" + data.getJpName());
                msgUtils.text("\n英文名：" + data.getEngName());
                msgUtils.text("\n数据来源：SauceNao (H-Misc)");
            }
        }

        return Pair.of(header.getSimilarity(), msgUtils.build());
    }


    @Autowired
    public void setBotConfig(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
