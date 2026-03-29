package com.info.ocms.dto;

import com.info.ocms.constants.GlobalRole;
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
    private String password;
    private String globalRole;
}
