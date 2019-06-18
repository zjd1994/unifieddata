package com.bootdo.common.config;

public class Constant {
    //服务器错误
    public static String STATUS_SERVERERROR = "500";
    //未授权
    public static String STATUS_UNAUTH = "403";
    //未登录
    public static String STATUS_UNLOGIN= "10000";
    //操作成功
    public static String STATUS_SUCESS = "200";
    //操作失败
    public static String STATUS_FAILED = "201";
    //校验未通过
    public static String STATUS_UNCHECKPASS = "402";
    
    //缓存方式
    public static String CACHE_TYPE_REDIS ="redis";
    
    //部门根节点id
    public static Long DEPT_ROOT_ID = 0l;

    //数据类型
    public static String JAVA_DATA_TYPE_OF_STRING = "java.lang.String";

    public static String JAVA_DATA_TYPE_OF_INTEGER = "java.lang.Integer";

    public static String JAVA_DATA_TYPE_OF_DOUBLE = "java.lang.Double";

}
