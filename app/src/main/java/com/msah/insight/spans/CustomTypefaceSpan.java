package com.msah.insight.spans;


import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class CustomTypefaceSpan extends MetricAffectingSpan {

    private static Typeface sTypeface;

    /**
     * @paramfamily The font family for this typeface.  Examples include:
     * ???
     */
    public CustomTypefaceSpan(Typeface typeface) {
        sTypeface = typeface;
    }

    public int getSpanTypeId() {
        return 13;
    }

    public int describeContents() {
        return 0;
    }


    private static void apply(Paint paint) {
        if (null != sTypeface) {
            paint.setTypeface(sTypeface);
        }
    }

    @Override
    public void updateMeasureState(TextPaint p) {
        apply(p);
    }

    @Override
    public void updateDrawState(TextPaint tp) {
        apply(tp);
    }
}
