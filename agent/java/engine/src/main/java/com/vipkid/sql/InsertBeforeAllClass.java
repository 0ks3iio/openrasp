package com.vipkid.sql;
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
public class InsertBeforeAllClass implements ClassFileTransformer {
    /*
     * 记录/拦截执行的update语句
     * todo 根据requestId判断请求是否来自扫描器
     * todo 如果不是来自扫描器则记录执行的sql语句，并对返回结果进行序列化操作
     * todo 如果判断是来自扫描器，则记录sql语句并对存储的原始数据进行反序列化并返回该对象，保证在不和数据库进行交互的前提下完成业务流程
     *
     * */

    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class <?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
//        if ("com/mysql/cj/jdbc/ClientPreparedStatement".equals( className )) {


        if (className.replace( "/", "." ).contains( "com.vipkid" )) {
            try {
                //1、所引用的类型，必须通过ClassPool获取后才可以使用
                //2、代码块中所用到的引用类型，使用时必须写全量类名
                final CtClass clazz = ClassPool.getDefault().get( className.split("$")[0].replace( "/", "." ) );
                System.out.println( "className is :" + clazz.getName() );
                CtMethod[] methods = clazz.getMethods();
                for (CtMethod method : methods) {
                    String name = method.getLongName();
                    if (name.contains( "com.vipkid" )){
                        System.out.println("name: " + name);
                        CtMethod executeUpdateInternal = clazz.getDeclaredMethod( name );
                    executeUpdateInternal.insertBefore( "" +
                        "System.out.println(\"------name:\" +this.toString());"
                );
                    }
                }
//                CtMethod executeUpdateInternal = clazz.getDeclaredMethod( "execSQL" );
//                executeUpdateInternal.insertBefore( "" +
//                        "System."
//                );
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
