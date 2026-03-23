package com.dems.utils;

public class UserUtil {
    /**
     * 检查用户工号是否符合正则
     * @param userId
     * @param regex
     * @return
     */

    public static boolean checkUserId(String userId,String regex){
       return userId.matches(regex);
    }


}
