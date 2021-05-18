package com.msah.insight.styles.toolitems;

import android.content.Context;
import android.text.Editable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.msah.insight.R;
import com.msah.insight.colorpicker.ColorPickerView;
import com.msah.insight.spans.CustomForegroundColorSpan;
import com.msah.insight.styles.IStyle;
import com.msah.insight.styles.toolitems.styles.Style_FontColor;
import com.msah.insight.utils.Util;
import com.msah.insight.views.CustomEditText;

public class ToolItem_FontColor extends ToolItem_Abstract {

    private final ColorPickerView colorPickerView;

    public ToolItem_FontColor() {
        this.colorPickerView = null;
    }

    public ToolItem_FontColor(ColorPickerView colorPickerView) {
        this.colorPickerView = colorPickerView;
    }

    @Override
    public IStyle getStyle() {
        if (mStyle == null) {
            CustomEditText editText = this.getEditText();
            mStyle = new Style_FontColor(editText, (ImageView) mToolItemView, colorPickerView);
        }
        return mStyle;
    }

    @Override
    public View getView(Context context) {
        if (null == context) {
            return mToolItemView;
        }
        if (mToolItemView == null) {
            ImageView imageView = new ImageView(context);
            int size = Util.getPixelByDp(context, 35);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.foregroundcolor);
            imageView.bringToFront();
            mToolItemView = imageView;
        }

        return mToolItemView;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        int spanColor = -1;

        CustomEditText editText = this.getEditText();
        Editable editable = editText.getEditableText();
        if (selStart > 0 && selStart == selEnd) {
            CustomForegroundColorSpan[] styleSpans = editable.getSpans(selStart - 1, selStart, CustomForegroundColorSpan.class);

            if (styleSpans.length > 0) {
                spanColor = ((CustomForegroundColorSpan) styleSpans[styleSpans.length - 1]).getForegroundColor();
            }
            ((Style_FontColor) mStyle).setColorChecked(spanColor);
        } else {
            //≤
            // Selection is a range
            CustomForegroundColorSpan[] styleSpans = editable.getSpans(selStart, selEnd, CustomForegroundColorSpan.class);

            boolean multipleColor = false;
            for (int i = 0; i < styleSpans.length; i++) {

                int thisSpanColor = styleSpans[i].getForegroundColor();
                if (spanColor == -1) {
                    spanColor = thisSpanColor;
                } else {
                    if (spanColor != thisSpanColor) {
                        multipleColor = true;
                        break;
                    }
                }
            }
            if (!multipleColor) {
                ((Style_FontColor) mStyle).setColorChecked(spanColor);
                return;
            }
        }
    }

    @Override
    public IToolItem_Updater getToolItemUpdater() {
        return null;
    }
}
