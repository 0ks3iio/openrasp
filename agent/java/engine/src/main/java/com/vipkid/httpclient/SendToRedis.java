package com.vipkid.httpclient;

import com.baidu.openrasp.request.HttpServletRequest;
import com.google.gson.Gson;
import com.vipkid.sql.DiyCloudHttp;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/8/5.
 *
 * @author wangjian.
 */
public class SendToRedis {
    String BeeGoServerAddress = "http://172.20.251.204:8082/";
    public String pushRequestInfoToRedis(String requestId, String method, String requestUrl, String protocol, Enumeration headers, Enumeration paramNames) throws Exception {
        Map <String, Object> parameters = new HashMap <String, Object>();
        parameters.put( "RequestId", requestId );
        String url = BeeGoServerAddress + "pushRequestIdToRedis";
        return new DiyCloudHttp().DiyRequest( url, new Gson().toJson( parameters ) );
    }
}
