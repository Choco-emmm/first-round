package com.dems.mapper;

import com.dems.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectUserByIdPass(String userId, String password);

    void insertUser(User user);

    User selectUserById(String userId);

    void updateBindById(User user);

    void updatePassById(User user);
}
