package top.ikaori.bot.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author origin
 */
@Component
public class RequestUtil<T> {


    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<T, T> client(String url) {
        return restTemplate.getForObject(url, Map.class);
    }
}
