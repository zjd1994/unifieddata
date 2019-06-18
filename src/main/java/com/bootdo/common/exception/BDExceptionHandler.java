package com.bootdo.common.exception;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bootdo.common.config.Constant;
import com.bootdo.common.utils.R;

/**
 * 异常处理器
 */
@RestControllerAdvice
public class BDExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());
//    @Autowired
//    LogService logService;
//
//    /**
//     * 自定义异常
//     */
//    @ExceptionHandler(BDException.class)
//    public R handleBDException(BDException e) {
//        logger.error(e.getMessage(), e);
//        R r = new R();
//        r.put("code", e.getCode());
//        r.put("msg", e.getMessage());
//        return r;
//    }
//
//    @ExceptionHandler(DuplicateKeyException.class)
//    public R handleDuplicateKeyException(DuplicateKeyException e) {
//        logger.error(e.getMessage(), e);
//        return R.error("数据库中已存在该记录");
//    }
//
//    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
//    public R noHandlerFoundException(org.springframework.web.servlet.NoHandlerFoundException e) {
//        logger.error(e.getMessage(), e);
//        return R.error(404, "没找找到页面");
//    }

    @ExceptionHandler(AuthorizationException.class)
    public Object handleAuthorizationException(AuthorizationException e, HttpServletRequest request) {
         return R.error(Constant.STATUS_UNAUTH, "未授权");
    }


    @ExceptionHandler({Exception.class})
    public Object handleException(Exception e, HttpServletRequest request) {
//        LogDO logDO = new LogDO();
//        logDO.setGmtCreate(new Date());
//        logDO.setOperation(Constant.LOG_ERROR);
//        logDO.setMethod(request.getRequestURL().toString());
//        logDO.setParams(e.toString());
//        UserDO current = ShiroUtils.getUser();
//        if(null!=current){
//            logDO.setUserId(current.getUserId());
//            logDO.setUsername(current.getUsername());
//        }
//        logService.save(logDO);
        logger.error(e.getMessage(), e);
        return R.error(Constant.STATUS_SERVERERROR, "服务器错误，请联系管理员");
    }
}
