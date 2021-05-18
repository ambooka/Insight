package com.msah.insight.styles.toolitems.styles;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.ItalicSpan;
import com.msah.insight.styles.ABS_Style;
import com.msah.insight.styles.toolitems.IToolItem_Updater;
import com.msah.insight.views.CustomEditText;

public class Style_Italic extends ABS_Style<ItalicSpan> {

    private ImageView mItalicImageView;

    private boolean mItalicChecked;

    private CustomEditText mEditText;

    private IToolItem_Updater mCheckUpdater;

    /**
     *
     * @param italicImage
     */
    public Style_Italic(CustomEditText editText, ImageView italicImage, IToolItem_Updater checkUpdater) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mItalicImageView = italicImage;
        this.mCheckUpdater = checkUpdater;
        setListenerForImageView(this.mItalicImageView);
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
                mItalicChecked = !mItalicChecked;
                if (null != mCheckUpdater) {
                    mCheckUpdater.onCheckStatusUpdate(mItalicChecked);
                }
                if (null != mEditText) {
                    applyStyle(mEditText.getEditableText(), mEditText.getSelectionStart(), mEditText.getSelectionEnd());
                }
            }
        });
    }

    @Override
    public ImageView getImageView() {
        return this.mItalicImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mItalicChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mItalicChecked;
    }

    @Override
    public ItalicSpan newSpan() {
        return new ItalicSpan();
    }
}
