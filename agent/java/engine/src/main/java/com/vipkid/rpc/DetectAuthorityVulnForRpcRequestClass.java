package com.vipkid.rpc;

import com.vipkid.sql.DetectAuthorityVulnClass;
import com.vipkid.sql.DiyCloudHttp;
import com.baidu.openrasp.request.AbstractRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.NameValuePair;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/7/21.
 *
 * @author wangjian.
 */
public class DetectAuthorityVulnForRpcRequestClass {
    public String detect(AbstractRequest request, String rpcUrl) throws Exception {

        /*
         */

        String ua = request.getHeader( "user-agent" );
        // 首先判断请求是否来自扫描器
        if (ua.contains( "TestBySecurityTeamForVuln" )) {
            String[] uaSplited = ua.split( "-" );
            String origRequestid = uaSplited[1];
            String origRpcUrl = new DetectAuthorityVulnClass().getRpcUrlByRequestId( origRequestid );
            try {
                Map <String, Object> rpcInfo = new HashMap <String, Object>();
                rpcInfo.put( "RequestId", origRequestid );
                rpcInfo.put( "OriginRpcUrl", origRpcUrl );
                rpcInfo.put( "CurrentRpcUrl", rpcUrl );
                String rpcInfoContent = new Gson().toJson( rpcInfo );
                System.out.println( "origRpcUrl:" + origRpcUrl );
                System.out.println( "CurrentRpcUrl:" + rpcUrl );
                //将检测到的rpcUrl放在一起
                try {
                    new DetectAuthorityVulnClass().BeegoRequest( "detectRpcUrl", rpcInfoContent );
                } catch (Exception e) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "1";
        } else {
            //正常请求则仅记录rpc对应的资源地址" +
            String requestId = request.getRequestId();
            // 存储当前请求对应的资源地址到redis中
            try {
                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
                System.out.println( ft.format( dNow ) + "|" + request.getRemoteAddr() + "|[org.apache.http.client]：" + rpcUrl );
                //根据requestId将url写入redis
                try {
                    new DetectAuthorityVulnClass().SetRpcUrl( requestId, rpcUrl );
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
            return "0";
        }
    }

    public String detect1(AbstractRequest request, String rpcUrl, String method) throws Exception {

        /*
         */

        String ua = request.getHeader( "user-agent" );
        System.out.println( request.getRemoteAddr() );
        // 首先判断请求是否来自扫描器
        if (ua.contains( "TestBySecurityTeamForVuln" )) {
            String[] uaSplited = ua.split( "-" );
            String origRequestid = uaSplited[1];
            String origRpcUrl = new DetectAuthorityVulnClass().getRpcUrlByRequestId( origRequestid );
            try {
                Map <String, Object> rpcInfo = new HashMap <String, Object>();
                rpcInfo.put( "RequestId", origRequestid );
                rpcInfo.put( "OriginRpcUrl", origRpcUrl );
                rpcInfo.put( "CurrentRpcUrl", rpcUrl );
                String rpcInfoContent = new Gson().toJson( rpcInfo );
                System.out.println( "origRpcUrl:" + origRpcUrl );
                System.out.println( "CurrentRpcUrl:" + rpcUrl );
                //将检测到的rpcUrl放在一起
                try {
                    new DetectAuthorityVulnClass().BeegoRequest( "detectRpcUrl", rpcInfoContent );
                } catch (Exception e) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "1";
        } else {
            //正常请求则仅记录rpc对应的资源地址" +
            String requestId = request.getRequestId();
            // 存储当前请求对应的资源地址到redis中
            try {
                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
                System.out.println( ft.format( dNow ) + "|" + request.getRemoteAddr() + "|[org.apache.http.client]：" + method + " " + rpcUrl );
                //根据requestId将url写入redis
                try {
                    new DetectAuthorityVulnClass().SetRpcUrl( requestId, rpcUrl );
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
            return "0";
        }
    }

    public String detect2(AbstractRequest request, String rpcUrl, String method, List <NameValuePair> parameters) throws Exception {

        /*
         */

        String ua = request.getHeader( "user-agent" );
        // 首先判断请求是否来自扫描器
        if (ua.contains( "TestBySecurityTeamForVuln" )) {
            String[] uaSplited = ua.split( "-" );
            String origRequestid = uaSplited[1];
            String origRpcUrl = new DetectAuthorityVulnClass().getRpcUrlByRequestId( origRequestid );
            try {
                Map <String, Object> rpcInfo = new HashMap <String, Object>();
                rpcInfo.put( "RequestId", origRequestid );
                rpcInfo.put( "OriginRpcUrl", origRpcUrl );
                rpcInfo.put( "CurrentRpcUrl", rpcUrl );
                String rpcInfoContent = new Gson().toJson( rpcInfo );
                System.out.println( "origRpcUrl:" + origRpcUrl );
                System.out.println( "CurrentRpcUrl:" + rpcUrl );
                //将检测到的rpcUrl放在一起
                try {
                    new DetectAuthorityVulnClass().BeegoRequest( "detectRpcUrl", rpcInfoContent );
                } catch (Exception e) {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "1";
        } else {
            //正常请求则仅记录rpc对应的资源地址" +
            String requestId = request.getRequestId();
            // 存储当前请求对应的资源地址到redis中
            try {
                Date dNow = new Date();
                SimpleDateFormat ft = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );
                System.out.println( ft.format( dNow ) + "|" + request.getRemoteAddr() + "|[org.apache.http.client]：" + method + " " + rpcUrl + " " + parameters );
                //根据requestId将url写入redis
                try {
                    new DetectAuthorityVulnClass().SetRpcUrl( requestId, rpcUrl );
                } catch (Exception e) {
                }
            } catch (Exception e) {
            }
            return "0";
        }
    }
}
