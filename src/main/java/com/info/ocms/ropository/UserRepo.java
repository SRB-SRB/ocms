package com.info.ocms.ropository;

import com.info.ocms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepo  extends JpaRepository<User,Long> {
}
