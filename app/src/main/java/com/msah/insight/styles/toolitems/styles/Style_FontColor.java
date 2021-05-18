package com.msah.insight.styles.toolitems.styles;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.colorpicker.ColorPickerListener;
import com.msah.insight.colorpicker.ColorPickerView;
import com.msah.insight.spans.CustomForegroundColorSpan;
import com.msah.insight.styles.ABS_Dynamic_Style;
import com.msah.insight.styles.windows.ColorPickerWindow;
import com.msah.insight.utils.Util;
import com.msah.insight.views.CustomEditText;

public class Style_FontColor extends ABS_Dynamic_Style<CustomForegroundColorSpan> implements ColorPickerListener {

    private ImageView mFontColorImageView;

    private final ColorPickerView colorPickerView;

    private CustomEditText mEditText;

    private ColorPickerWindow mColorPickerWindow;

    private int mColor;

    private boolean mIsChecked;

    /**
     * @param fontColorImage
     * @param colorPickerView
     */
    public Style_FontColor(CustomEditText editText, ImageView fontColorImage, ColorPickerView colorPickerView) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mFontColorImageView = fontColorImage;
        this.colorPickerView = colorPickerView;
        setListenerForImageView(this.mFontColorImageView);
        if (this.colorPickerView != null) colorPickerView.setColorPickerListener(this);
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
                if (colorPickerView != null) {
                    toggleColorPickerView();
                } else {
                    showFontColorPickerWindow();
                }
            }
        });
    }

    private void toggleColorPickerView() {
        boolean isVisible = colorPickerView.getVisibility() == View.VISIBLE;
        colorPickerView.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    private void showFontColorPickerWindow() {
        if (mColorPickerWindow == null) {
            mColorPickerWindow = new ColorPickerWindow(mContext, this);
        }
        int yOff = Util.getPixelByDp(mContext, -5);
        mColorPickerWindow.showAsDropDown(mFontColorImageView, 0, yOff);
    }

    @Override
    public CustomForegroundColorSpan newSpan() {
        return new CustomForegroundColorSpan(mColor);
    }

    @Override
    public ImageView getImageView() {
        return this.mFontColorImageView;
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
    protected void featureChangedHook(int lastSpanFontColor) {
        mColor = lastSpanFontColor;
        setColorChecked(lastSpanFontColor);
    }

    public void setColorChecked(int color) {
        if (colorPickerView != null) {
            colorPickerView.setColor(color);
        } else if (mColorPickerWindow != null) {
            mColorPickerWindow.setColor(color);
        }
    }

    @Override
    protected CustomForegroundColorSpan newSpan(int color) {
        return new CustomForegroundColorSpan(color);
    }

    @Override
    public void onPickColor(int color) {
        mIsChecked = true;

        mFontColorImageView.post(new Runnable() {
            @Override
            public void run() {
                mFontColorImageView.setColorFilter(mColor);
            }
        });

        mColor = color;
        if (null != mEditText) {
            Editable editable = mEditText.getEditableText();
            int start = mEditText.getSelectionStart();
            int end = mEditText.getSelectionEnd();

            if (end >= start) {
                applyNewStyle(editable, start, end, color);
            }
        }
    }
}

