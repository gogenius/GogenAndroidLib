package com.gogen.lib.demo.pictureselector;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.gogen.lib.demo.R;
import com.gogen.lib.demo.base.BaseActivity;
import com.luck.picture.lib.compress.LubanCompresser;
import com.luck.picture.lib.model.FunctionConfig;
import com.luck.picture.lib.model.LocalMediaLoader;
import com.luck.picture.lib.model.PictureConfig;
import com.yalantis.ucrop.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：邮箱->893855882@qq.com
 * data：16/12/31
 */

public class PicSelMainActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    public static final String TAG = "PicSelMainActivity";
    private RecyclerView recyclerView;
    private GridImageAdapter adapter;
    private RadioGroup rgbs01, rgbs0, rgbs1, rgbs2, rgbs3, rgbs4, rgbs5, rgbs6, rgbs7, rgbs8, rgbs9, rgbs10;
    private RadioButton rb_only_photo;
    private int selectMode = FunctionConfig.MODE_MULTIPLE;
    private int maxSelectNum = 9;// 图片最大可选数量
    private ImageButton minus, plus;
    private EditText select_num;
    private EditText et_w, et_h, et_compress_width, et_compress_height, et_compress_size;
    private TextView first_image_info;
    private LinearLayout ll_luban_wh;
    private boolean isShow = true;
    private boolean isOnlyPhoto = false;
    private int selectType = LocalMediaLoader.TYPE_IMAGE;
    private int copyMode = FunctionConfig.CROP_MODEL_DEFAULT;
    private boolean enablePreview = true;
    private boolean isPreviewVideo = true;
    private boolean enableCrop = true;
    private boolean theme = false;
    private boolean selectImageType = false;
    private int cropW = 0;
    private int cropH = 0;
    private int compressW = 0;
    private int compressH = 0;
    private int compresToSize = 0;
    private boolean isCompress = false;
    private boolean isCheckNumMode = false;
    private int compressFlag = 1;// 1 系统自带压缩 2 luban压缩
    private List<LocalMedia> selectMedia = new ArrayList<>();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pic_sel_a_main);
        mContext = this;
        findViewById(R.id.left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        rgbs01 = (RadioGroup) findViewById(R.id.rgbs01);
        rgbs0 = (RadioGroup) findViewById(R.id.rgbs0);
        rgbs1 = (RadioGroup) findViewById(R.id.rgbs1);
        rgbs2 = (RadioGroup) findViewById(R.id.rgbs2);
        rgbs3 = (RadioGroup) findViewById(R.id.rgbs3);
        rgbs4 = (RadioGroup) findViewById(R.id.rgbs4);
        rgbs5 = (RadioGroup) findViewById(R.id.rgbs5);
        rgbs6 = (RadioGroup) findViewById(R.id.rgbs6);
        rgbs7 = (RadioGroup) findViewById(R.id.rgbs7);
        rgbs8 = (RadioGroup) findViewById(R.id.rgbs8);
        rgbs9 = (RadioGroup) findViewById(R.id.rgbs9);
        rgbs10 = (RadioGroup) findViewById(R.id.rgbs10);
        rb_only_photo = (RadioButton) findViewById(R.id.rb_only_photo);
        ll_luban_wh = (LinearLayout) findViewById(R.id.ll_luban_wh);
        et_compress_width = (EditText) findViewById(R.id.et_compress_width);
        et_compress_height = (EditText) findViewById(R.id.et_compress_height);
        et_compress_size = (EditText) findViewById(R.id.et_compress_size);
        first_image_info = (TextView) findViewById(R.id.first_image_info);
        et_w = (EditText) findViewById(R.id.et_w);
        et_h = (EditText) findViewById(R.id.et_h);
        minus = (ImageButton) findViewById(R.id.minus);
        plus = (ImageButton) findViewById(R.id.plus);
        select_num = (EditText) findViewById(R.id.select_num);
        select_num.setText(maxSelectNum + "");
        FullyGridLayoutManager manager = new FullyGridLayoutManager(PicSelMainActivity.this, 4, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        adapter = new GridImageAdapter(PicSelMainActivity.this, onAddPicClickListener);
        adapter.setSelectMax(maxSelectNum);
        recyclerView.setAdapter(adapter);
        rgbs0.setOnCheckedChangeListener(this);
        rgbs1.setOnCheckedChangeListener(this);
        rgbs2.setOnCheckedChangeListener(this);
        rgbs3.setOnCheckedChangeListener(this);
        rgbs4.setOnCheckedChangeListener(this);
        rgbs5.setOnCheckedChangeListener(this);
        rgbs6.setOnCheckedChangeListener(this);
        rgbs7.setOnCheckedChangeListener(this);
        rgbs8.setOnCheckedChangeListener(this);
        rgbs9.setOnCheckedChangeListener(this);
        rgbs01.setOnCheckedChangeListener(this);
        rgbs10.setOnCheckedChangeListener(this);
        rb_only_photo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isOnlyPhoto = rb_only_photo.isChecked();
            }
        });
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (maxSelectNum > 1) {
                    maxSelectNum--;
                }
                select_num.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
            }
        });
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                maxSelectNum++;
                select_num.setText(maxSelectNum + "");
                adapter.setSelectMax(maxSelectNum);
            }
        });

        adapter.setOnItemClickListener(new GridImageAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                // 这里可预览图片
                //PictureConfig.getPictureConfig().externalPicturePreview(mContext, position, selectMedia);

                List<LocalMedia> tempList = new ArrayList<>();

                LocalMedia tempLocalMedia1 = new LocalMedia();
                tempLocalMedia1.setPath("http://k73.com/up/allimg/120906/22-120Z6140234L9.jpg");
                tempLocalMedia1.setType(LocalMediaLoader.TYPE_IMAGE);
                tempList.add(tempLocalMedia1);

                LocalMedia tempLocalMedia2 = new LocalMedia();
                tempLocalMedia2.setPath("http://img.bizhi.sogou.com/images/2012/03/14/124196.jpg");
                tempLocalMedia2.setType(LocalMediaLoader.TYPE_IMAGE);
                tempList.add(tempLocalMedia2);

                LocalMedia tempLocalMedia3 = new LocalMedia();
                //tempLocalMedia3.setPath("http://n.7k7kimg.cn/2014/1209/1418109581623.gif");
                tempLocalMedia3.setPath("https://nirvana.dev.server/v1/tfs/T1IyETByWT1RCvBVdK.png");
                tempLocalMedia3.setType(LocalMediaLoader.TYPE_IMAGE);
                tempList.add(tempLocalMedia3);

                PictureConfig.getPictureConfig().externalPicturePreview(mContext, position, tempList);
            }
        });

    }

    /**
     * 删除图片回调接口
     */

    private GridImageAdapter.onAddPicClickListener onAddPicClickListener = new GridImageAdapter.onAddPicClickListener() {
        @Override
        public void onAddPicClick(int type, int position) {
            switch (type) {
                case 0:
                    // 进入相册
                    /**
                     * type --> 1图片 or 2视频
                     * copyMode -->裁剪比例，默认、1:1、3:4、3:2、16:9
                     * maxSelectNum --> 可选择图片的数量
                     * selectMode         --> 单选 or 多选
                     * isShow       --> 是否显示拍照选项 这里自动根据type 启动拍照或录视频
                     * isPreview    --> 是否打开预览选项
                     * isCrop       --> 是否打开剪切选项
                     * isPreviewVideo -->是否预览视频(播放) mode or 多选有效
                     * ThemeStyle -->主题颜色
                     * CheckedBoxDrawable -->图片勾选样式
                     * cropW-->裁剪宽度 值不能小于100  如果值大于图片原始宽高 将返回原图大小
                     * cropH-->裁剪高度 值不能小于100
                     * isCompress -->是否压缩图片
                     * setEnablePixelCompress 是否启用像素压缩
                     * setEnableQualityCompress 是否启用质量压缩
                     * setRecordVideoSecond 录视频的秒数，默认不限制
                     * setRecordVideoDefinition 视频清晰度  Constants.HIGH 清晰  Constants.ORDINARY 低质量
                     * setImageSpanCount -->每行显示个数
                     * setCheckNumMode 是否显示QQ选择风格(带数字效果)
                     * setPreviewColor 预览文字颜色
                     * setCompleteColor 完成文字颜色
                     * setPreviewBottomBgColor 预览界面底部背景色
                     * setBottomBgColor 选择图片页面底部背景色
                     * setCompressQuality 设置裁剪质量，默认无损裁剪
                     * setSelectMedia 已选择的图片
                     * setCompressFlag 1为系统自带压缩  2为第三方luban压缩
                     * 注意-->type为2时 设置isPreview or isCrop 无效
                     * 注意：Options可以为空，默认标准模式
                     */
                    String ws = et_w.getText().toString().trim();
                    String hs = et_h.getText().toString().trim();

                    if (!isNull(ws) && !isNull(hs)) {
                        cropW = Integer.parseInt(ws);
                        cropH = Integer.parseInt(hs);
                    }

                    if (!isNull(et_compress_width.getText().toString()) && !isNull(et_compress_height.getText().toString())) {
                        compressW = Integer.parseInt(et_compress_width.getText().toString());
                        compressH = Integer.parseInt(et_compress_height.getText().toString());
                    }

                    if (!isNull(et_compress_size.getText().toString())) {
                        compresToSize = Integer.parseInt(et_compress_size.getText().toString());
                    }

                    int selector = R.drawable.pic_sel_cb;
                    FunctionConfig config = new FunctionConfig();
                    config.setType(selectType);
                    config.setCopyMode(copyMode);
                    config.setCompress(isCompress);
                    config.setEnablePixelCompress(true);
                    config.setEnableQualityCompress(true);
                    config.setMaxSelectNum(maxSelectNum);
                    config.setSelectMode(selectMode);
                    config.setShowCamera(isShow);
                    config.setEnablePreview(enablePreview);
                    config.setEnableCrop(enableCrop);
                    config.setPreviewVideo(isPreviewVideo);
                    config.setRecordVideoDefinition(FunctionConfig.HIGH);// 视频清晰度
                    config.setRecordVideoSecond(60);// 视频秒数
                    config.setCropW(cropW);
                    config.setCropH(cropH);
                    config.setCheckNumMode(isCheckNumMode);
                    config.setCompressQuality(100);
                    config.setImageSpanCount(4);
                    config.setSelectMedia(selectMedia);
                    config.setCompressFlag(compressFlag);
                    config.setCompresToSize(compresToSize);
                    config.setCompressW(compressW);
                    config.setCompressH(compressH);
                    if (theme) {
                        config.setThemeStyle(ContextCompat.getColor(PicSelMainActivity.this, R.color.blue));
                        // 可以自定义底部 预览 完成 文字的颜色和背景色
                        if (!isCheckNumMode) {
                            // QQ 风格模式下 这里自己搭配颜色，使用蓝色可能会不好看
                            config.setPreviewColor(ContextCompat.getColor(PicSelMainActivity.this, R.color.white));
                            config.setCompleteColor(ContextCompat.getColor(PicSelMainActivity.this, R.color.white));
                            config.setPreviewBottomBgColor(ContextCompat.getColor(PicSelMainActivity.this, R.color.blue));
                            config.setBottomBgColor(ContextCompat.getColor(PicSelMainActivity.this, R.color.blue));
                        }
                    }
                    if (selectImageType) {
                        config.setCheckedBoxDrawable(selector);
                    }

                    // 先初始化参数配置，在启动相册
                    PictureConfig.init(config);

                    if (isOnlyPhoto) {
                        // 只拍照
                        PictureConfig.getPictureConfig().openCamera(mContext, resultCallback);
                    } else {
                        // 打开相册
                        PictureConfig.getPictureConfig().openPhoto(mContext, resultCallback);
                    }

                    // 只压缩
                    //PictureConfig.getPictureConfig().externalPictureCompresser(mContext,
                    //        resultCallback, config, "/storage/emulated/0/ImageSelector/CameraImage/ImageSelector_20170410_102849.JPEG", null);

                    break;
                case 1:
                    // 删除图片
                    selectMedia.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;
            }
        }
    };


    /**
     * 图片回调方法
     */
    private PictureConfig.OnSelectResultCallback resultCallback = new PictureConfig.OnSelectResultCallback() {
        @Override
        public void onSelectSuccess(List<LocalMedia> resultList) {
            selectMedia = resultList;
            Log.i("callBack_result", "size===" + selectMedia.size() + "\nselectMedia===" + JSON.toJSONString(selectMedia));

            if (selectMedia != null) {
                adapter.setList(selectMedia);
                adapter.notifyDataSetChanged();

                if (selectMedia.get(0).isCompressed()) {

                    first_image_info.setText("nrl=" + LubanCompresser.getImageSize(selectMedia.get(0).getPath())[0]
                            + "*" + LubanCompresser.getImageSize(selectMedia.get(0).getPath())[1]
                            + "size=" + (new File(selectMedia.get(0).getPath()).length() / 1024)
                            + "\ncprs=" + LubanCompresser.getImageSize(selectMedia.get(0).getCompressPath())[0]
                            + "*" + LubanCompresser.getImageSize(selectMedia.get(0).getCompressPath())[1]
                            + "size=" + (new File(selectMedia.get(0).getCompressPath()).length() / 1024));

                }

                //fileSize.setText(imgFile.length() / 1024 + "k");
                //imageSize.setText(Luban.get(this).getImageSize(imgFile.getPath())[0] + " * " + Luban.get(this).getImageSize(imgFile.getPath())[1]);
            }
        }
    };


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_ordinary:
                isCheckNumMode = false;
                break;
            case R.id.rb_qq:
                isCheckNumMode = true;
                break;
            case R.id.rb_single:
                selectMode = FunctionConfig.MODE_SINGLE;
                break;
            case R.id.rb_multiple:
                selectMode = FunctionConfig.MODE_MULTIPLE;
                break;
            case R.id.rb_image:
                selectType = LocalMediaLoader.TYPE_IMAGE;
                break;
            case R.id.rb_video:
                selectType = LocalMediaLoader.TYPE_VIDEO;
                break;
            case R.id.rb_photo_display:
                isShow = true;
                break;
            case R.id.rb_photo_hide:
                isShow = false;
            case R.id.rb_only_photo:
                isOnlyPhoto = rb_only_photo.isChecked();
                break;
            case R.id.rb_default:
                copyMode = FunctionConfig.CROP_MODEL_DEFAULT;
                break;
            case R.id.rb_to1_1:
                copyMode = FunctionConfig.CROP_MODEL_1_1;
                break;
            case R.id.rb_to3_2:
                copyMode = FunctionConfig.CROP_MODEL_3_2;
                break;
            case R.id.rb_to3_4:
                copyMode = FunctionConfig.CROP_MODEL_3_4;
                break;
            case R.id.rb_to16_9:
                copyMode = FunctionConfig.CROP_MODEL_16_9;
                break;
            case R.id.rb_preview:
                enablePreview = true;
                break;
            case R.id.rb_preview_false:
                enablePreview = false;
                break;
            case R.id.rb_preview_video:
                isPreviewVideo = true;
                break;
            case R.id.rb_preview_video_false:
                isPreviewVideo = false;
                break;
            case R.id.rb_yes_copy:
                enableCrop = true;
                break;
            case R.id.rb_no_copy:
                enableCrop = false;
                break;
            case R.id.rb_theme1:
                theme = false;
                break;
            case R.id.rb_theme2:
                theme = true;
                break;
            case R.id.rb_select1:
                selectImageType = false;
                break;
            case R.id.rb_select2:
                selectImageType = true;
                break;
            case R.id.rb_compress_false:
                isCompress = false;
                rgbs10.setVisibility(View.GONE);
                ll_luban_wh.setVisibility(View.GONE);
                et_compress_height.setText("");
                et_compress_width.setText("");
                break;
            case R.id.rb_compress_true:
                isCompress = true;
                if (compressFlag == 2) {
                    ll_luban_wh.setVisibility(View.VISIBLE);
                }
                rgbs10.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_system:
                compressFlag = 1;
                ll_luban_wh.setVisibility(View.GONE);
                et_compress_height.setText("");
                et_compress_width.setText("");
                break;
            case R.id.rb_luban:
                compressFlag = 2;
                ll_luban_wh.setVisibility(View.VISIBLE);
                break;
        }
    }


    /**
     * 判断 一个字段的值否为空
     * @param s
     * @return
     * @author Michael.Zhang 2013-9-7 下午4:39:00
     */

    public boolean isNull(String s) {
        if (null == s || s.equals("") || s.equalsIgnoreCase("null")) {
            return true;
        }

        return false;
    }
}
