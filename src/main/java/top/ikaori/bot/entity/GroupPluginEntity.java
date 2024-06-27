package top.ikaori.bot.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 *
 * @author origin
 */
@Entity
@Table(name = "group_plugin")
@Getter
@Setter
public class GroupPluginEntity implements Serializable {

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
