package org.elasticsearch.plugin.ghostboyzone.util;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.elasticsearch.common.io.stream.BytesStreamOutput;
import org.elasticsearch.common.logging.ESLogger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentBuilderString;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.http.HttpChannel;
import org.elasticsearch.http.HttpRequest;
import org.elasticsearch.plugin.ghostboyzone.config.ESAuthConfig;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;

public class ContentBuilder {
	
	public static XContentBuilder restContentBuilder(RestRequest request)  
            throws IOException {
        XContentType contentType = XContentType  
                .fromRestContentType(request.header("Content-Type"));  
        if (contentType == null) {  
            // try and guess it from the body, if exists  
            if (request.hasContent()) {  
                contentType = XContentFactory.xContentType(request.content());  
            }  
        }  
        if (contentType == null) {  
            // default to JSON  
            contentType = XContentType.JSON;  
        }  
        BytesStreamOutput out = new BytesStreamOutput();  
        XContentBuilder builder = new XContentBuilder(  
                XContentFactory.xContent(contentType), out);  
  
        if (request.paramAsBoolean("pretty", false)) {  
            builder.prettyPrint();  
        }  
        String casing = request.param("case");  
        if (casing != null && "camelCase".equals(casing)) {  
            builder.fieldCaseConversion(  
                    XContentBuilder.FieldCaseConversion.CAMELCASE);  
        } else {  
            builder.fieldCaseConversion(  
                    XContentBuilder.FieldCaseConversion.NONE);  
        }  
        return builder;  
    }
	
	public static void respForbidden(final HttpRequest request, final HttpChannel channel, ESLogger logger) {
		XContentBuilder builder;
		try {
			builder = ContentBuilder.restContentBuilder(request);
			builder.startObject()  
	        .field(new XContentBuilderString("status"), RestStatus.FORBIDDEN)  
	        .field(new XContentBuilderString("message"),  
	                "You are not login")  
	        .endObject();
			
			channel.sendResponse(  
	                new BytesRestResponse(RestStatus.FORBIDDEN, builder)); 
		} catch (IOException e) {
			if (logger != null) {
				logger.error("Get Exception in checkPermission: " + e.getMessage());
			}
			e.printStackTrace();
		}
	}
	
	public static void respIpForbidden(final HttpRequest request, final HttpChannel channel, ESLogger logger) {
		XContentBuilder builder;
		try {
			builder = ContentBuilder.restContentBuilder(request);
			builder.startObject()  
	        .field(new XContentBuilderString("status"), RestStatus.FORBIDDEN)  
	        .field(new XContentBuilderString("message"),  
	                "Your ip is not in auth list")  
	        .endObject();
			
			channel.sendResponse(  
	                new BytesRestResponse(RestStatus.FORBIDDEN, builder)); 
		} catch (IOException e) {
			if (logger != null) {
				logger.error("Get Exception in checkIpPermission: " + e.getMessage());
			}
			e.printStackTrace();
		}
	}
	
	public static boolean checkPermission(final HttpRequest request, final HttpChannel channel, ESAuthConfig config, ESLogger logger) {
		/*
		//config reload
		if (request.paramAsBoolean("auth_reload", false)) {
			config.reloadConfig();
			logger.info("reload auth config");
		}
		*/
		
		// 是否开启认证授权
		boolean open_auth = config.getSet().getAsBoolean("open_auth", false);
		if (!open_auth) {
			return true;
		}
		
		String authStr = "";
		if (request != null) {
			authStr = Tool.base64Decode(request.param("auth", ""));
		}
		// logger.info("authStr:" + authStr);
		
		String[] authArr = authStr.split(":");
		if (authArr.length != 2) {
			respForbidden(request, channel, logger);
			return false;
		}
		String user_username = authArr[0].trim();
		String config_username = config.getSet().get("username", "").trim();
		
		String user_password = authArr[1].trim();
		String config_password = config.getSet().get("password", "").trim();
		
		// logger.info("user_username:" + user_username);
		// logger.info("config_username:" + config_username);
		// logger.info("user_password:" + user_password);
		// logger.info("config_password:" + config_password);
		
		if (!user_username.equals(config_username) || !user_password.equals(config_password)) {
			respForbidden(request, channel, logger);
			return false;
		}
		return true;
	}
	
	public static boolean checkIpPermission(final HttpRequest request, final HttpChannel channel, ESAuthConfig config, ESLogger logger) {
		// 是否开启ip授权
		boolean open_ip_auth = config.getSet().getAsBoolean("open_ip_auth", false);
		if (!open_ip_auth) {
			return true;
		}
		String[] ip_auth_list = config.getSet().getAsArray("ip_auth_list", new String[0]);
		
		
		// logger.info("LocalAddr:" + request.getLocalAddress().toString());
		// logger.info("RemoteAddr:" + request.getRemoteAddress().toString());
		InetSocketAddress localAddr = (InetSocketAddress) request.getLocalAddress();
		InetSocketAddress remoteAddr = (InetSocketAddress) request.getRemoteAddress();
		
		boolean has_local = false;
		boolean has_auth_ip = false;
		for(String ip : ip_auth_list) {
			if (ip.equals("local")
				|| ip.equals("localhost")
				|| ip.equals("127.0.0.1")) {
				has_local = true;
			}
			if (ip.equals(remoteAddr.getHostName())) {
				has_auth_ip = true;
			}
			logger.info("ip: " + ip + ", me: " + remoteAddr.getHostName() );
		}
		
		if (localAddr.getHostName().equals(remoteAddr.getHostName())) {
			logger.info("Local Request");
			
			if (!has_local) {
				// respIpForbidden(request, channel, logger);
				return true;
			}
		} else {
			logger.info("Local:" + localAddr.getHostName());
			logger.info("Remote:" + remoteAddr.getHostName());
			
			if (!has_auth_ip) {
				respIpForbidden(request, channel, logger);
				return false;
			}
		}
		return true;
	}
}