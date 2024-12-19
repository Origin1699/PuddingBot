package top.ikaori.bot.common.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import top.ikaori.bot.entity.BanEntity;
import top.ikaori.bot.repository.BanRepository;

import java.util.Optional;

/**
 * @author origin
 */
@Component
@RequiredArgsConstructor
public class BanUtil {
    private final BanRepository repository;

    public boolean isBan(Long userId) {
        Optional<BanEntity> byUserId = repository.findByUserId(userId);
        return byUserId.isPresent();
    }

    public void ban(String userId) {
//        repository.save(new Ban())
    }
}
