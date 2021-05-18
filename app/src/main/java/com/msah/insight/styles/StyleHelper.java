package com.msah.insight.styles;


import android.view.View;

import com.msah.insight.utils.Constants;

public class StyleHelper {

    /**
     * Updates the check status.
     *
     * @param areStyle
     * @param checked
     */
    public static void updateCheckStatus(IStyle areStyle, boolean checked) {
        areStyle.setChecked(checked);
        View imageView = areStyle.getImageView();
        int color = checked ? Constants.CHECKED_COLOR : Constants.UNCHECKED_COLOR;
        imageView.setBackgroundColor(color);
    }


}

