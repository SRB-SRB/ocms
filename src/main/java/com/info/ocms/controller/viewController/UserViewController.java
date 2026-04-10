package com.info.ocms.controller.viewController;

import com.info.ocms.dto.UserRequest;
import com.info.ocms.dto.UserResponse;
import com.info.ocms.ropository.UserRepo;
import com.info.ocms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/user/view")
@RequiredArgsConstructor

public class UserViewController {
    private final UserService userService;
    private final UserRepo userRepo;

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "user/register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("userRequest") UserRequest userRequest){
        userService.createUser(userRequest);
        return "redirect:/user/view/register";
    }

    @GetMapping("/dashboard/{id}")
    public String getDashboard(@PathVariable Long id,Model model){
        UserResponse userResponse=userService.getById(id);
        model.addAttribute("userResponse",userResponse);
        model.addAttribute("userId",id);
        return "user/dashboard";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id,Model model){
        UserResponse userResponse=userService.getById(id);
        UserRequest userRequest=new UserRequest();
        userRequest.setId(userResponse.getId());
        userRequest.setName(userResponse.getName());
        userRequest.setContact(userResponse.getContact());
        userRequest.setEmail(userResponse.getEmail());
        model.addAttribute("userRequest",userRequest);
        model.addAttribute("userId",id);
        return "user/updateUserForm";
    }

    @PutMapping("/update/{id}")
    public String updateUser(@PathVariable Long id,@ModelAttribute("userRequest") UserRequest userRequest){
        userService.updateUser(userRequest);
        return "redirect:/user/view/dashboard/" + id;
    }
}
