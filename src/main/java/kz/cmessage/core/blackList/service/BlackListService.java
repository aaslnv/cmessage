package kz.cmessage.core.blackList.service;

import kz.cmessage.core.blackList.repository.BlackListRepository;
import kz.cmessage.core.user.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlackListService {

    private BlackListRepository blackListRepository;

    public BlackListService(BlackListRepository blackListRepository) {
        this.blackListRepository = blackListRepository;
    }

    public List<User> getBlockedUsersByUserId(Long userId) {
        return blackListRepository.findAllByPrimaryKeyUserId(userId).stream()
                .map(blackList -> blackList.getPrimaryKey().getUser())
                .collect(Collectors.toList());
    }
}
