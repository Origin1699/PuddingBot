package top.ikaori.bot.plugins.aria2;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.ikaori.bot.entity.dto.Aria2DTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author origin
 */
@Component
public class Aria2Util {

    private RestTemplate template;

    @Value("${bot-config.aria2.url}")
    private String url;

    @Value("${bot-config.aria2.token}")
    private String token;

    private ObjectMapper objectMapper = new ObjectMapper();

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
        Aria2Command aria2Command = new Aria2Command().setToken("9529c3ca674d8810a4bc").setMethod(Aria2CommandType.getGlobalStat.value).addParam(queryParameter);
        return exec(aria2Command);
    }

    public Aria2DTO getTellWaiting() {
        Aria2Command aria2Command = new Aria2Command().setToken("9529c3ca674d8810a4bc").setMethod(Aria2CommandType.getGlobalStat.value).addParam(queryParameter);
        return exec(aria2Command);
    }

    public Aria2DTO getTellActive() {
        Aria2Command aria2Command = new Aria2Command(token).setMethod(Aria2CommandType.tellActive.value).addParam(queryParameter);
        return exec(aria2Command);
    }

    public Aria2DTO addUrl(String magnetUrl) {
        Aria2Command aria2Command = new Aria2Command(token).setMethod(Aria2CommandType.addUri.value).addParam(List.of(magnetUrl));
        return execAddUri(aria2Command);
    }

    private Aria2DTO execAddUri(Aria2Command aria2Command) {
        try {
            ResponseEntity<String> rest = template.postForEntity(url, buildJson(aria2Command), String.class);
            String body = rest.getBody();
//            Map map = objectMapper.readValue(body, Map.class);
            return new Aria2DTO();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Aria2DTO command(Aria2CommandType type) {
        switch (type) {
            default -> {
                return exec(buildTaskQueryCommand(type));
            }

        }

    }

    private Aria2Command buildTaskQueryCommand(Aria2CommandType type) {
        return new Aria2Command(token).setMethod(type.value).addParam(-1).addParam(1000).addParam(queryParameter);
    }


    public Aria2DTO exec(Aria2Command aria2Command) {

        try {
            ResponseEntity<String> rest = template.postForEntity(url, buildJson(aria2Command), String.class);
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
}
