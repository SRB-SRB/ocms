package com.info.ocms.dto;

import com.info.ocms.constants.GlobalRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private Long id;
    private String name;
    private String contact;
    private String email;
    @NotNull
    private String password;
    private String globalRole;
}
