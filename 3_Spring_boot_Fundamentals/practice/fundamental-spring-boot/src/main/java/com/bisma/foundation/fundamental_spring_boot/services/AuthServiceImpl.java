package com.bisma.foundation.fundamental_spring_boot.services;

import com.bisma.foundation.fundamental_spring_boot.config.AppConfiguration;
import com.bisma.foundation.fundamental_spring_boot.dto.req.RegisterReqDto;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService{
    private final AppConfiguration appConfiguration;

    public AuthServiceImpl(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
    }

    @Override
    public void registerUser(RegisterReqDto userReqDto) {
        if (!appConfiguration.getFeature().getRegisWithEmailAvail() && userReqDto.getEmail().contains("@")) {
            throw new IllegalArgumentException("Mohon maaf Anda ga bisa register email tuk sementara waktu");
        }
    }
}
