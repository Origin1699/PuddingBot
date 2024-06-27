package top.ikaori.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.ikaori.bot.entity.steam.SteamGameSubsEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface SteamGameSubsRepository extends JpaRepository<SteamGameSubsEntity, Long> {
    <S extends SteamGameSubsEntity> Optional<S> findByGroupIdAndUserIdAndAppid(Long groupId, Long userId, Integer appid);
    List<SteamGameSubsEntity> findByAppid(int appid);
    List<SteamGameSubsEntity> findByGroupIdAndAppid(Long groupId, int appid);
    @Modifying
    @Transactional
    int deleteByAppid(Integer id);
    @Modifying
    @Transactional
    int deleteByGroupIdAndAppid(Long groupId, Integer id);
}