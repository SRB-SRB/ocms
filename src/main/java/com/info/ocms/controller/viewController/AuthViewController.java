package com.info.ocms.controller.viewController;

import com.info.ocms.dto.UserRequest;
import com.info.ocms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth/view")
@RequiredArgsConstructor
public class AuthViewController {
    private final UserService userService;

    @GetMapping("/login")
    public String login(){
     return "user/login";
 }
    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("userRequest", new UserRequest());
        return "user/register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("userRequest") UserRequest userRequest){
        userService.createUser(userRequest);
        return "redirect:/auth/view/login";
    }

}
