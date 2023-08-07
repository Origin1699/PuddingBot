package life.kaori.bot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * author: origin 2023/7/6 16:57
 */
@Data
@Entity
@Table(name = "tb_plugin")
@NoArgsConstructor
public class PluginEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String name;
    @Column
    private String alias;
    @Column
    private boolean enable;

    public PluginEntity(String name, String alias, boolean enable) {
        this.name = name;
        this.alias = alias;
        this.enable = enable;
    }
}
