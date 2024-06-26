package top.ikaori.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.ikaori.bot.entity.chatgpt.ChatGPTEntity;


/**
 * @author origin
 */
@Repository
public interface ChatGPTRepository extends JpaRepository<ChatGPTEntity, Long> {
    @Modifying
    @Transactional
    void deleteByUserid(Long userid);

    ChatGPTEntity findByUserid(Long userid);
}
