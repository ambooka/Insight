package com.msah.insight.spans;

import android.text.style.ForegroundColorSpan;

import androidx.annotation.ColorInt;

public class CustomForegroundColorSpan extends ForegroundColorSpan implements DynamicSpan {
    public CustomForegroundColorSpan(@ColorInt int color) {
        super(color);
    }

    @Override
    public int getDynamicFeature() {
        return this.getForegroundColor();
    }
}

