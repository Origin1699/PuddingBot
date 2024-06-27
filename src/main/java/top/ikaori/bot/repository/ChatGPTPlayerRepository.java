package top.ikaori.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.ikaori.bot.entity.chatgpt.ChatGPTPlayer;

/**
 * @author origin
 */
@Repository
public interface ChatGPTPlayerRepository extends JpaRepository<ChatGPTPlayer, Long> {
    @Modifying
    @Transactional
    void deleteByName(String role);
    ChatGPTPlayer findByName(String role);
    @Modifying
    @Transactional
    void deleteByType(int type);
}
