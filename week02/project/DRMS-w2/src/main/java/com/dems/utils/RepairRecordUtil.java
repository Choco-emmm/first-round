package com.dems.utils;

public class RepairRecordUtil {
    public static boolean isEmpty(Object ...obj){
        if(obj==null|| obj.length == 0){
            //传入参数为空，直接返回
            return true;
        }
        for (Object o : obj) {
            if(o==null){
                //参数为空，返回true
                return true;
            }
            if(o instanceof String){
                //验证是否为空串
                if(((String) o).trim().isEmpty()){
                    return true;
                }
            }

        }
        //全部检查完了
        return false;
    }

    public static boolean BuildingIdIsWrong(Integer buildingId){
        return buildingId<0;
    }
}
