package kz.cmessage.core.blackList.repository;

import kz.cmessage.core.blackList.model.BlackList;
import kz.cmessage.core.blackList.model.BlackListPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListRepository extends JpaRepository<BlackList, BlackListPk> {


    boolean existsByPrimaryKeyUserIdAndPrimaryKeyBlockedUserId(Long userId, Long blockedUserId);

}
