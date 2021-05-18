package com.msah.insight.styles.toolitems;


import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.msah.insight.styles.IStyle;
import com.msah.insight.styles.toolbar.IToolbar;



public interface IToolItem {

    /**
     * Each tool item is a style, and a style combines with specific span.
     * @return
     */
    public IStyle getStyle();

    /**
     * Each tool item has a view. If context is null, return the generated view.
     */
    public View getView(Context context);

    /**
     * Selection changed call back. Update tool item checked status
     *
     * @param selStart
     * @param selEnd
     */
    public void onSelectionChanged(int selStart, int selEnd);

    /**
     * Returns the toolbar of this tool item.
     * @return
     */
    public IToolbar getToolbar();

    /**
     * Sets the toolbar for this tool item.
     */
    public void setToolbar(IToolbar toolbar);

    /**
     * Gets the tool item updater instance, will be called when style being checked and unchecked.
     * @return
     */
    public IToolItem_Updater getToolItemUpdater();

    /**
     * Sets the tool item updater.
     * @param toolItemUpdater
     */
    public void setToolItemUpdater(IToolItem_Updater toolItemUpdater);

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data);

}

