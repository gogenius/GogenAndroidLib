package com.apkfuns.logutils;

import java.util.List;

/**
 * Created by pengwei on 16/3/4.
 */
public interface LogConfig {

    LogConfig configAllowLog(boolean allowLog);

    LogConfig configTag(boolean useDefault, String prefix);

    LogConfig configShowBorders(boolean showBorder);

    LogConfig configLevel(@LogLevel.LogLevelType int logLevel);

    LogConfig addParserClass(Class<? extends Parser>... classes);

    LogConfig logToFile(boolean logToFile, String filePath, String fileName, boolean isAppend, List<String> doTagList);
}
