package com.msah.insight.styles.windows;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.msah.insight.R;
import com.msah.insight.spans.CustomImageSpan;
import com.msah.insight.styles.IImage;
import com.msah.insight.utils.Util;

public class ImageSelectDialog {

    private Context mContext;

    private View mRootView;

    private Dialog mDialog;

    private IImage mAreImage;

    private int mRequestCode;

    public ImageSelectDialog(Context context, IImage areImage, int requestCode) {
        mContext = context;
        mAreImage = areImage;
        mRequestCode = requestCode;
        mRootView = initView();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Insert Image");
        builder.setView(mRootView);
        mDialog = builder.create();
    }

    private View initView() {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.image_select, null);
        final RelativeLayout insertInternetImageLayout = view.findViewById(R.id.image_select_from_internet_layout);

        RadioGroup radioGroup = view.findViewById(R.id.image_select_radio_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.image_select_from_local) {
                    openImagePicker();
                } else {
                    insertInternetImageLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        final TextView insertInternetImage = view.findViewById(R.id.image_select_insert);
        insertInternetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertInternetImage();
            }
        });
        return view;
    }

    public void show() {
        mDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void openImagePicker() {
        Intent intent = new Intent();
		intent.setType("image/*");
		//TODO: Must be ACTION_OPEN_DOCUMENT for media content provider to grant permission
		intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
		((Activity) this.mContext).startActivityForResult(intent, mRequestCode);
		mDialog.dismiss();
    }

    private void insertInternetImage() {
        EditText editText = mRootView.findViewById(R.id.image_select_internet_image_url);
        String imageUrl = editText.getText().toString();
        if (imageUrl.startsWith("http")
                &&  (imageUrl.endsWith("png") || imageUrl.endsWith("jpg") || imageUrl.endsWith("jpeg"))) {
            mAreImage.insertImage(imageUrl, CustomImageSpan.ImageType.URL);
            mDialog.dismiss();
        } else {
            Util.toast(mContext, "Not a valid image");
        }
    }

}
