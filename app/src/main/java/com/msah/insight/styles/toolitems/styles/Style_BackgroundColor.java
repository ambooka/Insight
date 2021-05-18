package com.msah.insight.styles.toolitems.styles;


import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.styles.ABS_Style;
import com.msah.insight.styles.toolitems.IToolItem_Updater;
import com.msah.insight.views.CustomEditText;

public class Style_BackgroundColor extends ABS_Style<BackgroundColorSpan> {

    private ImageView mBackgroundImageView;

    private boolean mBackgroundChecked;

    private CustomEditText mEditText;

    private IToolItem_Updater mCheckUpdater;

    private int mColor;

    /**
     * @param backgroundImage
     */
    public Style_BackgroundColor(CustomEditText editText, ImageView backgroundImage, IToolItem_Updater checkUpdater, int backgroundColor) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mBackgroundImageView = backgroundImage;
        this.mCheckUpdater = checkUpdater;
        this.mColor = backgroundColor;
        setListenerForImageView(this.mBackgroundImageView);
    }

    @Override
    public EditText getEditText() {
        return this.mEditText;
    }

    /**
     * @param editText
     */
    public void setEditText(CustomEditText editText) {
        this.mEditText = editText;
    }

    @Override
    public void setListenerForImageView(final ImageView imageView) {
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackgroundChecked = !mBackgroundChecked;
                if (mCheckUpdater != null) {
                    mCheckUpdater.onCheckStatusUpdate(mBackgroundChecked);
                }
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
        return this.mBackgroundImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mBackgroundChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mBackgroundChecked;
    }

    @Override
    public BackgroundColorSpan newSpan() {
        return new BackgroundColorSpan(mColor);
    }
}


