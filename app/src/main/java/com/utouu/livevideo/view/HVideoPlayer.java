package com.utouu.livevideo.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.utouu.livevideo.R;

import java.util.Random;

import fm.jiecao.jcvideoplayer_lib.JCUtils;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDanmakus;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.ui.widget.DanmakuView;

/**
 * Create by 黄思程 on 2017/2/16   10:55
 * Function：
 * Desc：自定义视频播放器，继承于JCVideoPlayer
 */
public class HVideoPlayer extends JCVideoPlayerStandard{

    /**
     * 是否显示弹幕库
     */
    private boolean showDanmaku;

    /**
     * 弹幕View
     */
    private DanmakuView danmakuView;

    private DanmakuContext danmakuContext;
    private Context mContext;

    private EditText mEiEditText;

    private ImageView mSendImage;
    private ImageView mRewardBtn;

    private ProgressBar mProgress;

    private OnSendMsgListener mSendListener;
    private OnPayListener mShowPay;
    private OnFullScreenListener mFullScreenListener;

    private BaseDanmakuParser parser = new BaseDanmakuParser() {
        @Override
        protected IDanmakus parse() {
            return new Danmakus();
        }
    };



    public HVideoPlayer(Context context) {
        super(context);
    }

    public HVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 重写init方法，初始化我们自己添加的控件
     * @param context
     */
    @Override
    public void init(Context context) {
        super.init(context);

        this.mContext = context;
        mEiEditText = (EditText) findViewById(R.id.msg_edittext);
        mSendImage = ((ImageView) findViewById(R.id.send_img));
        mRewardBtn = ((ImageView) findViewById(R.id.reward_img));

        /**
         * 发送弹幕消息的监听
         */
        mSendImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mEiEditText.getText().toString();
                mSendListener.sendMsg(content);
                mEiEditText.setText("");
            }
        });

        /**
         * 点击打赏的监听
         */
        mRewardBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowPay.showPay();
            }
        });
    }

    @Override
    public void startDismissControlViewTimer() {
        //重写父类方法，防止自动隐藏播放器工具栏。
        //如需要自动隐藏请删除此方法或调用super.startDismissControlViewTimer();
        //super.startDismissControlViewTimer();
    }

   /**
     * 初始化弹幕
     */
    private void initDanmu(){

        ViewGroup vp = (ViewGroup) (JCUtils.scanForActivity(getContext()))//.getWindow().getDecorView();
                .findViewById(Window.ID_ANDROID_CONTENT);
        LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,sp2px(48),0,sp2px(48));

        danmakuView = new DanmakuView(mContext);
        vp.addView(danmakuView,lp);
        danmakuView.enableDanmakuDrawingCache(true);
        danmakuView.setCallback(new DrawHandler.Callback() {
            @Override
            public void prepared() {
                showDanmaku = true;
                danmakuView.start();
                generateSomeDanmaku();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuContext = DanmakuContext.create();
        danmakuView.prepare(parser,danmakuContext);
    }

    /**
     * 重写OnError方法，视频播放错误的时候隐藏弹幕
     * @param what
     * @param extra
     */
    @Override
    public void onError(int what, int extra) {
        super.onError(what, extra);
        if (what != 38 && what != -38){
            hideDanmu();
        }
    }

    /**
     * 随机生成测试弹幕内容
     */
    private void generateSomeDanmaku(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (showDanmaku){
                    int time = new Random().nextInt(500);
                    String content = "测试:"+time;
                    addDanmaku(content,false);
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 向弹幕View中添加一条弹幕
     * @param content 弹幕内容
     * @param withBorder 对否有边框
     */
    public void addDanmaku(String content, boolean withBorder) {
        //创建一个从左到右的弹幕库
        BaseDanmaku danmaku = danmakuContext.mDanmakuFactory.
                createDanmaku(BaseDanmaku.TYPE_SCROLL_LR);
        if (danmaku == null || danmakuView == null){
            return;
        }

        //弹幕内容相关属性设置
        danmaku.text = content;
        danmaku.padding = 5;
        danmaku.textSize = sp2px(20);
        danmaku.textColor = Color.WHITE;
        danmaku.textShadowColor = Color.CYAN;
        danmaku.setTime(danmakuView.getCurrentTime());
        if (withBorder){
            danmaku.borderColor = Color.GREEN;
        }

        danmakuView.addDanmaku(danmaku);
    }

    /**
     * 开启弹幕调用的方法封装
     */
    public void danmaResume(){
        if (danmakuView != null && danmakuView.isPrepared()
                && danmakuView.isPaused()){
            danmakuView.resume();
        }
    }

    /**
     * 隐藏弹幕的方法封装
     */
    public void hideDanmu() {
        ViewGroup vp = (ViewGroup) JCUtils.scanForActivity(getContext())
                .findViewById(Window.ID_ANDROID_CONTENT);
        if (danmakuView != null){
            danmakuView.release();
            showDanmaku = false;
            vp.removeView(danmakuView);
            danmakuView = null;
        }
    }

    /**
     * 关闭弹幕时调用的方法封装
     */
    public void danmaDes(){
        showDanmaku = false;
        if (danmakuView != null){
            danmakuView.release();
            danmakuView = null;
        }
    }


    /**
     * 重写初始化播放器参数
     * @param url
     * @param screen
     * @param objects
     */
    @Override
    public void setUp(String url, int screen, Object... objects) {
        //强制全屏
        FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        //添加全屏下的参数
        if (objects.length != 1){
            mSendListener = (OnSendMsgListener) objects[1];
            mShowPay = (OnPayListener) objects[2];
            mFullScreenListener = (OnFullScreenListener) objects[3];
        }
        super.setUp(url,screen,objects[0],mSendListener,mShowPay,mFullScreenListener);

        //全屏下展示弹幕
        if (currentScreen == SCREEN_WINDOW_FULLSCREEN){
//            initDanmu(); 非全屏下展示弹幕功能
            mEiEditText.setVisibility(View.VISIBLE);
            mSendImage.setVisibility(View.VISIBLE);
            mFullScreenListener.onFullScreen(this);
        }else if (currentScreen == SCREEN_LAYOUT_NORMAL
                || currentScreen == SCREEN_LAYOUT_LIST){
            initDanmu();
            mEiEditText.setVisibility(View.INVISIBLE);
            mSendImage.setVisibility(View.INVISIBLE);
        }

        //点击返回按钮隐藏弹幕
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideDanmu();
                backPress();
            }
        });

        //重写全屏按钮点击事件
        fullscreenButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentState == CURRENT_STATE_AUTO_COMPLETE) return;
                if (currentScreen == SCREEN_WINDOW_FULLSCREEN){
                    //退出全屏,隐藏弹幕
                    hideDanmu();
                    backPress();
                }else {
                    //设置全屏
                    startWindowFullscreen();
                }
            }
        });
    }

    /**
     * 设置自定义布局id
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.custom_video_player;
    }

    /**
     * 判断当前是否全屏的方法封装
     * @return
     */
    public boolean isFullScreen(){
        return currentScreen == SCREEN_WINDOW_FULLSCREEN;
    }

    /**
     * 设置发送弹幕监听器方法的封装
     * @param lis
     */
    public void setOnSendMsgListener(OnSendMsgListener lis){
        this.mSendListener = lis;
    }

    /**
     * 设置打赏监听器方法的封装
     * @param mShowPay
     */
    public void setOnPayListener(OnPayListener mShowPay) {
        this.mShowPay = mShowPay;
    }

    /**
     * 设置全屏监听器的方法封装
     * @param onFullScreenListener
     */
    public void setOnFullScreenListener(OnFullScreenListener onFullScreenListener){
        this.mFullScreenListener = onFullScreenListener;
    }

    /**
     * sp转px的方法。
     */
    public int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 发送消息的回调
     */
    public interface OnSendMsgListener{
        void sendMsg(String msg);
    }

    /**
     * 支付的回调
     */
    public interface OnPayListener{
        void showPay();
    }

    /**
     * 全屏的回调
     */
    public interface OnFullScreenListener{
        void onFullScreen(HVideoPlayer hVideoPlayer);
    }
}














