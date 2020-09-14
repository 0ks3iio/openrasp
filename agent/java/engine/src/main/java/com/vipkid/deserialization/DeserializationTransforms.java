package com.vipkid.deserialization;
import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;


public class DeserializationTransforms implements ClassFileTransformer {

    @Override
    public byte[] transform(final ClassLoader loader, final String className, final Class <?> classBeingRedefined, final ProtectionDomain protectionDomain, final byte[] classfileBuffer) {
        System.out.println(className);
        if ("com.sun.rowset.JdbcRowSetImp".equals( className )) {
            System.out.println(className);
        }
        // 如果返回null则字节码不会被修改
        return null;
    }
}
