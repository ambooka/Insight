package com.msah.insight.styles;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.CustomUnderlineSpan;
import com.msah.insight.views.CustomEditText;

public class Underline extends ABS_Style<CustomUnderlineSpan> {

    private ImageView mUnderlineImageView;

    private boolean mUnderlineChecked;

    private CustomEditText mEditText;

    public Underline(ImageView imageView) {
        super(imageView.getContext());
        this.mUnderlineImageView = imageView;
        setListenerForImageView(this.mUnderlineImageView);
    }

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
                mUnderlineChecked = !mUnderlineChecked;
                StyleHelper.updateCheckStatus(Underline.this, mUnderlineChecked);
                if (null != mEditText) {
                    applyStyle(mEditText.getEditableText(), mEditText.getSelectionStart(), mEditText.getSelectionEnd());
                }
            }
        });
    }

    @Override
    public ImageView getImageView() {
        return this.mUnderlineImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mUnderlineChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mUnderlineChecked;
    }

    @Override
    public CustomUnderlineSpan newSpan() {
        return new CustomUnderlineSpan();
    }
}
