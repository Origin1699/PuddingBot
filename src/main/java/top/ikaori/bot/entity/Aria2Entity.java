package top.ikaori.bot.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author origin
 */
@Entity
@Table(name = "aria2")
@Getter
@Setter
public class Aria2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String gid;
    @Column
    private String name;
    @Column
    private String size;
    @Column
    private String magnet;
    @Column
    private Long executor;

}
