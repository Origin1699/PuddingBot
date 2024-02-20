package top.ikaori.bot.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author origin
 */
@Data
@Entity
@Table(name = "pic_search")
public class PicSearchEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column
    private String md5;
    @Column
    private String url;
    @Column
    private String msg;
}
