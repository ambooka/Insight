package com.msah.insight.strategies.defaults;

import android.content.Context;
import android.content.Intent;
import android.text.style.URLSpan;

import com.msah.insight.spans.AtSpan;
import com.msah.insight.spans.CustomImageSpan;
import com.msah.insight.strategies.ClickStrategy;

import static com.msah.insight.spans.CustomImageSpan.ImageType;


public class DefaultClickStrategy implements ClickStrategy {
    @Override
    public boolean onClickAt(Context context, AtSpan atSpan) {
        Intent intent = new Intent();
        intent.setClass(context, DefaultProfileActivity.class);
        intent.putExtra("userKey", atSpan.getUserKey());
        intent.putExtra("userName", atSpan.getUserName());
        context.startActivity(intent);
        return true;
    }

    @Override
    public boolean onClickImage(Context context, CustomImageSpan imageSpan) {
        Intent intent = new Intent();
        ImageType imageType = imageSpan.getImageType();
        intent.putExtra("imageType", imageType);
        if (imageType == ImageType.URI) {
            intent.putExtra("uri", imageSpan.getUri());
        } else if (imageType == ImageType.URL) {
            intent.putExtra("url", imageSpan.getURL());
        } else {
            intent.putExtra("resId", imageSpan.getResId());
        }
        intent.setClass(context, DefaultImagePreviewActivity.class);
        context.startActivity(intent);
        return true;
    }


    @Override
    public boolean onClickUrl(Context context, URLSpan urlSpan) {
        // Use default behavior
        return false;
    }
}
