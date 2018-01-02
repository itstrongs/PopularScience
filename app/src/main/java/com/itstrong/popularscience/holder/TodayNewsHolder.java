package com.itstrong.popularscience.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.itstrong.popularscience.R;

/**
 * Created by itstrong on 2016/6/19.
 */
public class TodayNewsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView imgIcon;
    public TextView textTitle;
    public TextView textContent;
    public Button btnDetail;

    public TodayNewsHolder(View view) {
        super(view);
        imgIcon = (ImageView)view.findViewById(R.id.img_head_line_icon);
        textTitle = (TextView)view.findViewById(R.id.text_head_line_title);
//        textContent = (TextView)view.findViewById(R.id.text_head_line_content);
        btnDetail = (Button)view.findViewById(R.id.btn_head_line_detail);
        btnDetail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
