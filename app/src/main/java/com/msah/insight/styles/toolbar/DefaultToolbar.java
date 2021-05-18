package com.msah.insight.styles.toolbar;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import com.msah.insight.styles.toolitems.IToolItem;
import com.msah.insight.views.CustomEditText;

import java.util.ArrayList;
import java.util.List;


public class DefaultToolbar extends HorizontalScrollView implements IToolbar {

    private Context mContext;

    private LinearLayout mContainer;

    private List<IToolItem> mToolItems = new ArrayList<>();

    private CustomEditText mAREditText;

    public DefaultToolbar(Context context) {
        this(context, null);
    }

    public DefaultToolbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DefaultToolbar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = (Activity) context;
        initSelf();
    }

    @Override
    public void addToolbarItem(IToolItem toolbarItem) {
        toolbarItem.setToolbar(this);
        mToolItems.add(toolbarItem);
        View view = toolbarItem.getView(mContext);
        if (view != null) {
            mContainer.addView(view);
        }
    }

    @Override
    public List<IToolItem> getToolItems() {
        return mToolItems;
    }

    @Override
    public void setEditText(CustomEditText editText) {
        this.mAREditText = editText;
    }

    @Override
    public CustomEditText getEditText() {
        return mAREditText;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (IToolItem toolItem : this.mToolItems) {
            toolItem.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void initSelf() {
        mContainer = new LinearLayout(mContext);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mContainer.setGravity(Gravity.CENTER_VERTICAL);
        mContainer.setLayoutParams(params);
        this.addView(mContainer);
    }
}

