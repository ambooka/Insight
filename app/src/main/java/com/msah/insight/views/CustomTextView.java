package com.msah.insight.views;

import android.content.Context;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.msah.insight.utils.Constants;
import com.msah.insight.events.MovementMethod;
import com.msah.insight.html.Html;
import com.msah.insight.render.ImageGetter;
import com.msah.insight.render.TagHandler;
import com.msah.insight.strategies.ClickStrategy;
import com.msah.insight.strategies.defaults.DefaultClickStrategy;
import com.msah.insight.utils.Util;

import java.util.HashMap;


public class CustomTextView extends AppCompatTextView {

    private static HashMap<String, Spanned> spannedHashMap = new HashMap<>();

    private ClickStrategy mClickStrategy;

    Context mContext;

    public CustomTextView(Context context) {
        this(context, null);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.DEFAULT_FONT_SIZE);
        initGlobalValues();
        initMovementMethod();
    }

    private void initGlobalValues() {
        int[] wh = Util.getScreenWidthAndHeight(mContext);
        Constants.SCREEN_WIDTH = wh[0];
        Constants.SCREEN_HEIGHT = wh[1];
    }

    private void initMovementMethod() {
        if (this.mClickStrategy == null) {
            this.mClickStrategy = new DefaultClickStrategy();
        }
        this.setMovementMethod(new MovementMethod(this.mClickStrategy));
    }

    public void fromHtml(String html) {
        Spanned spanned = getSpanned(html);
        setText(spanned);
    }

    private Spanned getSpanned(String html) {
        Html.sContext = mContext;
        Html.ImageGetter imageGetter = new ImageGetter(mContext, this);
        Html.TagHandler tagHandler = new TagHandler();
        return Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH, imageGetter, tagHandler);
    }

    /**
     * Use cache will take more RAM, you need to call clear cache when you think it is safe to do that.
     * You may need cache when working with {@link android.widget.ListView} or RecyclerView
     *
     * @param html
     */
    public void fromHtmlWithCache(String html) {
        Spanned spanned = null;
        if (spannedHashMap.containsKey(html)) {
            spanned = spannedHashMap.get(html);
        }
        if (spanned == null) {
            spanned = getSpanned(html);
            spannedHashMap.put(html, spanned);
        }
        if (spanned != null) {
            setText(spanned);
        }
    }

    public static void clearCache() {
        spannedHashMap.clear();
    }

    public void setClickStrategy(ClickStrategy clickStrategy) {
        this.mClickStrategy = clickStrategy;
        this.setMovementMethod(new MovementMethod(this.mClickStrategy));
    }
}

