package com.msah.insight.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.msah.insight.colorpicker.ColorPickerListener;
import com.msah.insight.colorpicker.ColorPickerView;
import com.msah.insight.notifications.Data;
import com.msah.insight.styles.IStyle;
import com.msah.insight.styles.toolbar.IToolbar;
import com.msah.insight.styles.toolitems.IToolItem;
import com.msah.insight.styles.toolitems.ToolItem_FontColor;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class IllustratorView extends ScrollView {

    public static final int PIXEL_SIZE = 1;
    private Paint mPaint;
    private int mLastX;
    private int mLastY;
    int width;
    private Canvas mBuffer;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private DatabaseReference mFirebaseRef;
    private ChildEventListener mListener;
    private int mCurrentColor = 0xFFFF0000;
    private int mCurrentStrokeSize = 10;
    private Path mPath;

    private Set<String> mOutstandingSegments;
    private Segment mCurrentSegment;
    private float mScale = 1.0f;
    private int mCanvasWidth;
    private int mCanvasHeight;

    private int mPivotX = 0;
    private int mPivotY = 0;
    private int radius = 60;

    String mText = "text to be tested";
    TextPaint mTextPaint;
    StaticLayout mStaticLayout;
    private IToolbar mToolbar;

    Rect rectangle;





    public IllustratorView(Context context, DatabaseReference ref) {

        this(context, ref, 10);
        initLabelView();
    }
    public IllustratorView(Context context, DatabaseReference ref, int width, int height) {
        this(context, ref);
        this.setBackgroundColor(Color.DKGRAY);
        mCanvasWidth = width;
        mCanvasHeight = height;
    }
    public IllustratorView(Context context, DatabaseReference ref, float scale) {
        super(context);

        mOutstandingSegments = new HashSet<String>();
        mPath = new Path();
        this.mFirebaseRef = ref;
        this.mScale = scale;

        mListener = ref.addChildEventListener(new ChildEventListener() {
            /**
             * @param dataSnapshot The data we need to construct a new Segment
             * @param previousChildName Supplied for ordering, but we don't really care about ordering in this app
             */
            @Override
            public void onChildAdded(@NotNull DataSnapshot dataSnapshot, String previousChildName) {
                String name = dataSnapshot.getKey();
                // To prevent lag, we draw our own segments as they are created. As a result, we need to check to make
                // sure this event is a segment drawn by another user before we draw it
                if (!mOutstandingSegments.contains(name)) {
                    // Deserialize the data into our Segment class
                    Segment segment = dataSnapshot.getValue(Segment.class);
                    drawSegment(segment, paintFromColor(segment.getColor()));

                    // Tell the view to redraw itself
                    invalidate();
                }
            }

            @Override
            public void onChildChanged(@NotNull DataSnapshot dataSnapshot, String s) {
                // No-op
            }

            @Override
            public void onChildRemoved(@NotNull DataSnapshot dataSnapshot) {
                // No-op
            }

            @Override
            public void onChildMoved(@NotNull DataSnapshot dataSnapshot, String s) {
                // No-op
            }

            @Override
            public void onCancelled(@NotNull DatabaseError firebaseError) {
                // No-op
            }
        });


        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);



        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void removeListener() {
        mFirebaseRef.removeEventListener(mListener);
    }

    public void setColor(int color) {
        mCurrentColor = color;
        mPaint.setColor(color);
    }

    public void drawText(String text) {


        Canvas canvas = new Canvas(mBitmap);
        TextPaint tp = new TextPaint();
        tp.setColor(Color.WHITE);
        tp.setTextSize(80);
        tp.setAntiAlias(true);

// split line
        StaticLayout staticLayout = new StaticLayout("Title text", tp,
                mBuffer.getWidth(), Layout.Alignment.ALIGN_CENTER, 1, 0, false);
        int yPos = (mBuffer.getHeight() / 2) - (staticLayout.getHeight() / 2);
        mBuffer.translate(0, yPos);
        staticLayout.draw(mBuffer);
        canvas.restore();
     }
    public void setStrokeSize(int stokeSize){
        mCurrentStrokeSize = stokeSize;
        mPaint.setStrokeWidth(mCurrentStrokeSize);
    }

    public void clear() {
        mBitmap = Bitmap.createBitmap(mBitmap.getWidth(), mBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        mBuffer = new Canvas(mBitmap);
        mCurrentSegment = null;
        mOutstandingSegments.clear();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);

        mScale = Math.min(1.0f * w / mCanvasWidth, 1.0f * h / mCanvasHeight);

        mBitmap = Bitmap.createBitmap(Math.round(mCanvasWidth * mScale), Math.round(mCanvasHeight * mScale), Bitmap.Config.ARGB_8888);
        mBuffer = new Canvas(mBitmap);
        Log.i("AndroidDrawing", "onSizeChanged: created bitmap/buffer of "+mBitmap.getWidth()+"x"+mBitmap.getHeight());
    }

    public void drawCircle() {

        int minX = radius * 2;
        int maxX = getWidth() - (radius *2 );

        int minY = radius * 2;
        int maxY = getHeight() - (radius *2 );

        //Generate random numbers for x and y locations of the circle on screen
        Random random = new Random();
        mPivotX = random.nextInt(maxX - minX + 1) + minX;
        mPivotY = random.nextInt(maxY - minY + 1) + minY;

        //important. Refreshes the view by calling onDraw function
        invalidate();
    }

    public void drawRect() {
        invalidate();
    }


    private void initLabelView() {
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(16 * getResources().getDisplayMetrics().density);
        mTextPaint.setColor(mPaint.getColor());

        // default to a single line of text
        width = (int) mTextPaint.measureText(mText);


        mStaticLayout = new StaticLayout(mText, mTextPaint, width, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0, true);




        // New API alternate
        //
        // StaticLayout.Builder builder = StaticLayout.Builder.obtain(mText, 0, mText.length(), mTextPaint, width)
        //        .setAlignment(Layout.Alignment.ALIGN_NORMAL)
        //        .setLineSpacing(1, 0) // multiplier, add
        //        .setIncludePad(false);
        // mStaticLayout = builder.build();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // Final
        canvas.drawColor(Color.WHITE);
        canvas.drawRect(0, 0, radius, radius, mPaint);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        //canvas.drawRect(0, 0, mBitmap.getWidth(), mBitmap.getHeight(), paintFromColor(Color.WHITE, Paint.Style.FILL_AND_STROKE));
        canvas.drawPath(mPath, mPaint);
        canvas.drawCircle(mPivotX, mPivotY, radius, mPaint);

        int textHeight = mStaticLayout.getHeight();

        // get position of text's top left corner
        //float x = (canvas.getWidth() - width)/2;
       // float y = (canvas.getHeight() - textHeight)/2;
        //canvas.translate(x, y);
        int y = 100;
        for(int i = 0; i<mText.length(); i++) {
            canvas.save();
            canvas.translate(100, y);
          //  mStaticLayout.draw(canvas);
            canvas.restore();
            y += 50;
        }

    }

    public static Paint paintFromColor(int color) {
        return paintFromColor(color, Paint.Style.STROKE);
    }

    public static Paint paintFromColor(int color, Paint.Style style) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(true);
        p.setColor(color);
        p.setStyle(style);
        return p;
    }

    public static Path getPathForPoints(List<Point> points, double scale) {
        Path path = new Path();
        scale = scale * PIXEL_SIZE;
        Point current = points.get(0);
        path.moveTo(Math.round(scale * current.x), Math.round(scale * current.y));
        Point next = null;
        for (int i = 1; i < points.size(); ++i) {
            next = points.get(i);
            path.quadTo(
                    Math.round(scale * current.x), Math.round(scale * current.y),
                    Math.round(scale * (next.x + current.x) / 2), Math.round(scale * (next.y + current.y) / 2)
            );
            current = next;
        }
        if (next != null) {
            path.lineTo(Math.round(scale * next.x), Math.round(scale * next.y));
        }
        return path;
    }





    private void drawSegment(Segment segment, Paint paint) {
        if (mBuffer != null) {

            paint.setStrokeWidth(10);
            mBuffer.drawPath(getPathForPoints(segment.getPoints(), mScale), paint);
            mBuffer.drawPath(mPath, mPaint);
        }

    }
    private void onTouchStart(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mCurrentSegment = new Segment(mCurrentColor);
        mLastX = (int) x / PIXEL_SIZE;
        mLastY = (int) y / PIXEL_SIZE;
        mCurrentSegment.addPoint(mLastX, mLastY);
    }

    private void onTouchMove(float x, float y) {

        int x1 = (int) x / PIXEL_SIZE;
        int y1 = (int) y / PIXEL_SIZE;

        float dx = Math.abs(x1 - mLastX);
        float dy = Math.abs(y1 - mLastY);
        if (dx >= 1 || dy >= 1) {
            mPath.quadTo(mLastX * PIXEL_SIZE, mLastY * PIXEL_SIZE, ((x1 + mLastX) * PIXEL_SIZE) / 2, ((y1 + mLastY) * PIXEL_SIZE) / 2);
            mLastX = x1;
            mLastY = y1;
            mCurrentSegment.addPoint(mLastX, mLastY);
        }
    }

    private void onTouchEnd() {
        mPath.lineTo(mLastX * PIXEL_SIZE, mLastY * PIXEL_SIZE);
        mBuffer.drawPath(mPath, mPaint);



        mPath.reset();
        DatabaseReference segmentRef = mFirebaseRef.push();
        final String segmentName = segmentRef.getKey();
        mOutstandingSegments.add(segmentName);

        // create a scaled version of the segment, so that it matches the size of the board
        Segment segment = new Segment(mCurrentSegment.getColor());
        for (Point point: mCurrentSegment.getPoints()) {
            segment.addPoint((int)Math.round(point.x / mScale), (int)Math.round(point.y / mScale));
        }

        // Save our segment into Firebase. This will let other clients see the data and add it to their own canvases.
        // Also make a note of the outstanding segment name so we don't do a duplicate draw in our onChildAdded callback.
        // We can remove the name from mOutstandingSegments once the completion listener is triggered, since we will have
        // received the child added event by then.
        segmentRef.setValue(segment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, @NotNull DatabaseReference firebaseRef) {
                if (error != null) {
                    Log.e("Insight illustrator", error.toString());
                    throw error.toException();
                }
                mOutstandingSegments.remove(segmentName);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                onTouchEnd();
                invalidate();
                break;
        }
        return true;
    }

}
