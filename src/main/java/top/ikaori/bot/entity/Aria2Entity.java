package top.ikaori.bot.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @author origin
 */
@Data
@Entity
@Table(name = "tb_aria2")
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
