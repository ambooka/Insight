package com.msah.insight.styles.toolitems.styles;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.CustomSuperscriptSpan;
import com.msah.insight.styles.ABS_Style;
import com.msah.insight.styles.toolitems.IToolItem_Updater;
import com.msah.insight.views.CustomEditText;


public class Style_Superscript extends ABS_Style<CustomSuperscriptSpan> {

    private ImageView mSuperscriptImage;

    private boolean mSuperscriptChecked;

    private
    CustomEditText mEditText;

    private IToolItem_Updater mCheckUpdater;

    /**
     * @param imageView
     */
    public Style_Superscript(CustomEditText editText, ImageView imageView, IToolItem_Updater checkUpdater) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mSuperscriptImage = imageView;
        this.mCheckUpdater = checkUpdater;
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
                if (mCheckUpdater != null) {
                    mCheckUpdater.onCheckStatusUpdate(mSuperscriptChecked);
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
