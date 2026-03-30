package com.info.ocms.controller;


import com.info.ocms.dto.UserRequest;
import com.info.ocms.dto.UserResponse;
import com.info.ocms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

@PostMapping
    public UserResponse createUser(UserRequest userRequest){
    return userService.createUser(userRequest);
}
@GetMapping("/{id}")
    public UserResponse getById(@PathVariable Long id){
    return userService.getById(id);
}
@GetMapping
    public List<UserResponse> getAll(){
    return userService.getAll();
}
@PutMapping
    public UserResponse updateUser(UserRequest userRequest){
    return userService.updateUser(userRequest);
}
@DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id){
    userService.deleteById(id);
}

}
