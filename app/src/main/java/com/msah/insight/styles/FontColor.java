package com.msah.insight.styles;

import android.text.Editable;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.colorpicker.ColorPickerListener;
import com.msah.insight.spans.CustomForegroundColorSpan;
import com.msah.insight.styles.toolbar.CustomToolbar;
import com.msah.insight.utils.Util;
import com.msah.insight.views.CustomEditText;

public class FontColor extends ABS_Dynamic_Style<CustomForegroundColorSpan> {

    private ImageView mFontColorImageView;

    private CustomToolbar mToolbar;

    private CustomEditText mEditText;

    private int mColor = -1;

    private ColorPickerListener mColorPickerListener = new ColorPickerListener() {
        @Override
        public void onPickColor(int color) {
            mColor = color;
            if (null != mEditText) {
                Editable editable = mEditText.getEditableText();
                int start = mEditText.getSelectionStart();
                int end = mEditText.getSelectionEnd();

                if (end > start) {
                    applyNewStyle(editable, start, end, mColor);
                }
            }
        }
    };


    public FontColor(ImageView fontColorImage, CustomToolbar toolbar) {
        super(fontColorImage.getContext());
        this.mFontColorImageView = fontColorImage;
        this.mToolbar = toolbar;
        setListenerForImageView(this.mFontColorImageView);
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
                mToolbar.toggleColorPalette(mColorPickerListener);
            }
        });
    }

    @Override
    protected void changeSpanInsideStyle(Editable editable, int start, int end, CustomForegroundColorSpan existingSpan) {
        int currentColor = existingSpan.getForegroundColor();
        if (currentColor != mColor) {
            Util.log("color changed before: " + currentColor + ", new == " + mColor);
            applyNewStyle(editable, start, end, mColor);
            logAllFontColorSpans(editable);
        }
    }

    private void logAllFontColorSpans(Editable editable) {
        ForegroundColorSpan[] listItemSpans = editable.getSpans(0,
                editable.length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan span : listItemSpans) {
            int ss = editable.getSpanStart(span);
            int se = editable.getSpanEnd(span);
            Util.log("List All: " + " :: start == " + ss + ", end == " + se);
        }
    }

    @Override
    public CustomForegroundColorSpan newSpan() {
        return new CustomForegroundColorSpan(this.mColor);
    }

    @Override
    public ImageView getImageView() {
        return this.mFontColorImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        // Do nothing
    }

    @Override
    public boolean getIsChecked() {
        return this.mColor != -1;
    }

    @Override
    public EditText getEditText() {
        return this.mEditText;
    }

    @Override
    protected CustomForegroundColorSpan newSpan(int color) {
        return new CustomForegroundColorSpan(color);
    }

    @Override
    protected void featureChangedHook(int lastSpanColor) {
        mColor = lastSpanColor;
        mToolbar.setColorPaletteColor(mColor);
    }
}

