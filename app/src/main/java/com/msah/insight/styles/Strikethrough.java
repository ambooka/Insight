package com.msah.insight.styles;


import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.views.CustomEditText;

public class Strikethrough extends ABS_Style<StrikethroughSpan> {

    private ImageView mStrikethroughImageView;

    private boolean mStrikethroughChecked;

    private CustomEditText mEditText;

    public Strikethrough(ImageView strikethroughImage) {
        super(strikethroughImage.getContext());
        this.mStrikethroughImageView = strikethroughImage;
        setListenerForImageView(this.mStrikethroughImageView);
    }

    /**
     *
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
                mStrikethroughChecked = !mStrikethroughChecked;

                StyleHelper.updateCheckStatus(Strikethrough.this, mStrikethroughChecked);
                if (null != mEditText) {
                    applyStyle(mEditText.getEditableText(), mEditText.getSelectionStart(), mEditText.getSelectionEnd());
                }
            }
        });
    }

    @Override
    public ImageView getImageView() {
        return this.mStrikethroughImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mStrikethroughChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mStrikethroughChecked;
    }

    @Override
    public StrikethroughSpan newSpan() {
        return new StrikethroughSpan();
    }
}

