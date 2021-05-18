package com.msah.insight.styles.toolitems.styles;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.BoldSpan;
import com.msah.insight.styles.ABS_Style;
import com.msah.insight.styles.toolitems.IToolItem_Updater;
import com.msah.insight.views.CustomEditText;

public class Style_Bold extends ABS_Style<BoldSpan> {

    private ImageView mBoldImageView;

    private boolean mBoldChecked;

    private CustomEditText mEditText;

    private IToolItem_Updater mCheckUpdater;

    /**
     * @param boldImage
     */
    public Style_Bold(CustomEditText editText, ImageView boldImage, IToolItem_Updater checkUpdater) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mBoldImageView = boldImage;
        this.mCheckUpdater = checkUpdater;
        setListenerForImageView(this.mBoldImageView);
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
                mBoldChecked = !mBoldChecked;
                if (mCheckUpdater != null) {
                    mCheckUpdater.onCheckStatusUpdate(mBoldChecked);
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

