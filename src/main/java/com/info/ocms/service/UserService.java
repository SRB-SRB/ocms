package com.info.ocms.service;

import com.info.ocms.dto.UserRequest;
import com.info.ocms.dto.UserResponse;
import com.info.ocms.ropository.UserRepo;

import java.util.List;

public interface UserService {


    UserResponse createUser(UserRequest userRequest);
    UserResponse getById(Long id);
    List<UserResponse> getAll();
    UserResponse updateUser(UserRequest userRequest);
    void deleteById(Long id);
    UserResponse promoteToInstructor(Long userId);
    UserResponse demoteToStudent(Long userId);



}
