package com.msah.insight.styles;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spanned;
import android.widget.ImageView;

import com.msah.insight.activities.AtPickerActivity;
import com.msah.insight.models.AtItem;
import com.msah.insight.spans.AtSpan;
import com.msah.insight.strategies.AtStrategy;
import com.msah.insight.styles.toolbar.CustomToolbar;
import com.msah.insight.views.CustomEditText;

public class At extends ABS_FreeStyle {

    public static final String EXTRA_TAG = "atItem";

    private static final String AT = "@";

    private static int AT_INSERT_POS = -1;

    private CustomEditText mEditText;

    public At(CustomToolbar toolbar) {
        super(toolbar);
    }

    public void setEditText(CustomEditText editText) {
        this.mEditText = editText;
    }

//	public ARE_At(ImageView atImageView) {
//		setListenerForImageView(atImageView);
//	}

    @Override
    public void setListenerForImageView(ImageView imageView) {
//		imageView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				EditText editText = getEditText();
//				int selectionStart = editText.getSelectionStart();
//				int selectionEnd = editText.getSelectionEnd();
//
//				Editable editable = editText.getText();
//				SpannableStringBuilder ssb = new SpannableStringBuilder();
//
//				// The name of the people to @
//				String at = "@Wenbin Liu";
//				ssb.append(at);
//				AtSpan atSpan = new AtSpan("12345_1", Color.YELLOW);
//				ssb.setSpan(atSpan, 0, ssb.length(),
//						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//				// ssb.append(" "); // Append a blank after the at span.
//
//				editable.replace(selectionStart, selectionEnd, ssb);
//			}
//		});
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
        ((Activity) this.mContext).startActivityForResult(intent, CustomToolbar.REQ_AT);
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

}

