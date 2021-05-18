package com.msah.insight.styles.toolitems.styles;

import android.text.style.StrikethroughSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.styles.ABS_Style;
import com.msah.insight.styles.toolitems.IToolItem_Updater;
import com.msah.insight.views.CustomEditText;

public class Style_Strikethrough extends ABS_Style<StrikethroughSpan> {

    private ImageView mStrikethroughImageView;

    private boolean mStrikethroughChecked;

    private CustomEditText mEditText;

    private IToolItem_Updater mCheckUpdater;

    /**
     *
     * @param italicImage
     */
    public Style_Strikethrough(CustomEditText editText, ImageView italicImage, IToolItem_Updater checkUpdater) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mStrikethroughImageView = italicImage;
        this.mCheckUpdater = checkUpdater;
        setListenerForImageView(this.mStrikethroughImageView);
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
                mStrikethroughChecked = !mStrikethroughChecked;
                if (null != mCheckUpdater) {
                    mCheckUpdater.onCheckStatusUpdate(mStrikethroughChecked);
                }
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

