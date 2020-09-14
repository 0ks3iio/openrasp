/*
 * Copyright 2017-2020 Baidu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baidu.openrasp;

import org.apache.commons.cli.*;
import sun.management.FileSystem;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.baidu.openrasp.Module.START_ACTION_INSTALL;
import static com.baidu.openrasp.Module.START_MODE_NORMAL;

/**
 * Created by tyy on 3/27/17.
 * OpenRASP的执行流很简单主要分为以下几部分：
 * agent初始化
 * V8引擎初始化
 * 日志配置模块初始化
 * 插件模块初始化
 * hook点管理模块初始化
 * 字节码转换模块初始化
 *
 * 加载agent的入口类，先于主函数加载
 */
public class Agent {

    public static String projectVersion;
    public static String buildTime;
    public static String gitCommit;

    public static void main(String[] args) {
        try {
            Options options = new Options();
            options.addOption("h", "help", false, "print options information");
            options.addOption("v", "version", false, "print the version of rasp");
            HelpFormatter helpFormatter = new HelpFormatter();
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("v")) {
                readVersion();
                System.out.println("Version:       " + projectVersion + "\n" +
                        "Build Time:    " + buildTime + "\n" +
                        "Git Commit ID: " + gitCommit);
            } else if (cmd.hasOption("h")) {
                helpFormatter.printHelp("java -jar rasp.jar", options, true);
            } else {
                helpFormatter.printHelp("java -jar rasp.jar", options, true);
            }
        } catch (Throwable e) {
            System.out.println("failed to parse options\n" + e.getMessage());
        }
    }

    /**
     * agent初始化
     * 启动时加载的agent入口方法
     *
     * @param agentArg 启动参数
     * @param inst     {@link Instrumentation}
     */
    public static void premain(String agentArg, Instrumentation inst) {
            init(START_MODE_NORMAL, START_ACTION_INSTALL, inst);
    }

    /**
     * attach 机制加载 agent
     *
     * @param agentArg 启动参数
     * @param inst     {@link Instrumentation}
     */
    public static void agentmain(String agentArg, Instrumentation inst) {
        init(Module.START_MODE_ATTACH, agentArg, inst);
    }

    /**
     * attack 机制加载 agent
     *
     * @param mode 启动模式
     * @param inst {@link Instrumentation}
     */
    public static synchronized void init(String mode, String action, Instrumentation inst) {
        try {
            /*
            这里在模块加载前做了一个非常重要的操作——将Java agent的jar包加入到BootStrap class path中，如果不进行特殊设定，则会默认将jar包加入到System class path
            中，对于研究过类加载机制的朋友们来说一定不陌生，这样做得好处就是可以将jar包加到BootStrapClassLoader所加载的路径中，在类加载时可以保证加载顺序位于最顶层，
            这样就可以不受到类加载顺序的限制，拦截拦截系统类。当将jar包添加进BootStrap class path后，就是完成模块加载的初始化流程中，这里会根据指定的jar包来实例化
            模块加载的主流程
             */
            // 添加jar文件到jdk的跟路径下，优先加载
            JarFileHelper.addJarToBootstrap(inst);
            readVersion();
            // 加载所有 RASP 模块
            ModuleLoader.load(mode, action, inst);
        } catch (Throwable e) {
            System.err.println("[OpenRASP] Failed to initialize, will continue without security protection.");
            e.printStackTrace();
        }
    }

    public static void readVersion() throws IOException {
        Class clazz = Agent.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) + "/META-INF/MANIFEST.MF";
        Manifest manifest = new Manifest(new URL(manifestPath).openStream());
        Attributes attr = manifest.getMainAttributes();
        projectVersion = attr.getValue("Project-Version");
        buildTime = attr.getValue("Build-Time");
        gitCommit = attr.getValue("Git-Commit");

        projectVersion = (projectVersion == null ? "UNKNOWN" : projectVersion);
        buildTime = (buildTime == null ? "UNKNOWN" : buildTime);
        gitCommit = (gitCommit == null ? "UNKNOWN" : gitCommit);
    }

}