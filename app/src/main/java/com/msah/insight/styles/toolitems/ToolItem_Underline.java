package com.msah.insight.styles.toolitems;

import android.content.Context;
import android.text.Editable;
import android.text.style.CharacterStyle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.msah.insight.utils.Constants;
import com.msah.insight.R;
import com.msah.insight.spans.CustomUnderlineSpan;
import com.msah.insight.styles.IStyle;
import com.msah.insight.styles.toolitems.styles.Style_Underline;
import com.msah.insight.utils.Util;
import com.msah.insight.views.CustomEditText;


public class ToolItem_Underline extends ToolItem_Abstract {

    @Override
    public IToolItem_Updater getToolItemUpdater() {
        if (mToolItemUpdater == null) {
            mToolItemUpdater = new ToolItem_UpdaterDefault(this, Constants.CHECKED_COLOR, Constants.UNCHECKED_COLOR);
            setToolItemUpdater(mToolItemUpdater);
        }
        return mToolItemUpdater;
    }

    @Override
    public IStyle getStyle() {
        if (mStyle == null) {
            CustomEditText editText = this.getEditText();
            IToolItem_Updater toolItemUpdater = getToolItemUpdater();
            mStyle = new Style_Underline(editText, (ImageView) mToolItemView, toolItemUpdater);
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
            int size = Util.getPixelByDp(context, 40);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.underline);
            imageView.bringToFront();
            mToolItemView = imageView;
        }

        return mToolItemView;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        boolean underlinedExists = false;

        CustomEditText editText = this.getEditText();
        Editable editable = editText.getEditableText();
        if (selStart > 0 && selStart == selEnd) {
            CharacterStyle[] styleSpans = editable.getSpans(selStart - 1, selStart, CharacterStyle.class);

            for (int i = 0; i < styleSpans.length; i++) {
                if (styleSpans[i] instanceof CustomUnderlineSpan) {
                    underlinedExists = true;
                }
            }
        } else {
            //
            // Selection is a range
            CharacterStyle[] styleSpans = editable.getSpans(selStart, selEnd, CharacterStyle.class);

            for (int i = 0; i < styleSpans.length; i++) {

                if (styleSpans[i] instanceof CustomUnderlineSpan) {
                    if (editable.getSpanStart(styleSpans[i]) <= selStart
                            && editable.getSpanEnd(styleSpans[i]) >= selEnd) {
                        underlinedExists = true;
                    }
                }
            }
        }

        mToolItemUpdater.onCheckStatusUpdate(underlinedExists);
    }
}

