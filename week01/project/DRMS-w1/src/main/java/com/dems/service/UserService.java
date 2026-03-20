package com.dems.service;

import com.dems.pojo.LoginInfo;
import com.dems.pojo.Result;
import com.dems.pojo.User;

public interface UserService {
    LoginInfo login(String userId, String password);

    void register(User user);

    void bind(Integer buildingId, String roomId, String token);

    void updatePass(String password, String token);

    LoginInfo info(String token);
}
