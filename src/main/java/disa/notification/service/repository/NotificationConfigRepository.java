package disa.notification.service.repository;

import disa.notification.service.entity.NotificationConfig;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NotificationConfigRepository extends CrudRepository<NotificationConfig,Integer> {

    List<NotificationConfig> findByActiveTrue();
}
