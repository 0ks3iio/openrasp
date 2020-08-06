package com.vipkid.sql;

import com.baidu.openrasp.cloud.CloudHttp;
import com.baidu.openrasp.cloud.model.GenericResponse;
import com.baidu.openrasp.cloud.utils.CloudUtils;
import com.baidu.openrasp.config.Config;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.net.ssl.HttpsURLConnection;
//DataOutputStream类 数据输出流允许应用程序以与机器无关方式将Java基本数据类型写到底层输出流。
import java.io.DataOutputStream;
// Java标准库提供的最基本的输入流,抽象类,所有输入流的超
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.GZIPInputStream;


/**
 * Description
 * <p>
 * </p>
 * DATE 2020/7/21.
 *
 * @author wangjian.
 */
public class DiyCloudHttp extends CloudHttp {
    public String DiyRequest(String url, String content) throws Exception {

        DataOutputStream out = null;

        InputStream in = null;
        String jsonString = null;
        int responseCode;
        try {
            // URL类对象
            URL realUrl = new URL( url );
            // 返回一个HttpURLConnection 对象
            URLConnection conn = realUrl.openConnection();
            if (conn instanceof HttpsURLConnection && !Config.getConfig().isHttpsVerifyPeer()) {
                skipSSL( (HttpsURLConnection) conn );
            }
            HttpURLConnection httpUrlConnection = (HttpURLConnection) conn;
            // 修改Request的属性的方法
            httpUrlConnection.setRequestProperty( "Content-Type", "application/json" );
            String appId = Config.getConfig().getCloudAppId();
            httpUrlConnection.setRequestProperty( "X-OpenRASP-AppID", appId );
            String appSecret = Config.getConfig().getCloudAppSecret();
            httpUrlConnection.setRequestProperty( "X-OpenRASP-AppSecret", appSecret );
            httpUrlConnection.setRequestProperty( "Accept-Encoding", "gzip" );
            httpUrlConnection.setConnectTimeout( 1000 );
            httpUrlConnection.setReadTimeout( 3000 );
            httpUrlConnection.setRequestMethod( "POST" );
            httpUrlConnection.setUseCaches( false );
            httpUrlConnection.setDoOutput( true );
            httpUrlConnection.setDoInput( true );
            // 创建数据输出流对象
            out = new DataOutputStream( httpUrlConnection.getOutputStream() );
            out.write( content.getBytes( "UTF-8" ) );
            out.flush();
            httpUrlConnection.connect();
            responseCode = httpUrlConnection.getResponseCode();
            // 读取网页内容(字符串流)
            in = httpUrlConnection.getInputStream();
            String encoding = httpUrlConnection.getContentEncoding();
            if (encoding != null && encoding.contains( "gzip" )) {
                in = new GZIPInputStream( httpUrlConnection.getInputStream() );
            }
            jsonString = CloudUtils.convertInputStreamToJsonString( in );
        } finally {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
        }
        return jsonString;
    }
}

