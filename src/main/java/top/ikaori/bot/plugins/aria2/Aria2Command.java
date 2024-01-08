package top.ikaori.bot.plugins.aria2;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author origin
 */
@Accessors(chain = true)
@Data
@NoArgsConstructor
public class Aria2Command {

    private String id = UUID.randomUUID().toString();
    private final String jsonrpc = "2.0";

    private String method;
    private List<Object> params = new ArrayList<>();

    public Aria2Command(String token) {
        params.add(String.format("token:%s", token));
    }

    public Aria2Command setToken(String token) {
        params.add(String.format("token:%s", token));
        return this;
    }

    public Aria2Command addParam(Object object) {
        params.add(object);
        return this;
    }

}
