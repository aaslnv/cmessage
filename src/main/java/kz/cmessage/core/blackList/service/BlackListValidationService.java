package kz.cmessage.core.blackList.service;

import kz.cmessage.core.blackList.repository.BlackListRepository;
import org.springframework.stereotype.Service;

@Service
public class BlackListValidationService {

    private BlackListRepository blackListRepository;

    public BlackListValidationService(BlackListRepository blackListRepository) {
        this.blackListRepository = blackListRepository;
    }

    public boolean ifUserBlockedByUserByIds(Long blockedUserId, Long userId) {
        return blackListRepository.existsByPrimaryKeyUserIdAndPrimaryKeyBlockedUserId(blockedUserId, userId);
    }
}
