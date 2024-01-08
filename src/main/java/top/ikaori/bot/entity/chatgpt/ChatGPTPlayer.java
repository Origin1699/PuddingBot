package top.ikaori.bot.entity.chatgpt;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author origin
 */
@Data
@Table
@Entity(name = "chatgpt_player")
public class ChatGPTPlayer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private String name;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "player_id")
    private List<ChatGPTPrompt> list;

    @Column
    private int type;

    public ChatGPTPlayer() {
    }

    public ChatGPTPlayer(String name, List<ChatGPTPrompt> list, int type) {
        this.name = name;
        this.list = list;
        this.type = type;
    }
}
