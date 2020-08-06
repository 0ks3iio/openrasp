package com.vipkid.sql;
import com.baidu.openrasp.request.AbstractRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
/**
 * Description
 * <p>
 * </p>
 * DATE 2020/7/21.
 *
 * @author wangjian.
 */
public class DetectAuthorityVulnClass {

    String BeeGoServerAddress = "http://172.20.251.204:8082/";
//    String BeeGoServerAddress = "http://127.0.0.1:8082/";

    public String detect(AbstractRequest request, String bytes) throws Exception {

        // 字节数组转字符串
        String currentSql = hexStr2Str( bytes );
        String ua = request.getHeader( "user-agent" );
        // 首先判断请求是否来自扫描器
        if (ua.contains( "TestBySecurityTeamForVuln" )) {
            System.out.println( "匹配到了发起的检测请求，开始执行越权判断的逻辑" );
            //记录当前requestId检测的sql偏移
            Map <String, Object> parameters = new HashMap <String, Object>();
            parameters.put( "RequestId", request.getRequestId() );
            String content = new Gson().toJson( parameters );
            BeegoRequest( "setSqlIndexByRequestId", content );
            String[] uaSplited = ua.split( "-" );
            String origRequestid = uaSplited[1];
            // 根据执行的sql语句是否一致存在漏洞
            // 获取当前检测的sql的偏移量
            int sqlIndex = getSqlIndex( request.getRequestId() ) - 1;
            if (sqlIndex < 0) {
                sqlIndex = 1;
            }

            Map <String, Object> parameters1 = new HashMap <String, Object>();
            parameters1.put( "RequestId", origRequestid );
            parameters1.put( "Index", sqlIndex );

            String oriGSql = getRpcUrlByRequestId( origRequestid );

//            if (currentSql.equals( oriGSql )) {
//
//                System.out.println( "检测到替换身份信息以后执行了相同的SQL语句"+ origRequestid );
//                System.out.println( "   origSql:" + oriGSql );
//                System.out.println( "currentSql:" + currentSql );
//            } else {
//                System.out.println( "检测到替换身份信息以后执行了不同的SQL语句,url" );
//                System.out.println( "   origSql:" + oriGSql );
//                System.out.println( "currentSql:" + currentSql );
//            }
            Map <String, Object> sqlInfo = new HashMap <String, Object>();
            sqlInfo.put( "RequestId", origRequestid );
            sqlInfo.put( "OriginSQL", oriGSql );
            sqlInfo.put( "CurrentSQL", currentSql );
            String sqlInfoContent = new Gson().toJson( sqlInfo );
            try {
                //将检测到的sql进行记录
                BeegoRequest( "detectSql", sqlInfoContent );
            } catch (Exception e) {
            }


            return "1";
        } else {
            //正常请求则仅记录执行的SQL" +
            String requestId = request.getRequestId();
            // 存储当前请求对应的sql语句到redis中
            Map <String, Object> parameters = new HashMap <String, Object>();
            parameters.put( "RequestId", requestId );
            parameters.put( "Type", "sql" );
            parameters.put( "Value", currentSql );
            String content = new Gson().toJson( parameters );

            System.out.println("sql信息：" + content);
            try {
                //根据requestId将sql语句写入redis
                BeegoRequest( "setSqlToListByRequestId", content );
            } catch (Exception e) {
            }
            return "0";
        }
    }

    public static String hexStr2Str(String hexStr) {
        String s = "";
        for (String s1 : hexStr.split( "\n" )) {
            s = s + s1.substring( 0, 24 );
        }
        String str = "0123456789abcdef";
        char[] hexs = s.replace( " ", "" ).toCharArray();
        byte[] bytes = new byte[s.replace( " ", "" ).length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf( hexs[2 * i] ) * 16;
            n += str.indexOf( hexs[2 * i + 1] );
            bytes[i] = (byte) (n & 0xff);
        }
        return new String( bytes );
    }

    public void BeegoRequest(String action, String content) throws Exception {
        String url = BeeGoServerAddress + action;
        new DiyCloudHttp().DiyRequest( url, content );
    }

    public int getSqlIndex(String requestId) throws Exception {
        String url = BeeGoServerAddress + "getSqlIndexByRequestId";
        Map <String, Object> parameters = new HashMap <String, Object>();
        parameters.put( "RequestId", requestId );
        String content = new Gson().toJson( parameters );
        JsonObject returnData = new JsonParser().parse( new DiyCloudHttp().DiyRequest( url, content ) ).getAsJsonObject();
        return (int) Integer.parseInt( String.valueOf( returnData.get( "Index" ) ) );
    }

    public void setRequestBody(String requestId, String body) throws Exception {
        String url = BeeGoServerAddress + "setRequestBody";
        Map <String, Object> parameters = new HashMap <String, Object>();
        parameters.put( "RequestId", requestId );
        parameters.put( "Body", body );
        String content = new Gson().toJson( parameters );
        new JsonParser().parse( new DiyCloudHttp().DiyRequest( url, content ) ).getAsJsonObject();
    }


    public String getSqlByIndex(String requestId, int index) throws Exception {
        Map <String, Object> parameters = new HashMap <String, Object>();
        parameters.put( "RequestId", requestId );
        parameters.put( "Index", index );
        String url = BeeGoServerAddress + "getSqlByRequestIdAndIndex";
        return new DiyCloudHttp().DiyRequest( url, new Gson().toJson( parameters ) );
    }

    public String SetRpcUrl(String requestId, String rpcUrl) throws Exception {
        Map <String, Object> parameters = new HashMap <String, Object>();
        parameters.put( "RequestId", requestId );
        parameters.put( "Value", rpcUrl );
        String url = BeeGoServerAddress + "setRpcUrl";
        return new DiyCloudHttp().DiyRequest( url, new Gson().toJson( parameters ) );
    }

    public String getRpcUrlByRequestId(String requestId) throws Exception {
        Map <String, Object> parameters = new HashMap <String, Object>();
        parameters.put( "RequestId", requestId );
        String url = BeeGoServerAddress + "getRpcUrl";
        return new DiyCloudHttp().DiyRequest( url, new Gson().toJson( parameters ) );
    }

    public String pushRequestIdToRedis(String requestId) throws Exception {
        Map <String, Object> parameters = new HashMap <String, Object>();
        parameters.put( "RequestId", requestId );
        String url = BeeGoServerAddress + "pushRequestIdToRedis";
        return new DiyCloudHttp().DiyRequest( url, new Gson().toJson( parameters ) );
    }
}
