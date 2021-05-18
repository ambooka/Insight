package com.msah.insight.styles;


import android.content.Context;
import android.widget.EditText;

import com.msah.insight.styles.toolbar.CustomToolbar;

public abstract class ABS_FreeStyle implements IStyle {

    protected Context mContext;
    protected CustomToolbar mToolbar;
    protected EditText mEditText;

    public ABS_FreeStyle(Context context) {
        mContext = context;
    }

    public ABS_FreeStyle(CustomToolbar toolbar) {
        this.mToolbar = toolbar;
        if (null != toolbar) {
            this.mContext = toolbar.getContext();
            this.mEditText = toolbar.getEditText();
        }
    }

    @Override
    public EditText getEditText() {
        if (null != mEditText) {
            return mEditText;
        }
        if (null != mToolbar) {
            return mToolbar.getEditText();
        }
        return null;
    }

    // Dummy implementation
    @Override
    public boolean getIsChecked() {
        return false;
    }
}

