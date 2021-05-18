package com.msah.insight.styles.toolitems.styles;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.activities.AtPickerActivity;
import com.msah.insight.models.AtItem;
import com.msah.insight.spans.AtSpan;
import com.msah.insight.strategies.AtStrategy;
import com.msah.insight.styles.ABS_FreeStyle;
import com.msah.insight.views.CustomEditText;

public class Style_At extends ABS_FreeStyle {

    public static final int REQUEST_CODE = 1004;

    public static final String EXTRA_TAG = "atItem";

    private static final String AT = "@";

    private static int AT_INSERT_POS = -1;

    private CustomEditText mEditText;

    public Style_At(CustomEditText editText) {
        super(editText.getContext());
        this.mEditText = editText;
    }

    @Override
    public void setListenerForImageView(ImageView imageView) {
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
        if (end > start) {
            String typeString = editable.toString().substring(start, end);
            if (typeString.equals(AT)) {
                // Open contacts list
                openAtPicker();
                AT_INSERT_POS = end;
            }
        }
    }

    private void openAtPicker() {
        AtStrategy atStrategy = mEditText.getAtStrategy();
        if (atStrategy != null) {
            atStrategy.openAtPage();
            return;
        }
        Intent intent = new Intent(this.mContext, AtPickerActivity.class);
        ((Activity) this.mContext).startActivityForResult(intent, REQUEST_CODE);
    }

    public void insertAt(AtItem atItem) {
        AtStrategy atStrategy = mEditText.getAtStrategy();
        boolean consumed = false;
        if (atStrategy != null) {
            consumed = atStrategy.onItemSelected(atItem);
        }
        if (consumed) {
            return;
        }
        if (null == this.mEditText) {
            return;
        }
        if (atItem.mColor == Color.BLUE) {
            if (atItem.mName.startsWith("Steve")) { // For demo purpose
                atItem.mColor = Color.MAGENTA;
            }
        }
        AtSpan atSpan = new AtSpan(atItem);
        this.mEditText.getEditableText().insert(AT_INSERT_POS, atItem.mName);
        this.mEditText.getEditableText().setSpan(atSpan, AT_INSERT_POS - 1, AT_INSERT_POS + atItem.mName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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

    @Override
    public EditText getEditText() {
        return this.mEditText;
    }
}

