package top.ikaori.bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import top.ikaori.bot.entity.PicSearchEntity;

import java.util.List;

/**
 * @author origin
 */

@Repository
public interface PicSearchRepository extends JpaRepository<PicSearchEntity, Integer> {
    List<PicSearchEntity> findByMd5(String md5);
}
