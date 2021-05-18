package com.msah.insight.views;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.appcompat.widget.AppCompatEditText;

import com.msah.insight.utils.Constants;
import com.msah.insight.events.MovementMethod;
import com.msah.insight.html.Html;
import com.msah.insight.render.ImageGetter;
import com.msah.insight.render.TagHandler;
import com.msah.insight.spans.Clickable_Span;
import com.msah.insight.spans.CustomImageSpan;
import com.msah.insight.spans.CustomSubscriptSpan;
import com.msah.insight.spans.CustomSuperscriptSpan;
import com.msah.insight.spans.CustomUnderlineSpan;
import com.msah.insight.strategies.AtStrategy;
import com.msah.insight.strategies.ImageStrategy;
import com.msah.insight.styles.IStyle;
import com.msah.insight.styles.StyleHelper;
import com.msah.insight.styles.toolbar.CustomToolbar;
import com.msah.insight.styles.toolbar.IToolbar;
import com.msah.insight.styles.toolitems.IToolItem;
import com.msah.insight.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class CustomEditText extends AppCompatEditText {

    private IToolbar mToolbar;

    private static boolean LOG = false;

    private static boolean MONITORING = true;

    private CustomToolbar mFixedToolbar;

    private List<IStyle> mToolbarStylesList = new ArrayList<>();

    private Context mContext;

    private TextWatcher mTextWatcher;

    public CustomEditText(Context context) {
        this(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initGlobalValues();
        init();
        setupListener();
    }

    private void initGlobalValues() {
        int[] wh = Util.getScreenWidthAndHeight(mContext);
        Constants.SCREEN_WIDTH = wh[0];
        Constants.SCREEN_HEIGHT = wh[1];
    }

    private void init() {
        useSoftwareLayerOnAndroid8();
        // this.setMovementMethod(new AREMovementMethod());
        this.setFocusableInTouchMode(true);
        this.setBackgroundColor(Color.WHITE);
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
//			this.setInputType(EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
//					| EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//		} else {
        this.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
                | EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//		}
        int padding = 8;
        padding = Util.getPixelByDp(mContext, padding);
        this.setPadding(padding, padding, padding, padding);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.DEFAULT_FONT_SIZE);
    }

    private void paste(ClipData clip) {
        Editable mText = this.getEditableText();
        int min = 0;
        int max = mText.length();
        if (clip != null) {
            boolean didFirst = false;
            for (int i = 0; i < clip.getItemCount(); i++) {
                final CharSequence paste;
                paste = getClipItemCharSequence(clip.getItemAt(i));
                if (paste != null) {
                    if (!didFirst) {
                        Selection.setSelection((Spannable) mText, max);
                        ((Editable) mText).replace(min, max, paste);
                        didFirst = true;
                    } else {
                        ((Editable) mText).insert(getSelectionEnd(), "\n");
                        ((Editable) mText).insert(getSelectionEnd(), paste);
                    }
                }
            }
        }
    }

    @TargetApi(16)
    private CharSequence getClipItemCharSequence(ClipData.Item itemAt) {
        CharSequence text = getText();
        if (text instanceof Spanned) {
            return text;
        }
        String htmlText = itemAt.getHtmlText();
        if (htmlText != null) {
            try {
                Html.ImageGetter imageGetter = new ImageGetter(mContext, this);
                Html.TagHandler tagHandler = new TagHandler();
                CharSequence newText = Html.fromHtml(htmlText, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH, imageGetter, tagHandler);
                if (newText != null) {
                    return newText;
                }
            } catch (RuntimeException e) {
                // If anything bad happens, we'll fall back on the plain text.
            }
        }

        return itemAt.coerceToStyledText(mContext);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int off = MovementMethod.getTextOffset(this, this.getEditableText(), event);
        Clickable_Span[] clickableSpans = this.getText().getSpans(off, off, Clickable_Span.class);
        if (clickableSpans.length == 1 && clickableSpans[0] instanceof CustomImageSpan) {
            return true;
        }

        return super.onTouchEvent(event);
    }

    /**
     * Sets up listeners for controls.
     */
    private void setupListener() {
        setupTextWatcher();
    } // #End of setupListener()

    /**
     * Monitoring text changes.
     */
    private void setupTextWatcher() {
        mTextWatcher = new TextWatcher() {

            int startPos = 0;
            int endPos = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!MONITORING) {
                    return;
                }
                if (LOG) {
                    Util.log("beforeTextChanged:: s = " + s + ", start = " + start + ", count = " + count
                            + ", after = " + after);
                }
                // DO NOTHING FOR NOW
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!MONITORING) {
                    return;
                }

                if (LOG) {
                    Util.log("onTextChanged:: s = " + s + ", start = " + start + ", count = " + count + ", before = "
                            + before);
                }
                this.startPos = start;
                this.endPos = start + count;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!MONITORING) {
                    return;
                }

                if (LOG) {
                    Util.log("afterTextChanged:: s = " + s);
                }

                if (endPos <= startPos) {
                    Util.log("User deletes: start == " + startPos + " endPos == " + endPos);
                }

                for (IStyle style : mToolbarStylesList) {
                    style.applyStyle(s, startPos, endPos);
                }
            }
        };

        this.addTextChangedListener(mTextWatcher);
    }

    public void setToolbar(IToolbar toolbar) {
        mToolbarStylesList.clear();
        this.mToolbar = toolbar;
        this.mToolbar.setEditText(this);
        List<IToolItem> toolItems = toolbar.getToolItems();
        for (IToolItem toolItem : toolItems) {
            IStyle style = toolItem.getStyle();
            mToolbarStylesList.add(style);
        }
    }

    /**
     * Sets the fixed items toolbar to this EditText.
     */
    public void setFixedToolbar(CustomToolbar fixedToolbar) {
        mFixedToolbar = fixedToolbar;
        if (mFixedToolbar != null) {
            mToolbarStylesList = mFixedToolbar.getStylesList();
        }
    }

    @Override
    public void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mToolbar != null) {
            List<IToolItem> toolItems = mToolbar.getToolItems();
            for (IToolItem toolItem : toolItems) {
                toolItem.onSelectionChanged(selStart, selEnd);
            }
            return;
        }

        if (mFixedToolbar == null) {
            return;
        }

        boolean boldExists = false;
        boolean italicsExists = false;
        boolean underlinedExists = false;
        boolean strikethroughExists = false;
        boolean subscriptExists = false;
        boolean superscriptExists = false;
        boolean backgroundColorExists = false;
        boolean quoteExists = false;

        //
        // Two cases:
        // 1. Selection is just a pure cursor
        // 2. Selection is a range
        Editable editable = this.getEditableText();
        if (selStart > 0 && selStart == selEnd) {
            CharacterStyle[] styleSpans = editable.getSpans(selStart - 1, selStart, CharacterStyle.class);

            for (CharacterStyle styleSpan : styleSpans) {
                if (styleSpan instanceof StyleSpan) {
                    if (((StyleSpan) styleSpan).getStyle() == Typeface.BOLD) {
                        boldExists = true;
                    } else if (((StyleSpan) styleSpan).getStyle() == Typeface.ITALIC) {
                        italicsExists = true;
                    } else if (((StyleSpan) styleSpan).getStyle() == Typeface.BOLD_ITALIC) {
                        // TODO
                    }
                } else if (styleSpan instanceof CustomUnderlineSpan) {
                    underlinedExists = true;
                } else if (styleSpan instanceof StrikethroughSpan) {
                    strikethroughExists = true;
                } else if (styleSpan instanceof BackgroundColorSpan) {
                    backgroundColorExists = true;
                }
            }

            QuoteSpan[] quoteSpans = editable.getSpans(selStart - 1, selStart, QuoteSpan.class);
            if (quoteSpans != null && quoteSpans.length > 0) {
                quoteExists = true;
            }

            CustomSubscriptSpan[] subscriptSpans = editable.getSpans(selStart - 1, selStart, CustomSubscriptSpan.class);
            if (subscriptSpans != null && subscriptSpans.length > 0) {
                subscriptExists = true;
            }

            CustomSuperscriptSpan[] superscriptSpans = editable.getSpans(selStart - 1, selStart, CustomSuperscriptSpan.class);
            if (superscriptSpans != null && superscriptSpans.length > 0) {
                superscriptExists = true;
            }
        } else {
            //
            // Selection is a range
            CharacterStyle[] styleSpans = editable.getSpans(selStart, selEnd, CharacterStyle.class);

            for (CharacterStyle styleSpan : styleSpans) {
                if (styleSpan instanceof StyleSpan) {
                    if (((StyleSpan) styleSpan).getStyle() == Typeface.BOLD) {
                        if (editable.getSpanStart(styleSpan) <= selStart
                                && editable.getSpanEnd(styleSpan) >= selEnd) {
                            boldExists = true;
                        }
                    } else if (((StyleSpan) styleSpan).getStyle() == Typeface.ITALIC) {
                        if (editable.getSpanStart(styleSpan) <= selStart
                                && editable.getSpanEnd(styleSpan) >= selEnd) {
                            italicsExists = true;
                        }
                    } else if (((StyleSpan) styleSpan).getStyle() == Typeface.BOLD_ITALIC) {
                        if (editable.getSpanStart(styleSpan) <= selStart
                                && editable.getSpanEnd(styleSpan) >= selEnd) {
                            italicsExists = true;
                            boldExists = true;
                        }
                    }
                } else if (styleSpan instanceof CustomUnderlineSpan) {
                    if (editable.getSpanStart(styleSpan) <= selStart
                            && editable.getSpanEnd(styleSpan) >= selEnd) {
                        underlinedExists = true;
                    }
                } else if (styleSpan instanceof StrikethroughSpan) {
                    if (editable.getSpanStart(styleSpan) <= selStart
                            && editable.getSpanEnd(styleSpan) >= selEnd) {
                        strikethroughExists = true;
                    }
                } else if (styleSpan instanceof BackgroundColorSpan) {
                    if (editable.getSpanStart(styleSpan) <= selStart
                            && editable.getSpanEnd(styleSpan) >= selEnd) {
                        backgroundColorExists = true;
                    }
                }
            }
        }

        QuoteSpan[] quoteSpans = editable.getSpans(selStart, selEnd, QuoteSpan.class);
        if (quoteSpans != null && quoteSpans.length > 0) {
            if (editable.getSpanStart(quoteSpans[0]) <= selStart
                    && editable.getSpanEnd(quoteSpans[0]) >= selEnd) {
                quoteExists = true;
            }
        }

        CustomSubscriptSpan[] subscriptSpans = editable.getSpans(selStart, selEnd, CustomSubscriptSpan.class);
        if (subscriptSpans != null && subscriptSpans.length > 0) {
            if (editable.getSpanStart(subscriptSpans[0]) <= selStart
                    && editable.getSpanEnd(subscriptSpans[0]) >= selEnd) {
                subscriptExists = true;
            }
        }

        CustomSuperscriptSpan[] superscriptSpans = editable.getSpans(selStart, selEnd, CustomSuperscriptSpan.class);
        if (superscriptSpans != null && superscriptSpans.length > 0) {
            if (editable.getSpanStart(superscriptSpans[0]) <= selStart
                    && editable.getSpanEnd(superscriptSpans[0]) >= selEnd) {
                superscriptExists = true;
            }
        }

        //
        // Set style checked status
        StyleHelper.updateCheckStatus(mFixedToolbar.getBoldStyle(), boldExists);
        StyleHelper.updateCheckStatus(mFixedToolbar.getItalicStyle(), italicsExists);
        StyleHelper.updateCheckStatus(mFixedToolbar.getUnderlineStyle(), underlinedExists);
        StyleHelper.updateCheckStatus(mFixedToolbar.getStrikethroughStyle(), strikethroughExists);
        StyleHelper.updateCheckStatus(mFixedToolbar.getSubscriptStyle(), subscriptExists);
        StyleHelper.updateCheckStatus(mFixedToolbar.getSuperscriptStyle(), superscriptExists);
        StyleHelper.updateCheckStatus(mFixedToolbar.getBackgroundColoStyle(), backgroundColorExists);
        StyleHelper.updateCheckStatus(mFixedToolbar.getQuoteStyle(), quoteExists);
    } // #End of method:: onSelectionChanged

    /**
     * Sets html content to EditText.
     *
     * @param html
     * @return
     */
    public void fromHtml(String html) {
        Html.sContext = mContext;
        Html.ImageGetter imageGetter = new ImageGetter(mContext, this);
        Html.TagHandler tagHandler = new TagHandler();
        Spanned spanned = Html.fromHtml(html, Html.FROM_HTML_SEPARATOR_LINE_BREAK_PARAGRAPH, imageGetter, tagHandler);
        stopMonitor();
        this.getEditableText().append(spanned);
        startMonitor();
    }

    public String getHtml() {
        StringBuffer html = new StringBuffer();
        html.append("<html><body>");
        String editTextHtml = Html.toHtml(getEditableText(), Html.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL);
        html.append(editTextHtml);
        html.append("</body></html>");
        String htmlContent = html.toString().replaceAll(Constants.ZERO_WIDTH_SPACE_STR_ESCAPE, "");
        System.out.println(htmlContent);
        return htmlContent;
    }

    /**
     * Needs this because of this bug in Android O:
     * https://issuetracker.google.com/issues/67102093
     */
    public void useSoftwareLayerOnAndroid8() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) {
            this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public static void startMonitor() {
        MONITORING = true;
    }

    public static void stopMonitor() {
        MONITORING = false;
    }
    /* ----------------------
     * Customization part
     * ---------------------- */

    private AtStrategy mAtStrategy;
    public void setAtStrategy(AtStrategy atStrategy) { mAtStrategy = atStrategy; }
    public AtStrategy getAtStrategy() { return mAtStrategy; }

    // ImageStrategy
    private ImageStrategy mImageStrategy;
    public void setImageStrategy(ImageStrategy imageStrategy) { mImageStrategy = imageStrategy; }
    public ImageStrategy getImageStrategy() { return mImageStrategy; }
}
