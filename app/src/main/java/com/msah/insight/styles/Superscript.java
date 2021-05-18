package com.msah.insight.styles;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.CustomSuperscriptSpan;
import com.msah.insight.views.CustomEditText;

public class Superscript extends ABS_Style<CustomSuperscriptSpan> {

    private ImageView mSuperscriptImage;

    private boolean mSuperscriptChecked;

    private CustomEditText mEditText;

    /**
     * @param imageView
     */
    public Superscript(ImageView imageView) {
        super(imageView.getContext());
        this.mSuperscriptImage = imageView;
        setListenerForImageView(this.mSuperscriptImage);
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
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSuperscriptChecked = !mSuperscriptChecked;
                StyleHelper.updateCheckStatus(Superscript.this, mSuperscriptChecked);
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
        return mSuperscriptImage;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mSuperscriptChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mSuperscriptChecked;
    }

    @Override
    public CustomSuperscriptSpan newSpan() {
        return new CustomSuperscriptSpan();
    }
}

