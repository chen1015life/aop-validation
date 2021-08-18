package com.wanchen.aopvalidation;

import com.wanchen.aopvalidation.exception.BadRequestException;
import com.wanchen.aopvalidation.model.Request;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.groups.Default;
import java.util.*;

@Aspect
@Component
@Log4j2
public class ValidationAop {
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Resource
    private LocalValidatorFactoryBean localValidatorFactoryBean;

    @Pointcut(
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)" +
                    "||@annotation(org.springframework.web.bind.annotation.GetMapping)" +
                    "||@annotation(org.springframework.web.bind.annotation.PostMapping)" +
                    "||@annotation(org.springframework.web.bind.annotation.PutMapping)"
    )
//    @Pointcut("execution(* com.wanchen.validation.controller..*(..))") // 方式2
    public void webLog() {}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint){
        log.info("===========================Validation AOP Start============================");
        startTime.set(System.currentTimeMillis());
        //接收到請求，紀錄請求內容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        // 紀錄下請求內容
        log.info("Request URL: {}", request.getRequestURL());
        log.info("Request IP: {}", getIpAddr(request));
        log.info("Request Method: {}", request.getMethod());
        log.info("Request Header: {}", getHeadersInfo(request));
        log.info("Request Body: {}", getBodyInfo(request));
        log.info("Application Method: {}", joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
    }

    /**
     * 驗證參數
     *
     * @param joinPoint
     * @param request
     */
    @Before("webLog() && args(request,..)")
    public void validateParameter(JoinPoint joinPoint, Request request) {
        Set<ConstraintViolation<Request>> validErrors = this.localValidatorFactoryBean.validate(request, new Class[]{Default.class});
        Iterator iterator = validErrors.iterator();
        StringBuilder errorMsg = new StringBuilder();

        while (iterator.hasNext()) {
            ConstraintViolation constraintViolation = (ConstraintViolation) iterator.next();
            String error = constraintViolation.getPropertyPath() + ":" + constraintViolation.getMessage();
            errorMsg.append(iterator.hasNext() ? error + "; " : error);
        }
        if (!validErrors.isEmpty()) {
            throw new BadRequestException(errorMsg.toString());
        }
    }

    /**
     * 目标方法返回后调用
     *
     * @author winter
     *
     * */
    @AfterReturning(returning = "result", pointcut = "webLog()")
    public void doAfterReturning(Object result){
        try{
            log.info("Response: {}", result);
            log.info( "Time: {} ms", System.currentTimeMillis() - startTime.get());
        }catch (Exception exception){
            log.info("***记录日志失败doAfterReturning***");
        }
    }

    @AfterThrowing(pointcut = "webLog()", throwing = "ex")
    public void addAfterThrowingLogger(JoinPoint joinPoint, Exception ex) {
        log.error("例外錯誤: {}", ex.getMessage());
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    private Map<String, String> getBodyInfo(HttpServletRequest request){
        Map<String, String> map = new HashMap<>();

        Enumeration bodyNames = request.getParameterNames();

        while (bodyNames.hasMoreElements()) {
            String key = (String) bodyNames.nextElement();
            String value = request.getParameter(key);
            map.put(key, value);
        }
        return map;
    }

    /**
     * 获取登录用户远程主机ip地址
     *
     * @param request
     * @return
     */
    private String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
