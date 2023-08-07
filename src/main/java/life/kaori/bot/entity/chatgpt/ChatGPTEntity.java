package life.kaori.bot.entity.chatgpt;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * author: origin
 */
@Data
@Table
@Entity(name = "chatgpt_entity")
public class ChatGPTEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long userid;

    @OneToOne
    @JoinColumn(name = "player_id")
    private ChatGPTPlayer player;

    public ChatGPTEntity() {
    }

    public ChatGPTEntity(Long userid, ChatGPTPlayer player) {
        this.userid = userid;
        this.player = player;
    }
}
