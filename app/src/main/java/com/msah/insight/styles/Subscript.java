package com.msah.insight.styles;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.CustomSubscriptSpan;
import com.msah.insight.views.CustomEditText;


public class Subscript extends ABS_Style<CustomSubscriptSpan> {

    private ImageView mSubscriptImage;

    private boolean mSubscriptChecked;

    private CustomEditText mEditText;

    /**
     * @param imageView image view
     */
    public Subscript(ImageView imageView) {
        super(imageView.getContext());
        this.mSubscriptImage = imageView;
        setListenerForImageView(this.mSubscriptImage);
    }

    /**
     * @param editText edit text
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
                mSubscriptChecked = !mSubscriptChecked;
                StyleHelper.updateCheckStatus(Subscript.this, mSubscriptChecked);
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
        return mSubscriptImage;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mSubscriptChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mSubscriptChecked;
    }

    @Override
    public CustomSubscriptSpan newSpan() {
        return new CustomSubscriptSpan();
    }
}

