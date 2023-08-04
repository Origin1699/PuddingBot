package life.kaori.bot.entity.chatgpt;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

/**
 * author: origin
 */
@Data
@Table
@Entity(name = "chatgpt_player")
public class ChatGPTPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column
    private String name;


    @OneToMany
    @JoinColumn(name = "player_id")
    private List<ChatGPTPrompt> list;

    public ChatGPTPlayer() {
    }

    public ChatGPTPlayer(String name, List<ChatGPTPrompt> list) {
        this.name = name;
        this.list = list;
    }
}
