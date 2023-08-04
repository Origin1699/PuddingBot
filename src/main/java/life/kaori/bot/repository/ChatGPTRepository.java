package life.kaori.bot.repository;

import life.kaori.bot.entity.chatgpt.ChatGPTEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


/**
 * author: origin
 */
@Repository
public interface ChatGPTRepository extends JpaRepository<ChatGPTEntity, Long> {

    void deleteByUserid(Long userid);
}
