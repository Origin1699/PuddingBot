package top.ikaori.bot.entity.steam;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity(name = "steam_game_subs")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class SteamGameSubsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private Long groupId;
    @Column
    private Long userId;
    @Column
    private Integer appid;
    @Column
    private boolean informed;
}
