package top.ikaori.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.ikaori.bot.entity.steam.SteamEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SteamRepository extends JpaRepository<SteamEntity, Integer> {
    List<SteamEntity> findByGroupId(Long groupId);

    Optional<SteamEntity> findByGroupIdAndUserId(Long groupId, Long userId);
    @Modifying
    @Transactional
    int deleteByGroupIdAndUserId(Long groupId, Long userId);

}
