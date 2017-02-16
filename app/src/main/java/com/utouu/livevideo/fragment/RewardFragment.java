package com.utouu.livevideo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.utouu.livevideo.R;

/**
 * Create by 黄思程 on 2017/2/16   14:28
 * Function：
 * Desc：礼物详情页面
 */
public class RewardFragment extends Fragment{

    private TextView mGiftText;

    public RewardFragment() {
    }

    public static RewardFragment newInstance(){
        RewardFragment fragment = new RewardFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View giftView = inflater.inflate(R.layout.fragment_gift, container, false);
        mGiftText = ((TextView) giftView.findViewById(R.id.tv_gift));

        return giftView;
    }
}
