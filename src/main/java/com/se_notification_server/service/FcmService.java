package com.se_notification_server.service;

import com.google.firebase.messaging.*;
import com.se_notification_server.domain.AccountTagMapping;
import com.se_notification_server.repository.FcmRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
public class FcmService {
    private final FcmRepository fcmRepository;

    //@Autowired
    public FcmService(FcmRepository fcmRepository) {
        this.fcmRepository = fcmRepository;
    }


    public Optional<AccountTagMapping> findOne(Long userId) {
        return fcmRepository.findById(userId);
    }

    public Long save(AccountTagMapping accountTagMapping) {
        doubleCheck(accountTagMapping);
        fcmRepository.save(accountTagMapping);
        return accountTagMapping.getAccountId();
    }

    public void doubleCheck(AccountTagMapping accountTagMapping) {
        if(fcmRepository.existsAccountTagMappingByToken(accountTagMapping.getToken())) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    public List<String> getTokenList(List<Long> accountIdList) {
        List<String> tokenList = new ArrayList<>();
        for (Long accountId:accountIdList) {
            String token = fcmRepository.findById(accountId).get().getToken();
            tokenList.add(token);
        }
        return tokenList;
    }

    public BatchResponse send(List<String> sendTokens, String title, String msg) throws FirebaseMessagingException {

        MulticastMessage message = MulticastMessage.builder()
                .putData("title", title)
                .putData("content", msg)
                .addAllTokens(sendTokens)
                .build();

        BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(message);

        System.out.println(response.getSuccessCount() + " messages were sent successfully");

        return response;
    }
}
