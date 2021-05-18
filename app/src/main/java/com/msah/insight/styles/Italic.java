package com.msah.insight.styles;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.ItalicSpan;
import com.msah.insight.views.CustomEditText;

public class Italic extends ABS_Style<ItalicSpan> {

    private ImageView mItalicImageView;

    private boolean mItalicChecked;

    private CustomEditText mEditText;

    public Italic(ImageView italicImage) {
        super(italicImage.getContext());
        this.mItalicImageView = italicImage;
        setListenerForImageView(this.mItalicImageView);
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
                mItalicChecked = !mItalicChecked;
                StyleHelper.updateCheckStatus(Italic.this, mItalicChecked);
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
