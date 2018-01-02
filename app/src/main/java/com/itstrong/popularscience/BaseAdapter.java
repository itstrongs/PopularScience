package com.itstrong.popularscience;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by itstrong on 2016/6/18.
 */
public class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private int mCount;
    private BaseAdapterCallback mAdapterCallback;
    private OnItemClickListener mOnItemClickListener;

    public BaseAdapter(int count, BaseAdapterCallback adapterCallback) {
        this.mCount = count;
        this.mAdapterCallback = adapterCallback;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return mAdapterCallback.onCreateViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        mAdapterCallback.onBindViewHolder(holder, position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, holder.getLayoutPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCount;
    }

    public void setOnItemClickLitener(OnItemClickListener mOnItemClickLitener) {
        this.mOnItemClickListener = mOnItemClickLitener;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public interface BaseAdapterCallback {
        RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);
        void onBindViewHolder(RecyclerView.ViewHolder holder, int position);
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
