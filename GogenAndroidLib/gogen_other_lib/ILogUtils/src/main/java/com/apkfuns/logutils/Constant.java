package com.apkfuns.logutils;

import com.apkfuns.logutils.parser.BundleParse;
import com.apkfuns.logutils.parser.CollectionParse;
import com.apkfuns.logutils.parser.IntentParse;
import com.apkfuns.logutils.parser.MapParse;
import com.apkfuns.logutils.parser.ReferenceParse;
import com.apkfuns.logutils.parser.ThrowableParse;

import java.util.List;

/**
 * Created by pengwei on 16/4/18.
 */
public class Constant {

    public static final String STRING_OBJECT_NULL = "Object[object is null]";

    // 每行最大日志长度-1024 * 3系统Log每行的极限值
    //public static final int LINE_MAX = 1024 * 3;
    public static final int LINE_MAX = 1024 * 3;

    // 解析属性最大层级---超过1时，输出对象过大会内存溢出，慎用
    public static final int MAX_CHILD_LEVEL = 1;

    public static final int MIN_STACK_OFFSET = 5;

    //StringBuilder最大值
    //public static final int MAX_CARCH = 1024 * 5;
    //public static final int MAX_CARCH = 1024 * 20;

    // 换行符
    public static final String BR = System.getProperty("line.separator");

    // 空格
    public static final String SPACE = "\t";

    // 默认支持解析库
    public static final Class<? extends Parser>[] DEFAULT_PARSE_CLASS = new Class[]{
            BundleParse.class, IntentParse.class, CollectionParse.class,
            MapParse.class, ThrowableParse.class, ReferenceParse.class
    };


    /**
     * 获取默认解析类
     * @return
     */
    public static final List<Parser> getParsers() {
        return LogConfigImpl.getInstance().getParseList();
    }
}
