package com.info.ocms.service.serviceImpl;

import com.info.ocms.constants.GlobalRole;
import com.info.ocms.dto.UserRequest;
import com.info.ocms.dto.UserResponse;
import com.info.ocms.model.User;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    @Override
    public UserResponse createUser(UserRequest userRequest) {
        userRequest.setGlobalRole("STUDENT");
       return mapToUserResponse(userRepo.save(mapToUser(userRequest)));

    }

    @Override
    public UserResponse getById(Long id) {
       return mapToUserResponse(userRepo.findById(id).orElseThrow(()->new RuntimeException("User doesn't EXIST")));
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
        User existingUser=userRepo.findById(userRequest.getId()).orElseThrow(()->new RuntimeException("User doesn't EXIST"));
        existingUser.setName(userRequest.getName());
        existingUser.setContact(userRequest.getContact());
        existingUser.setEmail(userRequest.getEmail());
        existingUser.setPassword(userRequest.getPassword());
       return mapToUserResponse( userRepo.save(existingUser));
    }

    @Override
    public void deleteById(Long id) {
        userRepo.deleteById(id);
    }


    public User mapToUser(UserRequest userRequest){
        User user =new User();
        user.setId(userRequest.getId());
        user.setName(userRequest.getName());
        user.setContact(userRequest.getContact());
        user.setEmail(userRequest.getEmail());
        user.setPassword(userRequest.getPassword());
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
