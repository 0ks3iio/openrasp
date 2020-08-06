package com.vipkid.rpc;

import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
/**
 * Description
 * <p>
 * </p>
 * DATE 2020/7/21.
 *
 * @author wangjian.
 */
public class RpcSpringframeworkRestTemplateTransformer implements ClassFileTransformer {
    /*
     * 记录/拦截执行的update语句
     * todo 根据拦截到的rpc请求url判断发送给后端的数据是否合法，如果数据一致则判断发送请求前未做权限校验，第二点检测rpc后端服务接收到数据以后的是否涉及sql语句，如果存在则将整个数据链路进行展示
     *
     * */

    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class <?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
        if ("org/springframework/web/client/RestTemplate".equals( className )) {
            try {
                //1、所引用的类型，必须通过ClassPool获取后才可以使用
                //2、代码块中所用到的引用类型，使用时必须写全量类名
                final CtClass clazz = ClassPool.getDefault().get( className.replace( "/", "." ) );
                CtMethod executeUpdateInternal = clazz.getDeclaredMethod( "doExecute" );
                executeUpdateInternal.insertBefore(
                        "com.baidu.openrasp.request.AbstractRequest request = com.baidu.openrasp.HookHandler.requestCache.get();" +
                                "java.net.URI u = url;" +
                                "String currentUrl = u.toString();" +
                                "String resultStr = (new com.vipkid.rpc.DetectAuthorityVulnForRpcRequestClass()).detect(request, currentUrl);" +
                                "if (resultStr.equals(\"1\")){ url = \"http://172.20.251.204/\";}"
                );
                // 返回字节码，并且detachCtClass对象
                byte[] byteCode = clazz.toBytecode();
                //detach的意思是将内存中曾经被javassist加载过的Date对象移除，如果下次有需要在内存中找不到会重新走javassist加载
                clazz.detach();
                return byteCode;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        // 如果返回null则字节码不会被修改
        return null;
    }
}
