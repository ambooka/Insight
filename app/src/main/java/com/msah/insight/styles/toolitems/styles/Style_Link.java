package com.msah.insight.styles.toolitems.styles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.msah.insight.R;
import com.msah.insight.spans.CustomUrlSpan;
import com.msah.insight.styles.ABS_FreeStyle;
import com.msah.insight.views.CustomEditText;


public class Style_Link extends ABS_FreeStyle {

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private ImageView mLinkImageView;
    private CustomEditText mEditText;

    public Style_Link(CustomEditText editText, ImageView imageView) {
        super(editText.getContext());
        this.mEditText = editText;
        this.mLinkImageView = imageView;
        setListenerForImageView(imageView);
    }

    @Override
    public EditText getEditText() {
        return this.mEditText;
    }

    public void setEditText(CustomEditText editText) {
        this.mEditText = editText;
    }

    @Override
    public void setListenerForImageView(ImageView imageView) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open dialog
                openLinkDialog();
            }
        });
    }

    private void openLinkDialog() {
        Activity activity = (Activity) mLinkImageView.getContext();
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setTitle(R.string.link_title);

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        @SuppressLint("InflateParams") final View areInsertLinkView = layoutInflater.inflate(R.layout.link_insert, null);

        builder.setView(areInsertLinkView)
                // Add the buttons
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText editText = areInsertLinkView.findViewById(R.id.insert_link_edit);
                        String url = editText.getText().toString();
                        if (TextUtils.isEmpty(url)) {
                            dialog.dismiss();
                            return;
                        }

                        insertLink(url);
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void insertLink(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (!url.startsWith(HTTP) && !url.startsWith(HTTPS)) {
            url = HTTP + url;
        }

        if (null != mEditText) {
            Editable editable = mEditText.getEditableText();
            int start = mEditText.getSelectionStart();
            int end = mEditText.getSelectionEnd();
            if (start == end) {
                editable.insert(start, url);
                end = start + url.length();
            }
            editable.setSpan(new CustomUrlSpan(url), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    @Override
    public void applyStyle(Editable editable, int start, int end) {
        // Do nothing
    }

    @Override
    public ImageView getImageView() {
        return null;
    }

    @Override
    public void setChecked(boolean isChecked) {
        // Do nothing
    }
}

