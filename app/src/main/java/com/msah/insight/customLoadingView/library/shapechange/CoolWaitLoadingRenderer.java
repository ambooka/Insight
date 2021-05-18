package com.msah.insight.customLoadingView.library.shapechange;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.msah.insight.R;
import com.msah.insight.customLoadingView.DensityUtil;
import com.msah.insight.customLoadingView.library.LoadingRenderer;


public class CoolWaitLoadingRenderer extends LoadingRenderer {
    private final Interpolator ACCELERATE_INTERPOLATOR08 = new AccelerateInterpolator(0.8f);
    private final Interpolator ACCELERATE_INTERPOLATOR10 = new AccelerateInterpolator(1.0f);
    private final Interpolator ACCELERATE_INTERPOLATOR15 = new AccelerateInterpolator(1.5f);

    private final Interpolator DECELERATE_INTERPOLATOR03 = new DecelerateInterpolator(0.3f);
    private final Interpolator DECELERATE_INTERPOLATOR05 = new DecelerateInterpolator(0.5f);
    private final Interpolator DECELERATE_INTERPOLATOR08 = new DecelerateInterpolator(0.8f);
    private final Interpolator DECELERATE_INTERPOLATOR10 = new DecelerateInterpolator(1.0f);

    private static final Interpolator ACCELERATE_DECELERATE_INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private final float DEFAULT_WIDTH = 200.0f;
    private final float DEFAULT_HEIGHT = 150.0f;
    private final float DEFAULT_STROKE_WIDTH = 8.0f;
    private final float WAIT_CIRCLE_RADIUS = 50.0f;

    private static final float WAIT_TRIM_DURATION_OFFSET = 0.5f;
    private static final float END_TRIM_DURATION_OFFSET = 1.0f;

    private final long ANIMATION_DURATION = 10000;

    private final Paint mPaint = new Paint();

    private final Path mWaitPath = new Path();
    private final Path mCurrentTopWaitPath = new Path();
    private final PathMeasure mWaitPathMeasure = new PathMeasure();

    private final RectF mCurrentBounds = new RectF();

    private float mStrokeWidth;
    private float mWaitCircleRadius;
    private float mOriginEndDistance;
    private float mOriginStartDistance;
    private float mWaitPathLength;

    private int mTopColor;

    private CoolWaitLoadingRenderer(Context context) {
        super(context);
        init(context);
        setupPaint();
    }

    private void init(Context context) {
        mWidth = DensityUtil.dip2px(context, DEFAULT_WIDTH);
        mHeight = DensityUtil.dip2px(context, DEFAULT_HEIGHT);
        mStrokeWidth = DensityUtil.dip2px(context, DEFAULT_STROKE_WIDTH);
        mWaitCircleRadius = DensityUtil.dip2px(context, WAIT_CIRCLE_RADIUS);

        mTopColor = R.color.colorPrimary;

        mDuration = ANIMATION_DURATION;
    }

    private void setupPaint() {
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void draw(Canvas canvas, Rect bounds) {
        int saveCount = canvas.save();
        RectF arcBounds = mCurrentBounds;
        arcBounds.set(bounds);

        mPaint.setColor(mTopColor);
        canvas.drawPath(mCurrentTopWaitPath, mPaint);

        canvas.restoreToCount(saveCount);
    }

    private Path createWaitPath(RectF bounds) {
        Path path = new Path();
        //create circle
        path.moveTo(bounds.centerX() + mWaitCircleRadius, bounds.centerY());

        //create w
        path.cubicTo(bounds.centerX() + mWaitCircleRadius, bounds.centerY() - mWaitCircleRadius * 0.5f,
                bounds.centerX() + mWaitCircleRadius * 0.3f, bounds.centerY() - mWaitCircleRadius,
                bounds.centerX() - mWaitCircleRadius * 0.35f, bounds.centerY() + mWaitCircleRadius * 0.5f);
        path.quadTo(bounds.centerX() + mWaitCircleRadius, bounds.centerY() - mWaitCircleRadius,
                bounds.centerX() + mWaitCircleRadius * 0.05f, bounds.centerY() + mWaitCircleRadius * 0.5f);
        path.lineTo(bounds.centerX() + mWaitCircleRadius * 0.75f, bounds.centerY() - mWaitCircleRadius * 0.2f);

        path.cubicTo(bounds.centerX(), bounds.centerY() + mWaitCircleRadius * 1f,
                bounds.centerX() + mWaitCircleRadius, bounds.centerY() + mWaitCircleRadius * 0.4f,
                bounds.centerX() + mWaitCircleRadius, bounds.centerY());

        //create arc
        path.arcTo(new RectF(bounds.centerX() - mWaitCircleRadius, bounds.centerY() - mWaitCircleRadius,
                bounds.centerX() + mWaitCircleRadius, bounds.centerY() + mWaitCircleRadius), 0, -359);
        path.arcTo(new RectF(bounds.centerX() - mWaitCircleRadius, bounds.centerY() - mWaitCircleRadius,
                bounds.centerX() + mWaitCircleRadius, bounds.centerY() + mWaitCircleRadius), 1, -359);
        path.arcTo(new RectF(bounds.centerX() - mWaitCircleRadius, bounds.centerY() - mWaitCircleRadius,
                bounds.centerX() + mWaitCircleRadius, bounds.centerY() + mWaitCircleRadius), 2, -2);
        //create w
        path.cubicTo(bounds.centerX() + mWaitCircleRadius, bounds.centerY() - mWaitCircleRadius * 0.5f,
                bounds.centerX() + mWaitCircleRadius * 0.3f, bounds.centerY() - mWaitCircleRadius,
                bounds.centerX() - mWaitCircleRadius * 0.35f, bounds.centerY() + mWaitCircleRadius * 0.5f);
        path.quadTo(bounds.centerX() + mWaitCircleRadius, bounds.centerY() - mWaitCircleRadius,
                bounds.centerX() + mWaitCircleRadius * 0.05f, bounds.centerY() + mWaitCircleRadius * 0.5f);
        path.lineTo(bounds.centerX() + mWaitCircleRadius * 0.75f, bounds.centerY() - mWaitCircleRadius * 0.2f);

        path.cubicTo(bounds.centerX(), bounds.centerY() + mWaitCircleRadius * 1f,
                bounds.centerX() + mWaitCircleRadius, bounds.centerY() + mWaitCircleRadius * 0.4f,
                bounds.centerX() + mWaitCircleRadius, bounds.centerY());





        return path;
    }

    @Override
    protected void computeRender(float renderProgress) {
        if (mCurrentBounds.isEmpty()) {
            return;
        }

        if (mWaitPath.isEmpty()) {
            mWaitPath.set(createWaitPath(mCurrentBounds));
            mWaitPathMeasure.setPath(mWaitPath, false);
            mWaitPathLength = mWaitPathMeasure.getLength();

            mOriginEndDistance = mWaitPathLength * 0.255f;
            mOriginStartDistance = mWaitPathLength * 0.045f;
        }

        mCurrentTopWaitPath.reset();


        //draw the first half : top
        if (renderProgress <= WAIT_TRIM_DURATION_OFFSET) {
            float topTrimProgress = ACCELERATE_DECELERATE_INTERPOLATOR.getInterpolation(renderProgress / WAIT_TRIM_DURATION_OFFSET);
            float topEndDistance = mOriginEndDistance + mWaitPathLength * 0.3f * topTrimProgress;
            float topStartDistance = mOriginStartDistance + mWaitPathLength * 0.48f * topTrimProgress;
            mWaitPathMeasure.getSegment(topStartDistance, topEndDistance, mCurrentTopWaitPath, true);
        }





        //draw the last half : top
        if (renderProgress <= END_TRIM_DURATION_OFFSET && renderProgress > WAIT_TRIM_DURATION_OFFSET) {
            float trimProgress = ACCELERATE_DECELERATE_INTERPOLATOR.getInterpolation((renderProgress - WAIT_TRIM_DURATION_OFFSET) / (END_TRIM_DURATION_OFFSET - WAIT_TRIM_DURATION_OFFSET));
            float topEndDistance = mOriginEndDistance + mWaitPathLength * 0.3f + mWaitPathLength * 0.45f * trimProgress;
            float topStartDistance = mOriginStartDistance + mWaitPathLength * 0.48f + mWaitPathLength * 0.27f * trimProgress;
            mWaitPathMeasure.getSegment(topStartDistance, topEndDistance, mCurrentTopWaitPath, true);

        }



    }

    @Override
    protected void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);

    }

    @Override
    protected void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);

    }

    @Override
    protected void reset() {
    }

    public static class Builder {
        private Context mContext;

        public Builder(Context mContext) {
            this.mContext = mContext;
        }

        public CoolWaitLoadingRenderer build() {
            CoolWaitLoadingRenderer loadingRenderer = new CoolWaitLoadingRenderer(mContext);
            return loadingRenderer;
        }
    }
}
