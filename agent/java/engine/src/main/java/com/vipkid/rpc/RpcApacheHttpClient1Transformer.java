package com.vipkid.rpc;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import com.google.gson.Gson;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.net.URI;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/7/21.
 *
 * @author wangjian.
 */
public class RpcApacheHttpClient1Transformer implements ClassFileTransformer {
    /*
     * 记录/拦截 org.apache.http.client.methods.RequestBuilder.build()
     * 1. 对非扫描器发起的调用仅做记录，记录url、headers等尽可能完整的信息
     * 2. 针对扫描器发起的检测为了防止带来脏数据需要将调用做拦截，实现逻辑是将请求的uri内容进行替换并做记录，需要同时记录请求的headers等信息，用做判断是否属于越权调用的依据
     * */

    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class <?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
        if ("org.apache.http.impl.client.CloseableHttpClient".equals( className.replace( "/", "." ) )) {
//        if ("org.apache.http.impl.client.HttpClientBuilder".equals( className.replace( "/", "." ) )) {
            try {
                //1、所引用的类型，必须通过ClassPool获取后才可以使用
                //2、代码块中所用到的引用类型，使用时必须写全量类名
                final CtClass clazz = ClassPool.getDefault().get( className.replace( "/", "." ) );
                CtMethod executeUpdateInternal = clazz.getDeclaredMethod( "execute" );
                executeUpdateInternal.insertBefore(
                        "try{System.out.println(\"api 调用 org.apache.http.impl.client.CloseableHttpClient: \" + target.toString());System.out.println(request.toString());com.baidu.openrasp.request.AbstractRequest r = com.baidu.openrasp.HookHandler.requestCache.get();String resultStr = (new com.vipkid.rpc.DetectAuthorityVulnForRpcRequestClass()).detect(r, request.toString());}catch(Exception ex){ex.printStackTrace();}"
                );
                // 返回字节码，并且detachCtClass对象
                byte[] byteCode = clazz.toBytecode();
                //detach的意思是将内存中曾经被javassist加载过的Date对象移除，如果下次有需要在内存中找不到会重新走javassist加载
                clazz.detach();
                return byteCode;
            } catch (Exception ex) {
//                ex.printStackTrace();
            }
        }
        // 如果返回null则字节码不会被修改
        return null;
    }
}
