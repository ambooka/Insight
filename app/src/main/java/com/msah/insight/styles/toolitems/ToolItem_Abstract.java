package com.msah.insight.styles.toolitems;

import android.content.Intent;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.msah.insight.styles.IStyle;
import com.msah.insight.styles.toolbar.IToolbar;
import com.msah.insight.utils.Util;
import com.msah.insight.views.CustomEditText;

public abstract class ToolItem_Abstract implements IToolItem {

    protected IStyle mStyle;

    protected View mToolItemView;

    protected IToolItem_Updater mToolItemUpdater;

    private IToolbar mToolbar;

    @Override
    public IToolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void setToolbar(IToolbar toolbar) {
        mToolbar = toolbar;
    }

    @Override
    public void setToolItemUpdater(IToolItem_Updater toolItemUpdater) {
        mToolItemUpdater = toolItemUpdater;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Default do nothing
        // Children classes can override if necessary
        return;
    }

    public CustomEditText getEditText() {
        return mToolbar.getEditText();
    }



    protected <T> void printSpans(Class<T> clazz) {
        EditText editText = getEditText();
        Editable editable = editText.getEditableText();
        T[] spans = editable.getSpans(0, editable.length(), clazz);
        for (T span : spans) {
            int start = editable.getSpanStart(span);
            int end = editable.getSpanEnd(span);
            Util.log("Span -- " + clazz + ", start = " + start + ", end == " + end);
        }
    }
}

