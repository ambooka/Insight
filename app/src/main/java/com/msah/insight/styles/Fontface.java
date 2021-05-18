package com.msah.insight.styles;


import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.utils.Constants;
import com.msah.insight.spans.CustomTypefaceSpan;
import com.msah.insight.styles.toolbar.CustomToolbar;

public class Fontface extends ABS_FreeStyle {

    private ImageView mFontfaceImageView;

    /**
     *
     * @param fontSizeImage
     */
    public Fontface(ImageView fontSizeImage, CustomToolbar toolbar) {
        super(toolbar);
        this.mFontfaceImageView = fontSizeImage;
        setListenerForImageView(this.mFontfaceImageView);
    }

    @Override
    public void setListenerForImageView(final ImageView imageView) {
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = getEditText();
                int selectionStart = editText.getSelectionStart();
                int selectionEnd = editText.getSelectionEnd();

                AssetManager assetManager = editText.getContext().getAssets();
                Typeface typeface = Typeface.createFromAsset(assetManager, "fonts/walkway.ttf");
                CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan(typeface);
                if (selectionStart == selectionEnd) {
                    SpannableStringBuilder ssb = new SpannableStringBuilder();
                    ssb.append(Constants.ZERO_WIDTH_SPACE_STR);
                    ssb.setSpan(typefaceSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                    editText.getEditableText().replace(selectionStart, selectionEnd, ssb);
                } else {
                    editText.getEditableText().setSpan(typefaceSpan, selectionStart, selectionEnd,
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }
        });
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
        // Do nothing
    } // #End of method

    @Override
    public ImageView getImageView() {
        return this.mFontfaceImageView;
    }

    @Override
    public void setChecked(boolean isChecked) {
        // Do nothing.
    }
}

