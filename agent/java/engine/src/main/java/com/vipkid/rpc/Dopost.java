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
 * <p>javax.servlet.http service
 * </p>
 * DATE 2020/7/24.
 *
 * @author wangjian.
 */
public class Dopost implements ClassFileTransformer {
    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class <?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
        if ("javax.servlet.http.HttpServlet".equals( className.replace( "/", "." ) )) {
            try {
                //1、所引用的类型，必须通过ClassPool获取后才可以使用
                //2、代码块中所用到的引用类型，使用时必须写全量类名

                final CtClass clazz = ClassPool.getDefault().get( className.replace( "/", "." ) );
                CtMethod executeUpdateInternal = clazz.getDeclaredMethod( "doPost" );
                executeUpdateInternal.insertBefore(
                        "System.out.println(\"2222\");"
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
