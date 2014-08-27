package tw.freddie.ledcolor.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class HSVColorWheel extends View {

    private static final String TAG = "HSVColorWheel";

    private static final float SCALE = 1f;
    private static final float FADE_OUT_FRACTION = 0.03f;

    private static final int POINTER_LINE_WIDTH_DP = 2;
    private static final int POINTER_LENGTH_DP = 10;

    private static final float PIDivided180 = 180f / (float)Math.PI;

    private final Context mContext;

    private OnColorSelectedListener mListener;

    public interface OnColorSelectedListener {
        /**
         * @param color The color code selected, or null if no color.
         */
        public void colorSelected(Integer color);
    }

    public HSVColorWheel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        init();
    }

    public HSVColorWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public HSVColorWheel(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private int mScale;
    private int mPointerLength;
    private int mInnerPadding;
    private Paint mPointerPaint = new Paint();

    private void init() {
        float density = mContext.getResources().getDisplayMetrics().density;
        mScale = (int) (density * SCALE);
        mPointerLength = (int) (density * POINTER_LENGTH_DP );
        mPointerPaint.setStrokeWidth((int) (density * POINTER_LINE_WIDTH_DP));
        mInnerPadding = mPointerLength / 2;
    }

    public void setListener(OnColorSelectedListener mListener) {
        this.mListener = mListener;
    }

    float[] mColorHsv = { 0f, 0f, 1f };
    public void setColor( int color ) {
        Color.colorToHSV(color, mColorHsv);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if ( mBitmap != null ) {
            canvas.drawBitmap(mBitmap, null, mRect, null);
            float hueInPiInterval = mColorHsv[0] / 180f * (float)Math.PI;

            mSelectedPoint.x = mRect.left + (int) (-FloatMath.cos( hueInPiInterval ) * mColorHsv[1] * mInnerCircleRadius + mFullCircleRadius);
            mSelectedPoint.y = mRect.top + (int) (-FloatMath.sin( hueInPiInterval ) * mColorHsv[1] * mInnerCircleRadius + mFullCircleRadius);

            canvas.drawLine( mSelectedPoint.x - mPointerLength, mSelectedPoint.y, mSelectedPoint.x + mPointerLength, mSelectedPoint.y, mPointerPaint);
            canvas.drawLine( mSelectedPoint.x, mSelectedPoint.y - mPointerLength, mSelectedPoint.x, mSelectedPoint.y + mPointerLength, mPointerPaint);
        }
    }

    private Rect mRect;
    private Bitmap mBitmap;

    private int[] mPixels;
    private float mInnerCircleRadius;
    private float mFullCircleRadius;

    private int mScaledWidth;
    private int mScaledHeight;
    private int[] mScaledPixels;

    private float mScaledInnerCircleRadius;
    private float mScaledFullCircleRadius;
    private float mScaledFadeOutSize;

    private Point mSelectedPoint = new Point();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRect = new Rect(mInnerPadding, mInnerPadding, w - mInnerPadding, h - mInnerPadding);
        mBitmap = Bitmap.createBitmap( w - 2 * mInnerPadding, h - 2 * mInnerPadding, Config.ARGB_8888 );

        mFullCircleRadius = Math.min( mRect.width(), mRect.height() ) / 2;
        mInnerCircleRadius = mFullCircleRadius * ( 1 - FADE_OUT_FRACTION );

        mScaledWidth = mRect.width() / mScale;
        mScaledHeight = mRect.height() / mScale;
        mScaledFullCircleRadius = Math.min(mScaledWidth, mScaledHeight) / 2;
        mScaledInnerCircleRadius = mScaledFullCircleRadius * ( 1 - FADE_OUT_FRACTION );
        mScaledFadeOutSize = mScaledFullCircleRadius - mScaledInnerCircleRadius;
        mScaledPixels = new int[ mScaledWidth * mScaledHeight];
        mPixels = new int[ mRect.width() * mRect.height() ];

        Log.v(TAG, "+++createBitmap");
        createBitmap();
        Log.v(TAG, "---createBitmap");
    }

    private void createBitmap() {
        int w = mRect.width();
        int h = mRect.height();

        float[] hsv = new float[] { 0f, 0f, 1f };
        int alpha = 255;

        int x = (int) -mScaledFullCircleRadius, y = (int) -mScaledFullCircleRadius;
        for ( int i = 0; i < mScaledPixels.length; i++ ) {
            if ( i % mScaledWidth == 0 ) {
                x = (int) -mScaledFullCircleRadius;
                y++;
            } else {
                x++;
            }

            double centerDist = Math.sqrt( x*x + y*y );
            if ( centerDist <= mScaledFullCircleRadius) {
                hsv[ 0 ] = (float) (Math.atan2( y, x ) * PIDivided180 /* / Math.PI * 180f*/) + 180;
                hsv[ 1 ] = (float) (centerDist / mScaledInnerCircleRadius);
                if ( centerDist <= mScaledInnerCircleRadius) {
                    alpha = 255;
                } else {
                    alpha = 255 - (int) ((centerDist - mScaledInnerCircleRadius) / mScaledFadeOutSize * 255);
                }
                mScaledPixels[ i ] = Color.HSVToColor( alpha, hsv );
            } else {
                mScaledPixels[ i ] = 0x00000000;
            }
        }

        int scaledX, scaledY;
        for( x = 0; x < w; x++ ) {
            scaledX = x / mScale;
            if ( scaledX >= mScaledWidth) scaledX = mScaledWidth - 1;
            for ( y = 0; y < h; y++ ) {
                scaledY = y / mScale;
                if ( scaledY >= mScaledHeight) scaledY = mScaledHeight - 1;
                mPixels[ x * h + y ] = mScaledPixels[ scaledX * mScaledHeight + scaledY ];
            }
        }

        mBitmap.setPixels(mPixels, 0, w, 0, 0, w, h);

        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int maxWidth = MeasureSpec.getSize( widthMeasureSpec );
        int maxHeight = MeasureSpec.getSize( heightMeasureSpec );

        int width, height;
			/*
			 * Make the view quadratic, with height and width equal and as large as possible
			 */
        width = height = Math.min( maxWidth, maxHeight );

        setMeasuredDimension( width, height );
    }

    public int getColorForPoint( int x, int y, float[] hsv ) {
        x -= mFullCircleRadius;
        y -= mFullCircleRadius;
        double centerDist = Math.sqrt( x*x + y*y );
        hsv[ 0 ] = (float) (Math.atan2( y, x ) * PIDivided180 /*/ Math.PI * 180f*/) + 180;
        hsv[ 1 ] = Math.max( 0f, Math.min( 1f, (float) (centerDist / mInnerCircleRadius) ) );
        return Color.HSVToColor( hsv );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch ( action ) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                int color = getColorForPoint( (int)event.getX(), (int)event.getY(), mColorHsv);
                if ( mListener != null ) {
                    mListener.colorSelected(color);
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

}
