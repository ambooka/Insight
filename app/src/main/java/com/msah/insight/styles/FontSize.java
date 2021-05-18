package com.msah.insight.styles;

import android.text.Editable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.utils.Constants;
import com.msah.insight.spans.FontSizeSpan;
import com.msah.insight.styles.toolbar.CustomToolbar;
import com.msah.insight.styles.windows.FontSizeChangeListener;
import com.msah.insight.styles.windows.FontsizePickerWindow;
import com.msah.insight.utils.Util;
import com.msah.insight.views.CustomEditText;

public class FontSize extends ABS_Dynamic_Style<FontSizeSpan> implements FontSizeChangeListener {

//	public static boolean S_CHECKED = true;

    private ImageView mFontsizeImageView;

    private CustomToolbar mToolbar;

    private CustomEditText mEditText;

    private int mSize = Constants.DEFAULT_FONT_SIZE;

    private FontsizePickerWindow mFontPickerWindow;

    private static final int DEFAULT_FONT_SIZE = 18;

    private boolean mIsChecked;

    /**
     * @param fontSizeImage
     */
    public FontSize(ImageView fontSizeImage, CustomToolbar toolbar) {
        super(fontSizeImage.getContext());
        this.mToolbar = toolbar;
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
        imageView.setOnClickListener(new OnClickListener() {
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
        int yOff = 0 - Util.getPixelByDp(mContext, 150);
        mFontPickerWindow.showAsDropDown(mFontsizeImageView,0, yOff);
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
