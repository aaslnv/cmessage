package kz.cmessage.core.user.repository;

import kz.cmessage.core.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsernameAndDeletedIsFalse(String username);

}
