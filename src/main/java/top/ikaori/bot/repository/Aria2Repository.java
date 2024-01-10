package top.ikaori.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ikaori.bot.entity.Aria2Entity;

import java.util.Optional;

/**
 * @author origin
 */
@Repository
public interface Aria2Repository extends JpaRepository<Aria2Entity, Long> {
    Optional<Aria2Entity>  findById(Long id);
    Optional<Aria2Entity> findByGid(String gid);
}
