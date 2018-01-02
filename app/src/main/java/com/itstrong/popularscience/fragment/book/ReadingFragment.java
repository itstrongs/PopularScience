package com.itstrong.popularscience.fragment.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.itstrong.popularscience.R;
import com.itstrong.popularscience.fragment.BaseFragment;
import com.itstrong.popularscience.utils.LogUtils;
import com.itstrong.popularscience.utils.ToastUtils;
import com.joanzapata.pdfview.PDFView;

import java.io.File;

/**
 * 阅读页面
 * Created by itstrong on 2016/6/19.
 */
public class ReadingFragment extends BaseFragment implements View.OnClickListener {

    private PDFView mPDFView;
    private EditText mEditPage;

    public static ReadingFragment newInstance(String bookPath) {
        ReadingFragment newFragment = new ReadingFragment();
        Bundle bundle = new Bundle();
        bundle.putString("bookPath", bookPath);
        newFragment.setArguments(bundle);
        return newFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_reading, container, false);
        return mContentView;
    }

    @Override
    public void findViewById() {
        mPDFView = (PDFView)mContentView.findViewById(R.id.pdf_view);
        mEditPage = (EditText)mContentView.findViewById(R.id.edit_reading_page);
        mContentView.findViewById(R.id.btn_reading_switch).setOnClickListener(this);
    }

    @Override
    public void setListener() { }

    @Override
    public void processLogic() {
        LogUtils.d("getArguments().getString(\"bookPath\")" + getArguments().getString("bookPath"));
        try {
            mPDFView.fromFile(new File(getArguments().getString("bookPath")))
                    .defaultPage(1)
                    .showMinimap(false)
                    .enableSwipe(true)
                    .load();
        } catch (Exception e) {
            ToastUtils.showToast(mActivity, "文件格式错误，无法打开");
        }
    }

    @Override
    public void onClick(View v) {
        mPDFView.jumpTo(Integer.parseInt(mEditPage.getText().toString()));
    }
}
