package com.utouu.livevideo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.utouu.livevideo.R;

/**
 * Create by 黄思程 on 2017/2/16   14:26
 * Function：
 * Desc：直播交流页面
 */
public class CommentFragment extends Fragment{

    private TextView mCommentText;
    private RecyclerView mRView;

    public CommentFragment() {
    }

    public static CommentFragment newInstance(){
        CommentFragment fragment = new CommentFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View commentView = inflater.inflate(R.layout.fragment_comment, container, false);
        mCommentText = ((TextView) commentView.findViewById(R.id.tv_comment));
        mRView = ((RecyclerView) commentView.findViewById(R.id.rv_comment));

        return commentView;
    }
}
