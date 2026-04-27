package com.info.ocms.service.serviceImpl;

import com.info.ocms.model.User;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepo userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= userRepo.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User Not Found: "+ username));
        return new CustomUserDetails(user);
    }
}
