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
@Entity(name = "steam_game")
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
public class SteamGameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer appid;

    @Column
    private String name;

    @Column
    private String picUrl;

    @Column
    private String initialFormatted;

    @Column
    private String finalFormatted;

    @Column
    private Integer discountPercent;

}
