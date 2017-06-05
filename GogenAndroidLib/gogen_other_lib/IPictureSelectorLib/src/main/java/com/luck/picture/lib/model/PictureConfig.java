package com.luck.picture.lib.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.luck.picture.lib.R;
import com.luck.picture.lib.compress.CompressConfig;
import com.luck.picture.lib.compress.CompressImageOptions;
import com.luck.picture.lib.compress.CompressInterface;
import com.luck.picture.lib.compress.LubanOptions;
import com.luck.picture.lib.ui.PictureAlbumDirectoryActivity;
import com.luck.picture.lib.ui.PictureExternalPreviewActivity;
import com.luck.picture.lib.ui.PictureImageGridActivity;
import com.yalantis.ucrop.entity.LocalMedia;
import com.yalantis.ucrop.util.Utils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.util
 * email：893855882@qq.com
 * data：17/1/5
 */
public class PictureConfig {

    public static FunctionConfig config;
    public static PictureConfig pictureConfig;


    public static PictureConfig getPictureConfig() {
        if (pictureConfig == null) {
            pictureConfig = new PictureConfig();
        }

        return pictureConfig;
    }

    public PictureConfig() {
        super();
    }


    public OnSelectResultCallback resultCallback;

    public OnSelectResultCallback getResultCallback() {
        return resultCallback;
    }

    public static void init(FunctionConfig functionConfig) {
        config = functionConfig;
    }

    /**
     * 启动相册
     */
    public void openPhoto(Context mContext, OnSelectResultCallback resultCall) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
        if (config == null) {
            config = new FunctionConfig();
        }
        // 这里仿ios微信相册启动模式
        Intent intent1 = new Intent(mContext, PictureAlbumDirectoryActivity.class);
        Intent intent2 = new Intent(mContext, PictureImageGridActivity.class);
        Intent[] intents = new Intent[2];
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intents[0] = intent1;
        intents[1] = intent2;
        intent1.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
        intent2.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
        mContext.startActivities(intents);
        ((Activity) mContext).overridePendingTransition(R.anim.ucrop_slide_bottom_in, 0);
        // 绑定图片接口回调函数事件
        resultCallback = resultCall;
    }

    /**
     * start to camera、preview、crop
     */
    public void openCamera(Context mContext, OnSelectResultCallback resultCall) {
        if (config == null) {
            config = new FunctionConfig();
        }
        Intent intent = new Intent(mContext, PictureImageGridActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(FunctionConfig.EXTRA_THIS_CONFIG, config);
        intent.putExtra(FunctionConfig.FUNCTION_TAKE, true);
        mContext.startActivity(intent);
        ((Activity) mContext).overridePendingTransition(R.anim.ucrop_fade, R.anim.ucrop_hold);
        // 绑定图片接口回调函数事件
        resultCallback = resultCall;
    }

    /**
     * 外部图片预览
     * @param position
     * @param medias
     */
    public void externalPicturePreview(Context mContext, int position, List<LocalMedia> medias) {
        if (medias != null && medias.size() > 0) {
            Intent intent = new Intent();
            intent.putExtra(FunctionConfig.EXTRA_PREVIEW_SELECT_LIST, (Serializable) medias);
            intent.putExtra(FunctionConfig.EXTRA_POSITION, position);
            intent.setClass(mContext, PictureExternalPreviewActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(R.anim.ucrop_toast_enter, 0);
        }
    }

    //===========================================压缩处理============================================


    /**
     * 外部图片压缩
     * @param mContext   上下文
     * @param resultCall 回调
     * @param config     配置属性
     * @param filePath   单图片路径
     * @param result     列表
     */
    public void externalPictureCompresser(Context mContext, final OnSelectResultCallback resultCall, FunctionConfig config,
                                          String filePath, List<LocalMedia> result) {


        if (null != filePath && filePath.length() > 0) {
            if (!(new File(filePath).exists())) {
                Log.e("PictureConfig", "file not exists");
                return;
            }
            result = new ArrayList<>();
            LocalMedia m = new LocalMedia();
            m.setPath(filePath);
            m.setType(LocalMediaLoader.TYPE_IMAGE);
            result.add(m);
        }

        //showDialog(mContext, "处理中...");
        CompressConfig compress_config = CompressConfig.ofDefaultConfig();
        switch (config.getCompressFlag()) {
            case 1:
                // 系统自带压缩
                compress_config.enablePixelCompress(config.isEnablePixelCompress());
                compress_config.enableQualityCompress(config.isEnableQualityCompress());
                compress_config.setCompresToSize(config.getCompresToSize());
                break;
            case 2:
                // luban压缩
                LubanOptions option = new LubanOptions.Builder()
                        .setMaxHeight(config.getCompressH())
                        .setMaxWidth(config.getCompressW())
                        .setMaxSize(FunctionConfig.MAX_COMPRESS_SIZE)
                        .setCompresToSize(config.getCompresToSize())
                        .create();
                compress_config = CompressConfig.ofLuban(option);
                break;
        }

        CompressImageOptions.compress(mContext, compress_config, result, new CompressInterface.CompressListener() {
            @Override
            public void onCompressSuccess(List<LocalMedia> images) {
                // 压缩成功回调

                // 因为这里是单一实例的结果集，重新用变量接收一下在返回，不然会产生结果集被单一实例清空的问题
                List<LocalMedia> result = new ArrayList<>();
                for (LocalMedia media : images) {
                    result.add(media);
                }

                if (resultCall != null) {
                    resultCall.onSelectSuccess(result);
                }

                //dismiss();
            }

            @Override
            public void onCompressError(List<LocalMedia> images, String msg) {
                // 压缩失败回调 返回原图

                if (resultCall != null) {
                    resultCall.onSelectSuccess(images);
                }

                //dismiss();
            }
        }).compress();
    }

    /**
     * 处理结果
     */
    public interface OnSelectResultCallback {
        /**
         * 处理成功
         * @param resultList
         */
        void onSelectSuccess(List<LocalMedia> resultList);

    }

}
