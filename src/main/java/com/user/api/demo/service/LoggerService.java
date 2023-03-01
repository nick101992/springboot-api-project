package com.user.api.demo.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import com.user.api.demo.model.UserRoleRequest;

@Service
public class LoggerService {
	    private static final Logger requestLogger = LoggerFactory.getLogger("requestLogger");
	    private static final Logger securityLogger = LoggerFactory.getLogger("securityLogger");

	    public void logRequest(HttpServletRequest request, UserRoleRequest userRoleRequest) {
	    	String transactionId = MDC.get("transactionId");
	    	String clientIp = request.getRemoteAddr();
	    	String method = request.getMethod();
	    	String url = request.getRequestURL().toString();
	    	Map<String, String[]> parameters = request.getParameterMap();
	    	StringBuilder parametersString = new StringBuilder();
	    	for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
	    	    parametersString.append(entry.getKey()).append(":");
	    	    for (String value : entry.getValue()) {
	    	        parametersString.append(value).append(",");
	    	    }
	    	    parametersString.deleteCharAt(parametersString.length() - 1);
	    	    parametersString.append(";");
	    	}
	    	requestLogger.info("Transaction ID: {}, Client IP: {}, Method: {}, URL: {}, Parameters: {}, Request Body: {}",
	    	transactionId, clientIp, method, url, parametersString.toString(), userRoleRequest);
	    	}

	    public void logSecurity(String username, String message) {
	    	securityLogger.info("Username: {}, Message: {}", username, message);
	    }
}
