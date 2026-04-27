package com.info.ocms.util;

import com.info.ocms.model.User;
import com.info.ocms.ropository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtils {
    private final UserRepo userRepo;
    public User getCurrentUser(){
        String email= SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByEmail(email).orElseThrow(()->new RuntimeException("User Not Found"));
    }
}
