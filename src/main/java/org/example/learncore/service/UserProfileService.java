package org.example.learncore.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.learncore.model.UserProfile;
import org.example.learncore.repository.UserProfileRepository;
import org.example.learncore.dto.ErrorCode;
import org.example.learncore.exception.AppException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Transactional
    public void resetProfile() {
        UserProfile profile = userProfileRepository.findAll().stream().findFirst().orElse(new UserProfile());
        profile.setUsername("nguyen_van_a");
        profile.setBio("Hello, I am a Java Dev!");
        userProfileRepository.save(profile);
        log.info("[Optimistic] Đã reset User Profile");
    }

    @Transactional
    public void updateBioOptimistic(String newBio) throws InterruptedException {
        log.info("[Optimistic] Transaction BẮT ĐẦU...");

        UserProfile profile = userProfileRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        log.info("[Optimistic] Đã ĐỌC ID: {}. Version: {}, Bio cũ: {}", profile.getId(), profile.getVersion(), profile.getBio());

        Thread.sleep(5000);

        profile.setBio(newBio);
        userProfileRepository.save(profile);
        log.info("[Optimistic] Update THÀNH CÔNG. Bio mới: {}", profile.getBio());
    }
}
