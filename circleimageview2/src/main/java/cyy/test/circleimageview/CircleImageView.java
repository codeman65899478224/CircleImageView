package cyy.test.circleimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

/**
 * @author chenyyb
 * @date 2019/9/12
 */

public class CircleImageView extends AppCompatImageView {
    public static final String TAG = CircleImageView.class.getSimpleName();

    private BitmapShader bitmapShader;
    private Matrix matrix = new Matrix();

    private RectF drawableRect = new RectF();

    private Bitmap bitmap;

    private float scale;

    private float radius;

    private Paint paint = new Paint();

    private ColorFilter mColorFilter;

    private boolean mReady;

    public CircleImageView(Context context) {
        super(context);
        init();
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Log.i(TAG, "init");
        mReady = true;
        paint.setAntiAlias(true);
        setup();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(TAG, "onDraw");
        canvas.drawCircle(drawableRect.centerX(), drawableRect.centerY(), radius, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.i(TAG, "onSizeChanged");
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    @Override
    public ScaleType getScaleType() {
        return ScaleType.CENTER_CROP;
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Log.i(TAG, "setImageBitmap");
        super.setImageBitmap(bm);
        initializeBitmap();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        Log.i(TAG, "setImageDrawable");
        super.setImageDrawable(drawable);
        initializeBitmap();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        Log.i(TAG, "setImageResource");
        super.setImageResource(resId);
        initializeBitmap();
    }

    @Override
    public void setImageURI(Uri uri) {
        Log.i(TAG, "setImageURI");
        super.setImageURI(uri);
        initializeBitmap();
    }


    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        applyColorFilter();
        invalidate();
    }

    private void applyColorFilter() {
        if (paint != null) {
            paint.setColorFilter(mColorFilter);
        }
    }

    private void initializeBitmap() {
        bitmap = getBitmap(getDrawable());
        setup();
    }

    private RectF createRect() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        Log.i(TAG, "left: " + left + " top: " + top
                + " right: " + left + sideLength + " bottom: " + top + sideLength);

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    private void setup(){
        Log.i(TAG, "setup");
        if (!mReady) {
            Log.i(TAG, "not ready !");
            return;
        }

        if (getWidth() == 0 && getHeight() == 0) {
            Log.i(TAG, "width == 0 and height == 0 !");
            return;
        }

        if (bitmap == null) {
            Log.i(TAG, "bitmap == null !");
            invalidate();
            return;
        }

        bitmap = getBitmap(getDrawable());
        bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader
                .TileMode.CLAMP);

        drawableRect.set(createRect());
        scale = Math.max(drawableRect.height()/bitmap.getHeight(), drawableRect.width()/bitmap.getWidth());
        float dx, dy;
        dx = (drawableRect.width() - bitmap.getWidth() * scale) * 0.5f;
        dy = (drawableRect.height() - bitmap.getHeight() * scale) * 0.5f;

        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);

        radius = Math.min(drawableRect.height(), drawableRect.width())/2;

        applyColorFilter();
        invalidate();
    }

    private Bitmap getBitmap(Drawable drawable){
        if (drawable == null){
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap
                        .createBitmap(2, 2, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap
                        .createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                                Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
