package com.msah.insight.spans;

import android.text.style.AbsoluteSizeSpan;

public class FontSizeSpan extends AbsoluteSizeSpan implements DynamicSpan {

    public FontSizeSpan(int size) {
        super(size, true);
    }

    @Override
    public int getDynamicFeature() {
        return this.getSize();
    }
}