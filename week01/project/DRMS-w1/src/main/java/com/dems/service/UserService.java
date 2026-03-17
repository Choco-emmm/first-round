package com.dems.service;

import com.dems.pojo.Result;
import com.dems.pojo.User;

public interface UserService {
    User login(String userId, String password);

    Result register(User user);

    Result bind(Integer buildingId, String roomId, String userId);

    Result updatePass(String password, String userId);
}
