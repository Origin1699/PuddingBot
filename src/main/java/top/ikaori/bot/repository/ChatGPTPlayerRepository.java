package top.ikaori.bot.repository;

import top.ikaori.bot.entity.chatgpt.ChatGPTPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author origin
 */
@Repository
public interface ChatGPTPlayerRepository extends JpaRepository<ChatGPTPlayer, Long> {

    void deleteByName(String role);
    ChatGPTPlayer findByName(String role);
    void deleteByType(int type);
}
