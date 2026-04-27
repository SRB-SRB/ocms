package com.info.ocms.service.serviceImpl;

import com.info.ocms.constants.GlobalRole;
import com.info.ocms.dto.UserRequest;
import com.info.ocms.dto.UserResponse;
import com.info.ocms.exception.DuplicateEmailException;
import com.info.ocms.model.User;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if(userRepo.existsByEmail(userRequest.getEmail())){
            throw new DuplicateEmailException(userRequest.getEmail());
        }
        userRequest.setGlobalRole("STUDENT");
       return mapToUserResponse(userRepo.save(mapToUser(userRequest)));
    }

    @Override
    public UserResponse getById(Long id) {
       return mapToUserResponse(userRepo.findById(id).orElseThrow(()->new RuntimeException("User Not Found")));
    }

    @Override
    public List<UserResponse> getAll() {
        List<UserResponse> userResponses=new ArrayList<>();
        for(User user:userRepo.findAll()){
            userResponses.add(mapToUserResponse(user));
        }
        return userResponses;
    }

    @Override
    public UserResponse updateUser(UserRequest userRequest) {
        User existingUser=userRepo.findById(userRequest.getId()).orElseThrow(()->new RuntimeException("User Not Found"));
        if(!existingUser.getEmail().equals(userRequest.getEmail())&& userRepo.existsByEmail(userRequest.getEmail())){
            throw new DuplicateEmailException(userRequest.getEmail());
        }

        existingUser.setName(userRequest.getName());
        existingUser.setContact(userRequest.getContact());
        existingUser.setEmail(userRequest.getEmail());
    if(userRequest.getPassword()!=null && !userRequest.getPassword().isBlank()){
        existingUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
    }
       return mapToUserResponse( userRepo.save(existingUser));
    }

    @Override
    public void deleteById(Long id) {
        if(!userRepo.existsById(id)){
            throw new RuntimeException("User Not Found");
        }
        userRepo.deleteById(id);
    }

    @Override
    public UserResponse promoteToInstructor(Long userId) {
        User user=userRepo.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
        if(user.getGlobalRole()==GlobalRole.INSTRUCTOR){
            throw new RuntimeException("User is Already an Instructor");
        }
        if(user.getGlobalRole()==GlobalRole.ADMIN){
            throw new RuntimeException("Cannot change Role of an Admin");
        }
        user.setGlobalRole(GlobalRole.INSTRUCTOR);
        return mapToUserResponse(userRepo.save(user));
    }

    @Override
    public UserResponse demoteToStudent(Long userId) {
        User user=userRepo.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
        if(user.getGlobalRole()==GlobalRole.STUDENT){
            throw new RuntimeException("User is Already a Student");
        }
        if(user.getGlobalRole()==GlobalRole.ADMIN){
            throw new RuntimeException("Cannot change Role of an Admin");
        }
        user.setGlobalRole((GlobalRole.STUDENT));
        return mapToUserResponse(userRepo.save(user));
    }


    public User mapToUser(UserRequest userRequest){
        User user =new User();
        user.setName(userRequest.getName());
        user.setContact(userRequest.getContact());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        user.setGlobalRole(GlobalRole.valueOf(userRequest.getGlobalRole()));
        return user;
    }
    public UserResponse mapToUserResponse(User user){
        UserResponse userResponse=new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setName(user.getName());
        userResponse.setContact(user.getContact());
        userResponse.setEmail(user.getEmail());
        userResponse.setGlobalRole(user.getGlobalRole().toString());
        return userResponse;

    }


}
