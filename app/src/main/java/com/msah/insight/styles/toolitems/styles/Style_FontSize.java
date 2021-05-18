package com.msah.insight.styles.toolitems.styles;


import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.utils.Constants;
import com.msah.insight.spans.FontSizeSpan;
import com.msah.insight.styles.ABS_Dynamic_Style;
import com.msah.insight.styles.windows.FontSizeChangeListener;
import com.msah.insight.styles.windows.FontsizePickerWindow;
import com.msah.insight.views.CustomEditText;

public class Style_FontSize extends ABS_Dynamic_Style<FontSizeSpan> implements FontSizeChangeListener {

    private ImageView mFontsizeImageView;

    private CustomEditText mEditText;

    private int mSize = Constants.DEFAULT_FONT_SIZE;

    private FontsizePickerWindow mFontPickerWindow;

    private static final int DEFAULT_FONT_SIZE = 18;

    private boolean mIsChecked;

    /**
     * @param fontSizeImage
     */
    public Style_FontSize(CustomEditText editText, ImageView fontSizeImage) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mFontsizeImageView = fontSizeImage;
        setListenerForImageView(this.mFontsizeImageView);
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
                showFontsizePickerWindow();
            }
        });
    }

    private void showFontsizePickerWindow() {
        if (mFontPickerWindow == null) {
            mFontPickerWindow = new FontsizePickerWindow(mContext, this);
        }
        mFontPickerWindow.setFontSize(mSize);
        mFontPickerWindow.showAsDropDown(mFontsizeImageView,0, 0);
    }

    @Override
    protected void changeSpanInsideStyle(Editable editable, int start, int end, FontSizeSpan existingSpan) {
        int currentSize = existingSpan.getSize();
        if (currentSize != mSize) {
            applyNewStyle(editable, start, end, mSize);
        }
    }

    @Override
    public FontSizeSpan newSpan() {
        return new FontSizeSpan(mSize);
    }

    @Override
    public ImageView getImageView() {
        return this.mFontsizeImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        // Do nothing.
    }

    @Override
    public boolean getIsChecked() {
        return mIsChecked;
    }

    @Override
    public void onFontSizeChange(int fontSize) {
        mIsChecked = true;
        mSize = fontSize;
        if (null != mEditText) {
            Editable editable = mEditText.getEditableText();
            int start = mEditText.getSelectionStart();
            int end = mEditText.getSelectionEnd();

            if (end > start) {
                applyNewStyle(editable, start, end, mSize);
            }
        }
    }

    @Override
    protected void featureChangedHook(int lastSpanFontSize) {
        mSize = lastSpanFontSize;
        if (mFontPickerWindow != null) {
            mFontPickerWindow.setFontSize(mSize);
        }
    }

    @Override
    protected FontSizeSpan newSpan(int size) {
        return new FontSizeSpan(size);
    }
}

