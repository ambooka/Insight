package com.msah.insight.styles.toolitems;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.msah.insight.R;
import com.msah.insight.spans.CustomImageSpan;
import com.msah.insight.strategies.ImageStrategy;
import com.msah.insight.styles.IStyle;
import com.msah.insight.styles.toolitems.styles.Style_Image;
import com.msah.insight.utils.Util;
import com.msah.insight.views.CustomEditText;


public class ToolItem_Image extends ToolItem_Abstract {

    @Override
    public IToolItem_Updater getToolItemUpdater() {
        return null;
    }

    @Override
    public IStyle getStyle() {
        if (mStyle == null) {
            CustomEditText editText = this.getEditText();
            mStyle = new Style_Image(editText, (ImageView) mToolItemView);
        }
        return mStyle;
    }

    @Override
    public View getView(Context context) {
        if (null == context) {
            return mToolItemView;
        }
        if (mToolItemView == null) {
            ImageView imageView = new ImageView(context);
            int size = Util.getPixelByDp(context, 40);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
            imageView.setLayoutParams(params);
            imageView.setImageResource(R.drawable.image);
            imageView.bringToFront();
            mToolItemView = imageView;
        }

        return mToolItemView;
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (Style_Image.REQUEST_CODE == requestCode) {
                Style_Image imageStyle = (Style_Image) getStyle();
                Uri uri = data.getData();
                ImageStrategy imageStrategy = this.getEditText().getImageStrategy();
              /** TODO:Check Image strategy upload fuction
                if (imageStrategy != null) {
                    imageStrategy.uploadAndInsertImage(uri, imageStyle);
                    return;
                }

                **/
                imageStyle.insertImage(uri, CustomImageSpan.ImageType.URI);
            }
        }
    }
}
