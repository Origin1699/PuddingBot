package life.kaori.bot.common.util;

import life.kaori.bot.entity.BanEntity;
import life.kaori.bot.repository.BanRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * author: origin
 */
@Component
public class BanUtil {
    private final BanRepository repository;


    public BanUtil(BanRepository repository) {
        this.repository = repository;
    }

    public boolean isBan(Long userId) {
        Optional<BanEntity> byUserId = repository.findByUserId(userId);
        return byUserId.isPresent();
    }

    public void ban(String userId) {
//        repository.save(new Ban())
    }
}
