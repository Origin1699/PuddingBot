package top.ikaori.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ikaori.bot.entity.MessageRecordEntity;

import java.time.LocalDate;
import java.util.Collection;

@Repository
public interface MessageRecordRepository extends JpaRepository<MessageRecordEntity, String> {

    Collection<MessageRecordEntity> findAllByUserIdAndGroupIdAndTimeBetween(Long userId, Long groupId, LocalDate start, LocalDate end);

    Collection<MessageRecordEntity> findAllByGroupIdAndTimeBetween(Long groupId, LocalDate start, LocalDate end);
}
