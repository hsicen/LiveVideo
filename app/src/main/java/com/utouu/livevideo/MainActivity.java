package com.utouu.livevideo;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.utouu.livevideo.adapter.TableFragmentAdapter;
import com.utouu.livevideo.fragment.CommentFragment;
import com.utouu.livevideo.fragment.RewardFragment;
import com.utouu.livevideo.view.HVideoPlayer;

import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCUserAction;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;

/**
 * Create by 黄思程 on 2017/2/16  10:49
 * Function：
 * Desc：视频直播和弹幕功能的实现
 */
public class MainActivity extends AppCompatActivity {

    /**
     * 标准状态下播放器对象
     */
    private HVideoPlayer mVideoPlayerStandard;
    /**
     * 全屏状态下播放器对象
     */
    public HVideoPlayer mFullScreenPlayer;

    private List<Fragment> mFragmentList = new ArrayList<>();
    private TableFragmentAdapter mViewPageradapter;
    private EditText mMsgEditText;
    private TabLayout mTabLayout;

    private JCVideoPlayer.JCAutoFullscreenListener sensorEventListener;
    private SensorManager sensorManager;

    private int mRoomId;
    private String mBeginTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    /**
     * 布局初始化
     */
    private void initView() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new JCVideoPlayer.JCAutoFullscreenListener();

        mVideoPlayerStandard = (HVideoPlayer) findViewById(R.id.custom_videoplayer_standard);
        mMsgEditText = (EditText) findViewById(R.id.message);
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);

        Button sendBtn = (Button) findViewById(R.id.sendMsg);
        mFragmentList.add(CommentFragment.newInstance());
        mFragmentList.add(RewardFragment.newInstance());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPageradapter = new TableFragmentAdapter(getSupportFragmentManager()
                ,mFragmentList);
        viewPager.setAdapter(mViewPageradapter);
        mTabLayout.setupWithViewPager(viewPager);


        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgContent = mMsgEditText.getText().toString();
                sendMessage(msgContent);
            }
        });

        mVideoPlayerStandard.setOnSendMsgListener(new HVideoPlayer.OnSendMsgListener() {
            @Override
            public void sendMsg(String msg) {
                sendMessage(msg);
            }
        });

        mVideoPlayerStandard.setOnPayListener(new HVideoPlayer.OnPayListener() {
            @Override
            public void showPay() {
                showGift();
            }
        });

        mVideoPlayerStandard.setOnFullScreenListener(new HVideoPlayer.OnFullScreenListener() {
            @Override
            public void onFullScreen(HVideoPlayer hVideoPlayer) {
                mFullScreenPlayer = hVideoPlayer;
            }
        });

        /**
         * 设置用户的状态监听
         */
        mVideoPlayerStandard.setJcUserAction(new JCUserAction() {
            @Override
            public void onEvent(int type, String url, int screen, Object... objects) {
                switch (type) {
                    case JCVideoPlayer.CURRENT_STATE_PLAYING:
                        break;
                    case JCVideoPlayer.CURRENT_STATE_PAUSE:
                        break;
                }
            }
        });

        //rtmp://203.207.99.19:1935/live/CCTV5
        //http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8
        //http://ivi.bupt.edu.cn/hls/cctv1.m3u8
        //设置播放源
        mVideoPlayerStandard.setUp("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8",
                JCVideoPlayer.SCREEN_LAYOUT_NORMAL,"CCTV-1 HD");
        mVideoPlayerStandard.thumbImageView.setImageResource(R.drawable.abc);

        //自动进入播放
        mVideoPlayerStandard.startButton.performClick();
    }

    /**
     * 点击送礼调用的方法
     */
    private void showGift() {
        Toast.makeText(this, "该功能正在开发中....", Toast.LENGTH_SHORT).show();
    }

    /**
     * 发送消息调用的方法
     * @param msgContent
     */
    private void sendMessage(String msgContent) {
        //判断是否全屏,全屏则显示弹幕
        if (mFullScreenPlayer != null && mFullScreenPlayer.isFullScreen()){
            mFullScreenPlayer.addDanmaku(msgContent,true);
        }
        mVideoPlayerStandard.addDanmaku(msgContent,true);
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()){
            //隐藏弹幕
            if (mFullScreenPlayer != null){
                mFullScreenPlayer.hideDanmu();
                mFullScreenPlayer = null;
            }
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoPlayerStandard.danmaDes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
        JCVideoPlayer.releaseAllVideos();
        if (mFullScreenPlayer != null){
            mFullScreenPlayer.hideDanmu();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorEventListener, accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        mVideoPlayerStandard.danmaResume();
    }
}






