package life.kaori.bot.entity.chatgpt;

import com.theokanning.openai.completion.chat.ChatMessageRole;
import lombok.Data;

import javax.persistence.*;

/**
 * author: origin
 */
@Data
@Table
@Entity(name = "chatgpt_prompt")
public class ChatGPTPrompt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private ChatMessageRole role;

    @Column
    private String content;

    public ChatGPTPrompt() {
    }

    public ChatGPTPrompt(ChatMessageRole role, String content) {
        this.role = role;
        this.content = content;
    }
}
