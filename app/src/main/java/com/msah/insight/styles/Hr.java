package com.msah.insight.styles;


import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.ImageView;

import com.msah.insight.utils.Constants;
import com.msah.insight.spans.HrSpan;
import com.msah.insight.styles.toolbar.CustomToolbar;
import com.msah.insight.views.CustomEditText;

public class Hr extends ABS_FreeStyle {

    private CustomEditText mEditText;

    public Hr(ImageView imageView, CustomToolbar toolbar) {
        super(toolbar);
        setListenerForImageView(imageView);
    }

    /**
     * @param editText
     */
    public void setEditText(CustomEditText editText) {
        this.mEditText = editText;
    }

    @Override
    public void setListenerForImageView(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editable = mEditText.getEditableText();
                int start = mEditText.getSelectionStart();
                int end = mEditText.getSelectionEnd();

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(Constants.CHAR_NEW_LINE);
                ssb.append(Constants.CHAR_NEW_LINE);
                ssb.append(Constants.ZERO_WIDTH_SPACE_STR);
                ssb.append(Constants.CHAR_NEW_LINE);
                ssb.setSpan(new HrSpan(), 2, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                editable.replace(start, end, ssb);
            }
        });
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
        // Do nothing
    }


    @Override
    public ImageView getImageView() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setChecked(boolean isChecked) {
        // TODO Auto-generated method stub

    }
}

