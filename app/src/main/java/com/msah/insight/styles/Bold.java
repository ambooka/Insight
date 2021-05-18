package com.msah.insight.styles;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.BoldSpan;
import com.msah.insight.views.CustomEditText;

public class Bold extends ABS_Style<BoldSpan> {

    private ImageView mBoldImageView;

    private boolean mBoldChecked;

    private CustomEditText mEditText;

    /**
     * @param boldImage
     */
    public Bold(ImageView boldImage) {
        super(boldImage.getContext());
        this.mBoldImageView = boldImage;
        setListenerForImageView(this.mBoldImageView);
    }

    /**
     * @param editText
     */
    public void setEditText(CustomEditText editText) {
        this.mEditText = editText;
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }

    @Override
    public void setListenerForImageView(final ImageView imageView) {
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoldChecked = !mBoldChecked;
                StyleHelper.updateCheckStatus(Bold.this, mBoldChecked);
                if (null != mEditText) {
                    applyStyle(mEditText.getEditableText(),
                            mEditText.getSelectionStart(),
                            mEditText.getSelectionEnd());
                }
            }
        });
    }

    @Override
    public ImageView getImageView() {
        return this.mBoldImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mBoldChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mBoldChecked;
    }

    @Override
    public BoldSpan newSpan() {
        return new BoldSpan();
    }
}
