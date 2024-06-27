package top.ikaori.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import top.ikaori.bot.entity.steam.SteamGameEntity;

import java.util.Optional;

@Repository
public interface SteamGameRepository extends JpaRepository<SteamGameEntity, Long> {
    <S extends SteamGameEntity> Optional<S> findByAppid(int appid);
    @Modifying
    @Transactional
    void deleteByAppid(int appid);
}