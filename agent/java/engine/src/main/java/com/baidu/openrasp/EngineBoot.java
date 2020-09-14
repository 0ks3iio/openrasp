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

import com.baidu.openrasp.cloud.CloudManager;
import com.baidu.openrasp.cloud.model.CloudCacheModel;
import com.baidu.openrasp.cloud.utils.CloudUtils;
import com.baidu.openrasp.config.Config;
import com.baidu.openrasp.messaging.LogConfig;
import com.baidu.openrasp.plugin.checker.CheckerManager;
import com.baidu.openrasp.plugin.js.JS;
import com.baidu.openrasp.tool.cpumonitor.CpuMonitorManager;
import com.baidu.openrasp.tool.model.BuildRASPModel;
import com.baidu.openrasp.transformer.CustomClassTransformer;
import com.baidu.openrasp.v8.CrashReporter;
import com.baidu.openrasp.v8.Loader;
import com.vipkid.rpc.*;
import com.vipkid.sql.InsertBeforeAllClass;
import com.vipkid.sql.SqlTransformer;
import com.vipkid.sql.SqlTransformer1;
import com.vipkid.sql.SqlTransformer2;
import com.vipkid.sql.SqlTransformer3;
import com.vipkid.test.TestGetMethodsParamtersTransformer;
import com.vipkid.deserialization.DeserializationTransforms;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

/**
 * Created by tyy on 18-1-24.
 *
 * OpenRasp 引擎启动类
 */
public class EngineBoot implements Module {

    private CustomClassTransformer transformer;

    @Override
    public void start(String mode, Instrumentation inst) throws Exception {
        System.out.println("\n\n" +
                "   ____                   ____  ___   _____ ____ \n" +
                "  / __ \\____  ___  ____  / __ \\/   | / ___// __ \\\n" +
                " / / / / __ \\/ _ \\/ __ \\/ /_/ / /| | \\__ \\/ /_/ /\n" +
                "/ /_/ / /_/ /  __/ / / / _, _/ ___ |___/ / ____/ \n" +
                "\\____/ .___/\\___/_/ /_/_/ |_/_/  |_/____/_/      \n" +
                "    /_/                                          \n\n");
        try {
            Loader.load();
        } catch (Exception e) {
            System.out.println("[OpenRASP] Failed to load native library, please refer to https://rasp.baidu.com/doc/install/software.html#faq-v8-load for possible solutions.");
            e.printStackTrace();
            return;
        }
        if (!loadConfig()) {
            return;
        }
        //缓存rasp的build信息
        Agent.readVersion();
        BuildRASPModel.initRaspInfo(Agent.projectVersion, Agent.buildTime, Agent.gitCommit);
        // 初始化插件系统
        if (!JS.Initialize()) {
            return;
        }
        // hook点管理模块初始化
        CheckerManager.init();
        // 字节码转换模块的初始化
        initTransformer(inst);
        if (CloudUtils.checkCloudControlEnter()) {
            CrashReporter.install(Config.getConfig().getCloudAddress() + "/v1/agent/crash/report",
                    Config.getConfig().getCloudAppId(), Config.getConfig().getCloudAppSecret(),
                    CloudCacheModel.getInstance().getRaspId());
        }
        deleteTmpDir();
        String message = "[OpenRASP] Engine Initialized [" + Agent.projectVersion + " (build: GitCommit="
                + Agent.gitCommit + " date=" + Agent.buildTime + ")]";
        System.out.println(message);
        Logger.getLogger(EngineBoot.class.getName()).info(message);
    }

    @Override
    public void release(String mode) {
        CloudManager.stop();
        CpuMonitorManager.release();
        if (transformer != null) {
            transformer.release();
        }
        JS.Dispose();
        CheckerManager.release();
        String message = "[OpenRASP] Engine Released [" + Agent.projectVersion + " (build: GitCommit="
                + Agent.gitCommit + " date=" + Agent.buildTime + ")]";
        System.out.println(message);
    }

    private void deleteTmpDir() {
        try {
            File file = new File(Config.baseDirectory + File.separator + "jar_tmp");
            if (file.exists()) {
                FileUtils.deleteDirectory(file);
            }
        } catch (Throwable t) {
            Logger.getLogger(EngineBoot.class.getName()).warn("failed to delete jar_tmp directory: " + t.getMessage());
        }
    }

    /**
     * 初始化配置
     *
     * @return 配置是否成功
     */
    private boolean loadConfig() throws Exception {
        LogConfig.ConfigFileAppender();
        //单机模式下动态添加获取删除syslog
        if (!CloudUtils.checkCloudControlEnter()) {
            LogConfig.syslogManager();
        } else {
            System.out.println("[OpenRASP] RASP ID: " + CloudCacheModel.getInstance().getRaspId());
        }
        return true;
    }

    /**
     * 初始化类字节码的转换器
     * 字节码转换模块是整个Java RASP的重中之重，OpenRASP是使用的Javassist来操作字节码的，其大致的写法和ASM并无区别，接下来一步步跟进看一下
     * @param inst 用于管理字节码转换器
     *
     */
    private void initTransformer(Instrumentation inst) throws UnmodifiableClassException {
        // 这里添加自定义的拦截类
        // inst.addTransformer( new SqlTransformer(), true );
        inst.addTransformer( new SqlTransformer3(), true );
        // inst.addTransformer( new RpcSpringframeworkRestTemplateTransformer(), true );
        // inst.addTransformer( new RpcApacheHttpClientTransformer(), true );
        // inst.addTransformer( new RpcApacheHttpClient1Transformer(), true );
        // inst.addTransformer( new GetJsonContentFromServlet(), true );
        // inst.addTransformer( new ServletTransformer(), true );
        // inst.addTransformer( new Body(), true );
        // inst.addTransformer( new InsertBeforeAllClass(), true );
        // inst.addTransformer( new TestGetMethodsParamtersTransformer(), true );
        // inst.addTransformer( new DeserializationTransforms(), true );
        // 可以看到在实例化了ClassFileTransformer实现的CustomClassTransformer后
        transformer = new CustomClassTransformer(inst);
        // 调用了一个自己写的retransform方法，在这个方法中对Instrumentation已加载的所有类进行遍历，将其进行类的重新转换
        transformer.retransform();
    }

}
