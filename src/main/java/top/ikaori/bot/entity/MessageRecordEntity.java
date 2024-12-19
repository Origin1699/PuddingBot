package top.ikaori.bot.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author origin
 */
@Getter
@Setter
@Entity
@Table(name = "message_record")
public class MessageRecordEntity {
    @Id
    @GeneratedValue(generator = "system_uuid")
    @GenericGenerator(name = "system_uuid", strategy = "uuid")
    private String id;
    @Column(nullable = false)
    private Long userId;
    private Long groupId;
    @Column(nullable = false)
    private String message;
    @Column(nullable = false)
    private LocalDate time;


    public MessageRecordEntity() {
    }

    public MessageRecordEntity(Long userId, String message) {
        this.userId = userId;
        this.message = message;
        this.time = LocalDate.now();
    }

    public MessageRecordEntity(Long userId, Long groupId, String message) {
        this.userId = userId;
        this.groupId = groupId;
        this.message = message;
        this.time = LocalDate.now();
    }
}
