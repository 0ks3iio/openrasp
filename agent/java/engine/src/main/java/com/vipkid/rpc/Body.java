package com.vipkid.rpc;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/7/23.
 *
 * @author wangjian.
 */
public class Body implements ClassFileTransformer {
    /*
     * 记录/拦截 org.springframework.web.method.support.InvocableHandlerMethod.doInvoke方法，捕获http请求的body信息，解决无法捕获json格式数据问题，并写入到redis中
     * 1. 对非扫描器发起的调用仅做记录，记录url、headers等尽可能完整的信息
     * 2. 针对扫描器发起的检测为了防止带来脏数据需要将调用做拦截，实现逻辑是将请求的uri内容进行替换并做记录，需要同时记录请求的headers等信息，用做判断是否属于越权调用的依据
     *
     * */

    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class <?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
        if ("org.springframework.web.method.support.InvocableHandlerMethod".equals( className.replace( "/", "." ) )) {
            System.out.println( className );
            // invokeForRequest
            try {
                //1、所引用的类型，必须通过ClassPool获取后才可以使用
                //2、代码块中所用到的引用类型，使用时必须写全量类名
                final CtClass clazz = ClassPool.getDefault().get( className.replace( "/", "." ) );
                CtMethod executeUpdateInternal = clazz.getDeclaredMethod( "doInvoke" );
                executeUpdateInternal.insertAfter(
                        "try{com.baidu.openrasp.request.AbstractRequest request = com.baidu.openrasp.HookHandler.requestCache.get();(new com.vipkid.sql.DetectAuthorityVulnClass()).setRequestBody(request.getRequestId(), args[0].toString());}catch(Exception ex){}"
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
