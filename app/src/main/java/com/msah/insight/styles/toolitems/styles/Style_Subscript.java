package com.msah.insight.styles.toolitems.styles;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.msah.insight.spans.CustomSubscriptSpan;
import com.msah.insight.styles.ABS_Style;
import com.msah.insight.styles.StyleHelper;
import com.msah.insight.styles.toolitems.IToolItem_Updater;
import com.msah.insight.views.CustomEditText;


public class Style_Subscript extends ABS_Style<CustomSubscriptSpan> {

    private ImageView mSubscriptImage;

    private boolean mSubscriptChecked;

    private CustomEditText mEditText;

    private IToolItem_Updater mCheckUpdater;

    /**
     * @param imageView image view
     */
    public Style_Subscript(CustomEditText editText, ImageView imageView, IToolItem_Updater checkUpdater) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mSubscriptImage = imageView;
        this.mCheckUpdater = checkUpdater;
        setListenerForImageView(this.mSubscriptImage);
    }

    /**
     * @param editText edit text
     */
    public void setEditText(CustomEditText editText) {
        this.mEditText = editText;
    }

    @Override
    public EditText getEditText() {
        return mEditText;
    }

    @Override
    public void setListenerForImageView(final ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSubscriptChecked = !mSubscriptChecked;
                StyleHelper.updateCheckStatus(Style_Subscript.this, mSubscriptChecked);
                if (null != mEditText) {
                    applyStyle(mEditText.getEditableText(),
                            mEditText.getSelectionStart(),
                            mEditText.getSelectionEnd());
                }
            }
        });
    }

    @Override
    public ImageView getImageView() {
        return mSubscriptImage;
    }

    @Override
    public void setChecked(boolean isChecked) {
        this.mSubscriptChecked = isChecked;
    }

    @Override
    public boolean getIsChecked() {
        return this.mSubscriptChecked;
    }

    @Override
    public CustomSubscriptSpan newSpan() {
        return new CustomSubscriptSpan();
    }
}
