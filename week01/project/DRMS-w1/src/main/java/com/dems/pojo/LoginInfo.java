package com.dems.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginInfo {
    private String userId;
    private String username;
    private String token;
    private Integer role;
    private Integer buildingId;
    private String roomId;
    private String url;
}
