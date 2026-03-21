package com.dems.service;

import com.dems.pojo.LoginInfo;
import com.dems.pojo.Result;
import com.dems.pojo.User;

public interface UserService {
    LoginInfo login(String userId, String password);

    void register(User user);

    void bind(Integer buildingId, String roomId);

    void updatePass(String password);

    LoginInfo info();
}
