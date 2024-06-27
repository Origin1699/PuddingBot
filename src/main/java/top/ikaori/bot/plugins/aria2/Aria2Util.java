package top.ikaori.bot.plugins.aria2;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import top.ikaori.bot.config.BotConfig;
import top.ikaori.bot.core.exception.BotException;
import top.ikaori.bot.core.exception.ExceptionMsg;
import top.ikaori.bot.entity.dto.Aria2DTO;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author origin
 */
@Component
public class Aria2Util {

    private RestTemplate template;
    
    private BotConfig.Plugins.Aria2Config config;
    
    private ObjectMapper objectMapper;

    private final String[] queryParameter = new String[]{
            "gid",
            "dir",
            "totalLength",
            "completedLength",
            "uploadSpeed",
            "downloadSpeed",
            "connections",
            "numSeeders",
            "seeder",
            "status",
            "errorCode",
            "verifiedLength",
            "verifyIntegrityPending",
            "files",
            "bittorrent",
            "infoHash"
    };

    public Aria2DTO getGlobalStat() {
        Aria2Command aria2Command = new Aria2Command(config.getToken()).setMethod(Aria2CommandType.getGlobalStat.value).addParam(queryParameter);
        return exec(aria2Command);
    }

    public Aria2DTO getTellWaiting() {
        Aria2Command aria2Command = new Aria2Command(config.getToken()).setMethod(Aria2CommandType.tellWaiting.value).addParam(-1).addParam(1000).addParam(queryParameter);
        return exec(aria2Command);
    }

    public Aria2DTO getTellActive() {
        Aria2Command aria2Command = new Aria2Command(config.getToken()).setMethod(Aria2CommandType.tellActive.value).addParam(queryParameter);
        return exec(aria2Command);
    }

    public String addUrl(String magnetUrl) throws JsonProcessingException {
        Aria2Command aria2Command = new Aria2Command(config.getToken()).setMethod(Aria2CommandType.addUri.value).addParam(List.of(magnetUrl));
        return execNoDto(aria2Command);
    }

    public String start(String gid) throws JsonProcessingException {
        Aria2Command aria2Command = new Aria2Command(config.getToken()).setMethod(Aria2CommandType.unpause.value).addParam(gid);
        return execNoDto(aria2Command);
    }

    public String stop(String gid) throws JsonProcessingException {
        Aria2Command aria2Command = new Aria2Command(config.getToken()).setMethod(Aria2CommandType.forcePause.value).addParam(gid);
        return execNoDto(aria2Command);
    }

    public String delete(String gid) throws JsonProcessingException  {
        Aria2Command aria2Command = new Aria2Command(config.getToken()).setMethod(Aria2CommandType.forceRemove.value).addParam(gid);
        return execNoDto(aria2Command);
    }


    private String execNoDto(Aria2Command aria2Command) throws JsonProcessingException {
        try {
            ResponseEntity<String> rest = template.postForEntity(config.getUrl(), buildJson(aria2Command), String.class);
            if (rest.getStatusCode() == HttpStatus.OK) {
                String body = rest.getBody();
                Map map = objectMapper.readValue(body, Map.class);
                return (String) map.get("result");
            }
            throw ExceptionMsg.ARIA2_ERROR;
        } catch (HttpClientErrorException httpError) {
            String body = httpError.getResponseBodyAsString(StandardCharsets.UTF_8);
            Map map = objectMapper.readValue(body, Map.class);
            Map error = (Map) map.get("error");
            throw new BotException((String) error.get("message"));
        }
    }

    public Aria2DTO exec(Aria2Command aria2Command) {

        try {
            ResponseEntity<String> rest = template.postForEntity(config.getUrl(), buildJson(aria2Command), String.class);
            String body = rest.getBody();
            return objectMapper.readValue(body, Aria2DTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    public String buildJson(Aria2Command aria2Command) throws JsonProcessingException {
        return objectMapper.writeValueAsString(aria2Command);
    }

    @Autowired
    public void setTemplate(RestTemplate restTemplate) {
        this.template = restTemplate;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Autowired
    public void setConfig(BotConfig.Plugins.Aria2Config config) {
        this.config = config;
    }
}
