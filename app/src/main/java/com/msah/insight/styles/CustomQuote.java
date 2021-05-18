package com.msah.insight.styles;

import android.text.Editable;
import android.text.Spannable;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.utils.Constants;
import com.msah.insight.spans.CustomQuoteSpan;
import com.msah.insight.utils.Util;
import com.msah.insight.views.CustomEditText;

public class CustomQuote implements IStyle {

    private ImageView mQuoteImageView;

    private boolean mQuoteChecked;

    private CustomEditText mEditText;

    private boolean mRemovedNewLine;


    /**
     * @param quoteImageView
     */
    public CustomQuote(ImageView quoteImageView) {
        this.mQuoteImageView = quoteImageView;
        setListenerForImageView(this.mQuoteImageView);
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
                mQuoteChecked = !mQuoteChecked;
                StyleHelper.updateCheckStatus(CustomQuote.this, mQuoteChecked);
                if (null != mEditText) {
                    if (mQuoteChecked) {
                        makeLineAsQuote();
                    } else {
                        removeQuote();
                    }
                }
            }
        });
    }

    /**
     * @return
     */
    private void makeLineAsQuote() {
        EditText editText = getEditText();
        int currentLine = Util.getCurrentCursorLine(editText);
        int start = Util.getThisLineStart(editText, currentLine);
        int end = Util.getThisLineEnd(editText, currentLine);
        Editable editable = editText.getText();
        editable.insert(start, Constants.ZERO_WIDTH_SPACE_STR);
        start = Util.getThisLineStart(editText, currentLine);
        end = Util.getThisLineEnd(editText, currentLine);

        if (editable.charAt(end - 1) == Constants.CHAR_NEW_LINE) {
            end--;
        }

        CustomQuoteSpan[] existingQuoteSpans = editable.getSpans(start, end, CustomQuoteSpan.class);
        if (existingQuoteSpans != null && existingQuoteSpans.length > 0) {
            return;
        }
        if (start > 2) {
            existingQuoteSpans = editable.getSpans(start - 2, start, CustomQuoteSpan.class);
            if (existingQuoteSpans != null && existingQuoteSpans.length > 0) {
                // Merge forward
                int quoteStart = editable.getSpanStart(existingQuoteSpans[0]);
                editable.setSpan(existingQuoteSpans[0], quoteStart, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                return;
            }
        }
        CustomQuoteSpan quoteSpan = new CustomQuoteSpan();
        editable.setSpan(quoteSpan, start, end,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        StyleHelper.updateCheckStatus(CustomQuote.this, true);
    }

    private void removeQuote() {
        EditText editText = getEditText();
        Editable editable = editText.getText();
        int currentLine = Util.getCurrentCursorLine(editText);
        int start = Util.getThisLineStart(editText, currentLine);
        int end = Util.getThisLineEnd(editText, currentLine);
        CustomQuoteSpan[] quoteSpans = null;
        if (start == 0) {
            quoteSpans = editable.getSpans(start, end, CustomQuoteSpan.class);
            editable.removeSpan(quoteSpans[0]);
            return;
        } else {
            quoteSpans = editable.getSpans(start - 1, end, CustomQuoteSpan.class);
        }
        if (quoteSpans == null || quoteSpans.length == 0) {
            quoteSpans = editable.getSpans(start, end, CustomQuoteSpan.class);
            if (quoteSpans != null && quoteSpans.length == 0) {
                return;
            }
        }
        int quoteStart = editable.getSpanStart(quoteSpans[0]);
        editable.removeSpan(quoteSpans[0]);
        if (start > quoteStart) {
            editable.setSpan(quoteSpans[0], quoteStart, start - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
//        Util.log("Quote apply style, start == " + start + ", end == " + end + ", is quote checked == " + mQuoteChecked);
//        if (!mQuoteChecked) {
//            return;
//        }

        CustomQuoteSpan[] quoteSpans = editable.getSpans(start, end, CustomQuoteSpan.class);
        if (null == quoteSpans || quoteSpans.length == 0) {
            return;
        }

        // Handle \n and backspace
        if (end > start) {
            // User inputs
            char c = editable.charAt(end - 1);
            if (c == Constants.CHAR_NEW_LINE) {
                editable.append(Constants.ZERO_WIDTH_SPACE_STR);
            }
        } else {
            // User deletes
            CustomQuoteSpan quoteSpan = quoteSpans[0];
            int spanStart = editable.getSpanStart(quoteSpan);
            int spanEnd = editable.getSpanEnd(quoteSpan);
            Util.log("Delete spanStart = " + spanStart + ", spanEnd = "
                    + spanEnd + " ,, start == " + start);
            if (spanStart == spanEnd) {
                setChecked(false);
                StyleHelper.updateCheckStatus(CustomQuote.this, false);
                removeQuote();
            }
            if (end > 2) {
                if (mRemovedNewLine) {
                    mRemovedNewLine = false;
                    return;
                }
                char pChar = editable.charAt(end - 1);
                if (pChar == Constants.CHAR_NEW_LINE) {
                    //
                    // This case
                    // |aa
                    // |
                    // When user deletes at the first of the 2nd line (i.e.: ZERO_WIDTH_STR)
                    // We want to put cursor to the end of the previous line "aa"
                    mRemovedNewLine = true;
                    editable.delete(end - 1, end);
                }
            }
        }
    }

    @Override
    public ImageView getImageView() {
        return this.mQuoteImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mQuoteChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mQuoteChecked;
    }

    @Override
    public EditText getEditText() {
        return this.mEditText;
    }

}

