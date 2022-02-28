package disa.notification.service.entity;

import lombok.Data;

import javax.persistence.*;

@Entity(name = "notification_config")
@Data
@Table(name = "notification_config")
public class NotificationConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String province;
    private String partner;
    private String mailList;
    private boolean active;
}
