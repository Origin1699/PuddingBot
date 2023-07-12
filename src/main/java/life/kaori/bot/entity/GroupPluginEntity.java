package life.kaori.bot.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * author: origin
 */
@Data
@Entity
@Table(name = "tb_plugin_enable")
public class GroupPluginEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private Long groupId;
    @Column
    private String pluginName;
    @Column
    private boolean enable;
}
