package com.info.ocms.controller.viewController;

import com.info.ocms.dto.UserResponse;
import com.info.ocms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/view")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminViewController {
    private final UserService userService;

    @GetMapping("/users")
    public String getAllUsers(Model model){
        List<UserResponse> users=userService.getAll();
        model.addAttribute("users",users);
        return"admin/users";
    }

    @GetMapping("/users/{id}")
    public String getUser(@PathVariable Long id, Model model){
        UserResponse userResponse=userService.getById(id);
        model.addAttribute("user", userResponse);
        return "admin/viewUser";
    }
    @PostMapping("/users/{id}/promote")
    public String promoteToInstructor(@PathVariable Long id){
        userService.promoteToInstructor(id);
        return "redirect:/admin/view/users/"+id;
    }
    @PostMapping("/users/{id}/demote")
    public String demoteToStudent(@PathVariable Long id){
        userService.demoteToStudent(id);
        return"redirect:/admin/view/users/"+id;
    }
    @DeleteMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id){
        userService.deleteById(id);
        return "redirect:/admin/view/users";
    }

}
