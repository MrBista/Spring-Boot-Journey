package com.bisma.foundation.fundamental_spring_boot.services;


import com.bisma.foundation.fundamental_spring_boot.dto.req.RegisterReqDto;

public interface AuthService {
    void registerUser(RegisterReqDto userReqDto);
}
