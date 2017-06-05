package com.apkfuns.logutils;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.apkfuns.logutils.LogLevel.LogLevelType;
import static com.apkfuns.logutils.LogLevel.TYPE_DEBUG;
import static com.apkfuns.logutils.LogLevel.TYPE_ERROR;
import static com.apkfuns.logutils.LogLevel.TYPE_INFO;
import static com.apkfuns.logutils.LogLevel.TYPE_VERBOSE;
import static com.apkfuns.logutils.LogLevel.TYPE_WARM;
import static com.apkfuns.logutils.LogLevel.TYPE_WTF;
import static com.apkfuns.logutils.utils.CommonUtil.DIVIDER_BOTTOM;
import static com.apkfuns.logutils.utils.CommonUtil.DIVIDER_CENTER;
import static com.apkfuns.logutils.utils.CommonUtil.DIVIDER_NORMAL;
import static com.apkfuns.logutils.utils.CommonUtil.DIVIDER_TOP;
import static com.apkfuns.logutils.utils.CommonUtil.largeStringToList;
import static com.apkfuns.logutils.utils.CommonUtil.printDividingLine;
import static com.apkfuns.logutils.utils.ObjectUtil.objectToString;

/**
 * Created by pengwei08 on 2015/7/20.
 */
// TODO: 16/3/22 泛型支持
final class Logger implements Printer {

    private static final String TAG = "Logger";

    private LogConfigImpl mLogConfig;

    protected Logger() {
        mLogConfig = LogConfigImpl.getInstance();
        mLogConfig.addParserClass(Constant.DEFAULT_PARSE_CLASS);
    }

    /**
     * 打印字符串
     * @param type
     * @param msg
     * @param args
     */
    private void logString(@LogLevelType int type, String tag, String msg, Object... args) {

        if (null != args && args.length > 0) {
            msg += getObjectsString(args);
        }

        //Log.i(TAG, "打印字符串 type===" + LogLevel.TYPE_LIST[type] + " &msg.length===" + msg.length());

        logString(type, tag, msg, false);

    }

    private void logString(@LogLevelType int type, String tag, String msg, boolean isPart) {

        if (!mLogConfig.isEnable()) {
            return;
        }
        if (type < mLogConfig.getLogLevel()) {
            return;
        }

        String tagTemp;
        if (mLogConfig.isUseDefaultTag() || null == tag || tag.length() == 0) {
            tagTemp = generateTag(type);
        } else {
            tagTemp = tag;
        }

        if (msg.length() > Constant.LINE_MAX) {
            if (mLogConfig.isShowBorder()) {
                printLog(type, tagTemp, printDividingLine(DIVIDER_TOP));
                printLog(type, tagTemp, printDividingLine(DIVIDER_NORMAL) + getTopStackInfo(type));
                printLog(type, tagTemp, printDividingLine(DIVIDER_CENTER));
            }
            for (String subMsg : largeStringToList(msg)) {

                logString(type, tagTemp, subMsg, true);

            }
            if (mLogConfig.isShowBorder()) {
                printLog(type, tagTemp, printDividingLine(DIVIDER_BOTTOM));
            }
            return;
        }

        if (mLogConfig.isShowBorder()) {
            if (isPart) {
                for (String sub : msg.split(Constant.BR)) {
                    printLog(type, tagTemp, printDividingLine(DIVIDER_NORMAL) + sub);
                }
            } else {

                printLog(type, tagTemp, printDividingLine(DIVIDER_TOP));
                printLog(type, tagTemp, printDividingLine(DIVIDER_NORMAL) + getTopStackInfo(type));
                printLog(type, tagTemp, printDividingLine(DIVIDER_CENTER));
                for (String sub : msg.split(Constant.BR)) {
                    printLog(type, tagTemp, printDividingLine(DIVIDER_NORMAL) + sub);
                }

                printLog(type, tagTemp, printDividingLine(DIVIDER_BOTTOM));
            }
        } else {
            printLog(type, tagTemp, msg);
        }
    }


    /**
     * 打印对象
     * @param type
     * @param object
     */
    private void logObject(@LogLevelType int type, Object object) {
        logString(type, "", objectToString(object));
    }

    /**
     * 自动生成tag
     * @param type
     * @return
     */
    private String generateTag(int type) {
        if (!mLogConfig.isShowBorder()) {
            return mLogConfig.getTagPrefix() + "/" + getTopStackInfo(type);
        }
        return mLogConfig.getTagPrefix();
    }

    /**
     * 获取最顶部stack信息
     * @param type
     * @return
     */
    private String getTopStackInfo(int type) {

        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int stackOffset = getStackOffset(trace);
        if (stackOffset == -1) {
            return null;
        }

        StackTraceElement caller = trace[stackOffset];
        String stackTrace = caller.toString();
        stackTrace = stackTrace.substring(stackTrace.lastIndexOf('('), stackTrace.length());
        String tag = "%s.%s%s";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), stackTrace);

        return LogLevel.TYPE_LIST[type] + ": " + new SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.getDefault()).format(new Date()) + ": " + tag;

    }

    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = Constant.MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (name.equals(LogUtils.class.getName())) {
                return ++i;
            }
        }
        return -1;
    }

    @Override
    public void d(String tag, String message, Object... args) {
        logString(TYPE_DEBUG, tag, message, args);
    }

    @Override
    public void d(String message, Object... args) {
        logString(TYPE_DEBUG, "", message, args);
    }

    @Override
    public void d(Object object) {
        logObject(TYPE_DEBUG, object);
    }

    @Override
    public void e(String tag, String message, Object... args) {
        logString(TYPE_ERROR, tag, message, args);
    }

    @Override
    public void e(String message, Object... args) {
        logString(TYPE_ERROR, "", message, args);
    }

    @Override
    public void e(Object object) {
        logObject(TYPE_ERROR, object);
    }

    @Override
    public void w(String tag, String message, Object... args) {
        logString(TYPE_WARM, tag, message, args);
    }

    @Override
    public void w(String message, Object... args) {
        logString(TYPE_WARM, "", message, args);
    }

    @Override
    public void w(Object object) {
        logObject(TYPE_WARM, object);
    }

    @Override
    public void i(String tag, String message, Object... args) {
        logString(TYPE_INFO, tag, message, args);
    }

    @Override
    public void i(String message, Object... args) {
        logString(TYPE_INFO, "", message, args);
    }

    @Override
    public void i(Object object) {
        logObject(TYPE_INFO, object);
    }

    @Override
    public void v(String tag, String message, Object... args) {
        logString(TYPE_VERBOSE, tag, message, args);
    }

    @Override
    public void v(String message, Object... args) {
        logString(TYPE_VERBOSE, "", message, args);
    }

    @Override
    public void v(Object object) {
        logObject(TYPE_VERBOSE, object);
    }

    @Override
    public void wtf(String tag, String message, Object... args) {
        logString(TYPE_WTF, tag, message, args);
    }

    @Override
    public void wtf(String message, Object... args) {
        logString(TYPE_WTF, "", message, args);
    }

    @Override
    public void wtf(Object object) {
        logObject(TYPE_WTF, object);
    }

    @Override
    public void json(String tag, String msg, Object... json) {
        int indent = 4;
        if (TextUtils.isEmpty(json.toString())) {
            d(tag, "JSON{json is null}");
            return;
        }
        try {

            String jsonTemp = json.toString();

            if (jsonTemp.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(jsonTemp);
                String jsonResult = jsonObject.toString(indent);
                d(tag, msg, jsonResult);
            } else if (jsonTemp.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(jsonTemp);
                String jsonResult = jsonArray.toString(indent);
                d(tag, msg, jsonResult);
            }

        } catch (JSONException e) {
            e(tag, "JSONException", e);
        }
    }

    @Override
    public void json(String json) {
        int indent = 4;
        if (TextUtils.isEmpty(json)) {
            d("JSON{json is null}");
            return;
        }
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String msg = jsonObject.toString(indent);
                d(msg);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String msg = jsonArray.toString(indent);
                d(msg);
            }
        } catch (JSONException e) {
            e(e);
        }
    }

    /**
     * 处理多个参数，一一列举
     * @param objects
     * @return
     */
    private static String getObjectsString(Object... objects) {

        if (objects.length > 1) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n");
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];
                if (object == null) {
                    stringBuilder.append("Object").append("[").append(i).append("]").append(" = ").append("null").append("\n");
                } else {
                    stringBuilder.append("Object").append("[").append(i).append("]").append(" = ").append(objectToString(object)).append("\n");
                }
            }
            return stringBuilder.toString();
        } else {
            Object object = objectToString(objects[0]);
            return object == null ? "" : "\n" + "Object[0] = " + object.toString();
        }
    }

    /**
     * 打印日志
     * @param type
     * @param tag
     * @param msg
     */
    private void printLog(@LogLevelType int type, String tag, String msg) {

        switch (type) {
            case TYPE_VERBOSE:
                Log.v(tag, msg);
                break;
            case TYPE_DEBUG:
                Log.d(tag, msg);
                break;
            case TYPE_INFO:
                Log.i(tag, msg);
                break;
            case TYPE_WARM:
                Log.w(tag, msg);
                break;
            case TYPE_ERROR:
                Log.e(tag, msg);
                break;
            case TYPE_WTF:
                Log.wtf(tag, msg);
                break;
            default:
                break;
        }

        if (mLogConfig.isLogToFile()) {

            if (null == mLogConfig.getDoTagList() || mLogConfig.getDoTagList().size() == 0) {
                LogFile.printFile(tag, mLogConfig.getFilePath(), mLogConfig.getFileName(), "", msg + "\n", mLogConfig.isAppend());
            } else if (mLogConfig.getDoTagList().contains(tag)) {
                LogFile.printFile(tag, mLogConfig.getFilePath(), mLogConfig.getFileName(), "", msg + "\n", mLogConfig.isAppend());
            }

            return;
        }

    }

}
