package top.ikaori.bot.repository;

import top.ikaori.bot.entity.chatgpt.ChatGPTEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * @author origin
 */
@Repository
public interface ChatGPTRepository extends JpaRepository<ChatGPTEntity, Long> {

    void deleteByUserid(Long userid);

    ChatGPTEntity findByUserid(Long userid);
}
