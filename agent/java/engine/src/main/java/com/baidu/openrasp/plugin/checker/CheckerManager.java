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

package com.baidu.openrasp.plugin.checker;

import com.baidu.openrasp.plugin.checker.CheckParameter.Type;

import java.util.EnumMap;

/**
 * Created by tyy on 17-11-20.
 *
 * 用于管理 hook 点参数的检测
 *
 * hook点管理的初始化过程非常简单，就是遍历
 * com.baidu.openrasp.plugin.checkerCheckParameter的Type
 * 将其中的元素添加进枚举映射中
 *
 */
public class CheckerManager {

    private static EnumMap<Type, Checker> checkers = new EnumMap<Type, Checker>(Type.class);

    public synchronized static void init() throws Exception {
        for (Type type : Type.values()) {
            checkers.put(type, type.checker);
        }
    }

    public synchronized static void release() {
        checkers = null;
    }

    // 这里的checkers是在hook点管理模块初始化时设置的枚举类映射
    // OGNL("ognl", new V8AttackChecker(), 1 << 10)
    public static boolean check(Type type, CheckParameter parameter) {
        return checkers.get(type).check(parameter);
    }

}
