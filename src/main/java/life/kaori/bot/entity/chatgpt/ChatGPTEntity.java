package life.kaori.bot.entity.chatgpt;

import lombok.Data;

import javax.persistence.*;

/**
 * author: origin
 */
@Data
@Table
@Entity(name = "chatgpt_entity")
public class ChatGPTEntity {

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
