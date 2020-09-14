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
public class ServletTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class <?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {

        if ("javax.servlet.http.HttpServlet".equals( className.replace( "/", "." ) )) {
            try {
                //1、所引用的类型，必须通过ClassPool获取后才可以使用
                //2、代码块中所用到的引用类型，使用时必须写全量类名

                final CtClass clazz = ClassPool.getDefault().get( className.replace( "/", "." ) );
                CtMethod executeUpdateInternal = clazz.getDeclaredMethod( "service" );
                executeUpdateInternal.insertBefore(
                                "com.baidu.openrasp.request.AbstractRequest request = com.baidu.openrasp.HookHandler.requestCache.get();" +
//                                        "System.out.println(\"requestid: \" + request.getRequestId());" +
//                                        "javax.servlet.http.HttpServletRequestWrapper r = req;" +
//                                "System.out.println(req.getMethod() +\" \"+req.getRequestURL().toString() +\" \"+req.getProtocol());" +
//                                "java.util.Enumeration/*<string>*/ s = req.getHeaderNames();" +
//                                "while (s.hasMoreElements()){String header = s.nextElement();System.out.println( header+ \" : \"+ req.getHeader(header)); }" +
//                                "System.out.println(\"email: \" + req.getParameter(\"email\"));" +
//                                "System.out.println(\"getPathTranslated: \" + req.getPathTranslated());" +
//                                "System.out.println(\"getQueryString: \" + req.getQueryString());" +
                                        "java.util.Enumeration headers = req.getHeaderNames();" +
                                        "while(headers.hasMoreElements()) {String header = (String)headers.nextElement();System.out.println(header +\": \"+req.getHeader(header)); }" +
//                                "System.out.println(\"--------打印 GET 类型参数值----------\");" +
                                        "java.util.Enumeration paramNames = req.getParameterNames();" +
                                "while(paramNames.hasMoreElements()) {String paramName = (String)paramNames.nextElement();System.out.println(paramName + \": \" + req.getParameter(paramName)); }"
//                                "System.out.println(\"getInputStream: \" + r.getInputStream());" +
//                                "java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(r.getInputStream(), \"utf-8\"));java.lang.StringBuffer sb = new java.lang.StringBuffer(\"\");String temp;while ((temp = br.readLine()) != null) {sb.append(temp);} " +
//                                "String param = sb.toString();" +
//                                "System.out.println(param);"
                        // "java.util.Enumeration/*<string>*/ parameterNames = r.getParameterNames();" +
                        // "while (parameterNames.hasMoreElements()){String parameterName = parameterNames.nextElement();System.out.println( parameterName+ \" : \"+ r.getParameterNames(parameterName)); }"
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
