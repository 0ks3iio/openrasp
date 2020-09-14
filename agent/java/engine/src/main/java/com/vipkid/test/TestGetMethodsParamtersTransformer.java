package com.vipkid.test;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * <p>
 * </p>
 * DATE 2020/8/5.
 *
 * @author wangjian.
 */
public class TestGetMethodsParamtersTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class <?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
//        System.out.println("TestGetMethodsParamtersTransformer： " + className );
//        if ( className.toLowerCase().contains( "vipkid/teacher" ) || className.toLowerCase().contains( "org/springframework/web/" ) || className.toLowerCase().contains( "org/apache/catalina/connector" ) || className.toLowerCase().startsWith( "org/apache/commons/httpclient/URI".toLowerCase() ) || className.toLowerCase().startsWith( "org/apache/http/client/methods" ) || className.toLowerCase().startsWith( "com/squareup/okhttp3/HttpUrl".toLowerCase() ) || className.toLowerCase().startsWith( "com/squareup/okhttp/HttpUrl".toLowerCase() ) || className.toLowerCase().startsWith( "sun/net/www/protocol/http/HttpURLConnection".toLowerCase() ) || className.toLowerCase().startsWith( "java/io/ObjectInputStream".toLowerCase() ) || className.toLowerCase().startsWith( "java/lang/UNIXProcess".toLowerCase() ) || className.toLowerCase().startsWith( "java/lang/ProcessImpl".toLowerCase() ) || className.toLowerCase().startsWith( "com/alibaba/dubbo/rpc/filter/ContextFilter".toLowerCase() ) || className.toLowerCase().startsWith( "com/alibaba/dubbo/rpc/filter/GenericFilter".toLowerCase() ) || className.toLowerCase().startsWith( "com/mysql/cj/jdbc/clientpreparedstatement" ) || className.toLowerCase().startsWith( "com/mysql/jdbc/NonRegisteringDriver".toLowerCase() ) || className.toLowerCase().startsWith( "com/mysql/cj/jdbc/NonRegisteringDriver".toLowerCase() ) || className.toLowerCase().startsWith( "java/net/" ) || className.toLowerCase().contains( "com/vipkid/mall" ) || className.toLowerCase().contains( "test1" ) || className.toLowerCase().contains( "org/apache/tomcat" )) {
        if (className.toLowerCase().contains( "com/vipkid/" )){
            if ( className.toLowerCase().startsWith( "com/vipkid/commons/springboot/web" ) || className.startsWith( "com.vipkid.sql.DetectAuthorityVulnClass" )|| className.startsWith( "com/vipkid/rpc/DetectAuthorityVulnForRpcRequestClass" ) || className.startsWith( "com/dianping/cat" ) || className.contains( "SocketProperties" ) || className.contains( "BaseModelMBean" )|| className.contains( "Registry" ) || className.contains( "NioBlockingSelector" ) || className.toLowerCase().contains( "synchronizedqueue" ) || className.toLowerCase().contains( "com/mysql/jdbc/nonregisteringdriver" ) || className.toLowerCase().contains( "NioEndpoint" ) || className.toLowerCase().startsWith( "com/baidu" ) || className.toLowerCase().startsWith( "com/fasterxml" ) || className.toLowerCase().startsWith( "org/springframework/" ) || className.toLowerCase().startsWith( "org/hibernate/" ) || className.equals( "com/vipkid/sql/DiyCloudHttp" ) || className.equals( "com/vipkid/sql/DetectAuthorityVulnClass" ) || className.contains( "security" ) || className.contains( "java/util/concurrent" ) || className.contains( "org/apache/catalina/loader" ) || className.contains( "java/sql/SQLPermission" ) || className.contains( "ch/qos/logback/" ) || className.contains( "java/lang/invoke" ) || className.contains( "Proxy" ) || className.toLowerCase().contains( "buffer" ) || className.contains( "java/lang/ClassValue" ) || className.toLowerCase().contains( "java/lang/classLoader" ) || className.toLowerCase().contains( "java/lang/reflect" ) || className.toLowerCase().contains( "java/lang/shutdown" ) || className.toLowerCase().contains( "java/lang/NullPointerException" ) || className.toLowerCase().contains( "java/lang/Thread" ) || className.toLowerCase().contains( "sun/reflect/" ) || className.toLowerCase().contains( "javassist" ) || className.contains( "org/apache/http/client/methods/AbstractExecutionAwareRequest" ) || className.contains( "com/mysql/jdbc/Field" ) || className.contains( "com/mysql/jdbc/ByteArrayRow" )) {
            } else {
//                System.out.println( className );
                try {
                    final CtClass ctClass = ClassPool.getDefault().get( className.replace( "/", "." ) );
                    
                    CtMethod[] methods = ctClass.getMethods();
                    for (CtMethod method : methods) {
                        String methodLongName = method.getLongName();
                        String methodName = method.getName();
                        // 如果类名包含于方法名 则执行以下逻辑
                        if (methodLongName.contains( className.replace( "/", "." ) )) {
                            String tmpMethodName = methodLongName.split( "\\(" )[0].replace( className.replace( "/", "." ) + ".", "" );
                            CtMethod ctMethod = ctClass.getDeclaredMethod( tmpMethodName );
                            MethodInfo methodInfo = ctMethod.getMethodInfo();
                            boolean isStatic = (methodInfo.getAccessFlags() & AccessFlag.STATIC) != 0;  // 判断是否为静态方法
                            if (isStatic) {
                                try {
                                    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                                    LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute( LocalVariableAttribute.tag );
                                    CtClass[] parameterTypes = ctMethod.getParameterTypes();
                                    int parameterSize = attr.tableLength(); // 静态类型取值
//                                    System.out.println("--------a----------");
//                                    System.out.println("className: " + className);
//                                    System.out.println("methodLongName: " + methodLongName);
//                                    System.out.println("tmpMethodName: " + tmpMethodName);
//                                    System.out.println("parameterSize: " + parameterSize);
//                                    System.out.println("returnType: " + method.getReturnType().getName());
//                                    System.out.println("--------b----------");
                                    CtMethod executeUpdateInternal = ctClass.getDeclaredMethod( tmpMethodName );
                                    executeUpdateInternal.insertBefore(
                                            "try{" +
                                                    "String clazz = this.getClass().getName();" +
                                                    "if (clazz.contains(\"springfrasmework\")){}else{" +
//                                                    "System.out.println(\" 11 returnType: " + method.getReturnType().getName() + "\");" +
//                                                    "System.out.println(\"className: " + className + "\");" +
                                                    "com.baidu.openrasp.request.AbstractRequest request = com.baidu.openrasp.HookHandler.requestCache.get();" +
//                                                    "System.out.println(request.getRequestId());" +
                                                    "String method = java.lang.Thread.currentThread() .getStackTrace()[1].getMethodName();" +
//                                                    "System.out.println(\"clazz: \" + clazz + \" method: \" + method);" +
                                                    "for (int i = 0; i < " + parameterSize + "; i++) {System.out.println(\"--- \" + i +\" --- \"+ $args[i].getClass().getName() +\" --- \"+ $args[i]);};" +
                                                    "}}catch(Exception Ex){}"
//
                                    );
                                    executeUpdateInternal.insertAfter( "try{com.baidu.openrasp.request.AbstractRequest request = com.baidu.openrasp.HookHandler.requestCache.get();" );
                                } catch (Exception e) {
//                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
                                    LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute( LocalVariableAttribute.tag );
                                    CtClass[] parameterTypes = ctMethod.getParameterTypes();


                                    int parameterSize = attr.tableLength(); // 静态类型取值
//                                    System.out.println("--------c----------");
//                                    System.out.println(className);
//                                    System.out.println(methodLongName);
//                                    System.out.println(tmpMethodName);
//                                    System.out.println("--------d----------");
                                    CtMethod executeUpdateInternal = ctClass.getDeclaredMethod( tmpMethodName );
                                    executeUpdateInternal.insertBefore(
                                            "try{" +
                                                    "com.baidu.openrasp.request.AbstractRequest request = com.baidu.openrasp.HookHandler.requestCache.get();" +
                                                    "String clazz = this.getClass().getName();" +
                                                    "if (clazz.contains(\"aaaaaaaaaaaa\")){}else{" +
//                                                    "try{System.out.println(\"1 returnType: " + method.getReturnType().getName() + "\");" +
                                                    "try{" +
                                                    "String method = java.lang.Thread.currentThread() .getStackTrace()[1].getMethodName();" +
//                                                    "System.out.println(\"clazz: \" + clazz + \" method: \" + method);" +
//                                                    "System.out.println(\"parameterSize: " + parameterSize + "\");" +
                                                    "for (int i = 0; i < " + parameterSize + "; i++) {" +
                                                    "String tmp = request.getRequestId() + \"-+-+-\"+ clazz +\"-+-+-\"+ method +\"-+-+-\" + i +\"-+-+-\"+ $args[i].getClass().getName() +\"-+-+-\"+ $args[i];" +
//                                                    "System.out.println(tmp);" +
                                                    "try{(new com.vipkid.sql.DetectAuthorityVulnClass()).setClassAndMethod(request.getRequestId(), tmp);}catch (Exception e){e.printStackTrace();}" +
                                                    "}}catch(Exception e){}}}" +
                                                    "catch(Exception Ex){}"
                                    );
//                                    executeUpdateInternal.insertAfter(
//                                            "try{com.baidu.openrasp.request.AbstractRequest request = com.baidu.openrasp.HookHandler.requestCache.get();" +
//                                            "String clazz = this.getClass().getName();" +
////                                            "System.out.println(\"2 returnType: " + method.getReturnType().getName() + "\");" +
//                                            "String method = java.lang.Thread.currentThread() .getStackTrace()[1].getMethodName();" +
////                                            "System.out.println(\"requestId: \" + request.getRequestId() + \" clazz: \" + clazz + \" method: \" + method + \" returnType: \" + method.getReturnType().getName() + \" returnValue: \"+ $_);}catch(Exception ex){ex.printStackTrace();}" +
//                                                    "");
                                } catch (Exception e) {
//                                   e.printStackTrace();
                                }
                            }
                        }
                    }

                    // 返回字节码，并且detachCtClass对象
                    byte[] byteCode = ctClass.toBytecode();
                    //detach的意思是将内存中曾经被javassist加载过的Date对象移除，如果下次有需要在内存中找不到会重新走javassist加载
                    ctClass.detach();
                    return byteCode;
                } catch (Exception ex) {
//                ex.printStackTrace();
                }
            }
        }
        // 如果返回null则字节码不会被修改
        return null;
    }
}
