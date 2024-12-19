package top.ikaori.bot.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author origin
 */
@Component
@RequiredArgsConstructor
public class RequestUtil<T> {

    private final RestTemplate restTemplate;

    public Map<T, T> client(String url) {
        return restTemplate.getForObject(url, Map.class);
    }
}
