package com.msah.insight.styles.toolitems.styles;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.CustomUnderlineSpan;
import com.msah.insight.styles.ABS_Style;
import com.msah.insight.styles.toolitems.IToolItem_Updater;
import com.msah.insight.views.CustomEditText;

public class Style_Underline extends ABS_Style<CustomUnderlineSpan> {

    private ImageView mUnderlineImageView;

    private boolean mUnderlineChecked;

    private CustomEditText mEditText;

    private IToolItem_Updater mCheckUpdater;

    /**
     *
     * @param italicImage
     */
    public Style_Underline(CustomEditText editText, ImageView italicImage, IToolItem_Updater checkUpdater) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mUnderlineImageView = italicImage;
        this.mCheckUpdater = checkUpdater;
        setListenerForImageView(this.mUnderlineImageView);
    }

    @Override
    public EditText getEditText() {
        return this.mEditText;
    }

    /**
     *
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
                mUnderlineChecked = !mUnderlineChecked;
                if (null != mCheckUpdater) {
                    mCheckUpdater.onCheckStatusUpdate(mUnderlineChecked);
                }
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
    public
    CustomUnderlineSpan newSpan() {
        return new CustomUnderlineSpan();
    }
}
