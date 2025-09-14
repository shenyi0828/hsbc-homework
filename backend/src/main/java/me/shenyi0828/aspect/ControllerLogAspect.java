package me.shenyi0828.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Controller interface logging aspect
 * Logs request path, parameters, and execution time for all controller methods
 */
@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Around advice for all controller methods
     * Logs request information and execution time in a single line
     */
    @Around("execution(* me.shenyi0828.controller.*.*(..))")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        // Get request information
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = joinPoint.getArgs();
        
        // Prepare request info
        String requestPath = request != null ? request.getRequestURI() : "unknown";
        String httpMethod = request != null ? request.getMethod() : "unknown";
        String queryString = request != null ? request.getQueryString() : null;
        
        // Prepare parameters info with parameter names
        StringBuilder paramsInfo = new StringBuilder();
        if (args != null && args.length > 0) {
            String[] paramNames = getParameterNames(joinPoint);
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (arg != null && !isServletObject(arg)) {
                    if (paramsInfo.length() > 0) {
                        paramsInfo.append(", ");
                    }
                    String paramName = (paramNames != null && i < paramNames.length) ? paramNames[i] : "param" + i;
                    try {
                        String paramJson = objectMapper.writeValueAsString(arg);
                        paramsInfo.append(paramName).append(": ").append(paramJson);
                    } catch (Exception e) {
                        paramsInfo.append(paramName).append(": ").append(arg.toString());
                    }
                }
            }
        }
        
        Object result;
        try {
            // Execute the method
            result = joinPoint.proceed();
            
            // Calculate execution time
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Log in single line - successful completion
            String logMessage = String.format("[API] %s %s | Method: %s.%s | Params: {%s} | Query: %s | Time: %dms | Status: SUCCESS",
                    httpMethod, requestPath, className, methodName, 
                    paramsInfo.toString(), queryString != null ? queryString : "none", executionTime);
            log.info(logMessage);
            
        } catch (Exception e) {
            // Calculate execution time for failed requests
            long executionTime = System.currentTimeMillis() - startTime;
            
            // Log in single line - error
            String logMessage = String.format("[API] %s %s | Method: %s.%s | Params: {%s} | Query: %s | Time: %dms | Status: ERROR | Error: %s",
                    httpMethod, requestPath, className, methodName, 
                    paramsInfo.toString(), queryString != null ? queryString : "none", executionTime, e.getMessage());
            log.error(logMessage);
            
            throw e;
        }
        
        return result;
    }
    
    /**
     * Check if the object is a servlet-related object that should not be logged
     */
    private boolean isServletObject(Object obj) {
        return obj instanceof HttpServletRequest || 
               obj instanceof HttpServletResponse ||
               obj.getClass().getName().startsWith("org.springframework.web");
    }
    
    /**
     * Get parameter names from method signature
     */
    private String[] getParameterNames(ProceedingJoinPoint joinPoint) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Parameter[] parameters = method.getParameters();
            String[] paramNames = new String[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                paramNames[i] = parameters[i].getName();
            }
            return paramNames;
        } catch (Exception e) {
            log.debug("Failed to get parameter names: {}", e.getMessage());
            return null;
        }
    }
}