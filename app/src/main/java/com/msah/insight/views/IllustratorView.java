package com.msah.insight.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IllustratorView extends ScrollView {

    public static final int PIXEL_SIZE = 1;
    private Paint mPaint;
    private int mLastX;
    private int mLastY;
    private Canvas mBuffer;
    private Bitmap mBitmap;
    private Paint mBitmapPaint;
    private DatabaseReference mFirebaseRef;
    private ChildEventListener mListener;
    private int mCurrentColor = 0xFFFF0000;
    private int mCurrentStrokeSize = 10;
    private Path mPath, dBPath;
    private Set<String> mOutstandingSegments;
    private Segment mCurrentSegment;
    private float mScale = 1.0f;
    private int mCanvasWidth;
    private int mCanvasHeight;

    public int counter = 0;

    ObjectAnimator animator = ObjectAnimator.ofFloat(IllustratorView.this, "phase", 1.0f, 0.0f);



    private float length;

    public IllustratorView(Context context, DatabaseReference ref) {

        this(context, ref, 10);
    }

    public IllustratorView(Context context, DatabaseReference ref, int width, int height) {
        this(context, ref);
        mCanvasWidth = width;
        mCanvasHeight = height;
    }

    public IllustratorView(Context context, DatabaseReference ref, float scale) {
        super(context);

        mOutstandingSegments = new HashSet<String>();
        mPath = new Path();
        dBPath = new Path();
        this.mFirebaseRef = ref;
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Segment segment;
                List<Segment> incomingSegments = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    segment = ds.getValue(Segment.class);
                    String name = ds.getKey();
                        incomingSegments.add(segment);
                }

                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {


                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                            if(counter <= incomingSegments.size()-1) {

                                Segment sg = incomingSegments.get(counter);
                                dBPath.reset();
                                drawSegment(sg, paintFromColor(sg.getColor()));
                                invalidate();
                                addListener(ref);
                                counter += 1;
                            }

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        ref.addListenerForSingleValueEvent(valueEventListener);
        this.mScale = scale;
        addListener(ref);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(0xFFFF0000);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setShadowLayer(0.2f, 0, 0, Color.RED);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    }
    public void init(Segment segment) {
        Segment testSeg = segment;
        List<Point> points = testSeg.getPoints();
        float scale = 1 * PIXEL_SIZE;
        Point current = points.get(0);
        dBPath.moveTo(Math.round(scale * current.x), Math.round(scale * current.y));
        Point next = null;
        for (int i = 1; i < points.size(); ++i) {
            next = points.get(i);
            dBPath.quadTo(
                    Math.round(scale * current.x), Math.round(scale * current.y),
                    Math.round(scale * (next.x + current.x) / 2), Math.round(scale * (next.y + current.y) / 2)
            );
            current = next;
        }
        if (next != null) {
            dBPath.lineTo(Math.round(scale * next.x), Math.round(scale * next.y));
        }
        PathMeasure measure = new PathMeasure(dBPath, false);
        length = measure.getLength();
        float[] intervals = new float[]{length, length};
        animator.setDuration(3000);
        animator.start();
      //  mBuffer.drawPath(getPathForPoints(segment.getPoints(), mScale), mPaint);
    }
    public void setPhase(float phase){
        mPaint.setPathEffect(createPathEffect(length, phase, 0.0f));
        invalidate();
    }
    private static PathEffect createPathEffect(float pathLength, float phase, float offset){
        return new DashPathEffect(new float[]{pathLength, pathLength}, Math.max(phase*pathLength, offset));
    }
    public void addListener(DatabaseReference ref){
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
                if(!mOutstandingSegments.contains(name)){
                    // Deserialize the data into our Segment class
                    Segment segment = dataSnapshot.getValue(Segment.class);
                    dBPath.reset();
                    drawSegment(segment, paintFromColor(segment.getColor()));
                   mOutstandingSegments.add(name);
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
    }

    public void removeListener() {
        mFirebaseRef.removeEventListener(mListener);
    }

    public void setColor(int color) {
        mCurrentColor = color;
        mPaint.setColor(color);
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
        dBPath.reset();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        mScale = Math.min(1.0f * w / mCanvasWidth, 1.0f * h / mCanvasHeight);
        mBitmap = Bitmap.createBitmap(Math.round(mCanvasWidth * mScale), Math.round(mCanvasHeight * mScale), Bitmap.Config.ARGB_8888);
        mBuffer = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        //canvas.drawPath(dBPath, mPaint);
        mBuffer.drawPath(dBPath, mPaint);
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
            init(segment);

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
