package com.github.caiiiycuk.hmv.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.camera.core.ImageProxy;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.facebook.litho.drawable.ComparableDrawableWrapper;
import com.github.caiiiycuk.hmv.HideMyVoteApplication;
import com.github.caiiiycuk.hmv.R;

import java.nio.ByteBuffer;
import java.util.Random;

public class Ui {

    private static final String[] marks = new String[] {
      "V", "v", "X", "x", "O", "o", "\\/"
    };

    private static final Random random = new Random();

    private static Typeface[] fonts;

    private static Handler handler = new Handler(Looper.getMainLooper());

    private Ui() {
    }

    public static void initFonts(@NonNull Context context) {
        fonts = new Typeface[]{
                ResourcesCompat.getFont(context, R.font.font_1),
                ResourcesCompat.getFont(context, R.font.font_2),
                ResourcesCompat.getFont(context, R.font.font_3),
                ResourcesCompat.getFont(context, R.font.font_4),
                ResourcesCompat.getFont(context, R.font.font_5),
                ResourcesCompat.getFont(context, R.font.font_6),
        };
    }

    public static @Px
    int getPx(@DimenRes int res) {
        return HideMyVoteApplication.getContext().getResources().getDimensionPixelSize(res);
    }

    public static @ColorInt
    int getColor(@ColorRes int color) {
        return ContextCompat.getColor(HideMyVoteApplication.getContext(), color);
    }

    public static ComparableDrawableWrapper circle(@ColorRes int color) {
        OvalShape shape = new OvalShape();
        ShapeDrawable drawable = new ShapeDrawable(shape);
        drawable.getPaint().setColor(getColor(color));
        return new IntDrawable(drawable, "circle", color);
    }

    @Nullable
    public static Bitmap imageProxyToBitmap(@NonNull ImageProxy proxy, int rotationDegrees) {
        Image image = proxy.getImage();

        if (image == null || image.getPlanes().length == 0) {
            return null;
        }

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        buffer.rewind();
        byte[] bytes = new byte[buffer.capacity()];
        buffer.get(bytes);

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        if (rotationDegrees != 0 && bitmap.getWidth() > bitmap.getHeight()) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);

            Bitmap rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    matrix, true);
            bitmap.recycle();
            bitmap = rotated;
        }

        return bitmap;
    }

    public static Bitmap createMark(int width, int height, float angle) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTypeface(fonts[random.nextInt(fonts.length)]);

        String text = marks[random.nextInt(marks.length)];

        float size = height;
        textPaint.setTextSize(height);
        float measuredWidth = textPaint.measureText(text);
        if (measuredWidth > width) {
            size = size * width / measuredWidth;
        }

        canvas.rotate((angle -15 + random.nextInt(31)) % 30, width / 2, height / 2);

        textPaint.setTextSize(size);

        float x = width / 2;
        float y = height / 2 - (textPaint.descent() + textPaint.ascent()) / 2;

        textPaint.setColor(Color.argb(128, 0, 0, 0));
        canvas.drawText(text, 0, text.length(), x, y, textPaint);

        for (int i = 0; i < 10; ++i) {
            textPaint.setColor(Color.argb((int) (128.f * (10 - i) / 10), 0, 0, 0));
            canvas.drawText(text, 0, text.length(), x + 1, y, textPaint);
            canvas.drawText(text, 0, text.length(), x, y + 1, textPaint);
            canvas.drawText(text, 0, text.length(), x - 1, y, textPaint);
            canvas.drawText(text, 0, text.length(), x, y - 1, textPaint);
        }

        return bitmap;
    }

    public static Point mapFitCenterImagePointToBitmapPoint(float touchX,
                                                            float touchY,
                                                            int componentWidth,
                                                            int componentHeight,
                                                            Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleX = (float) componentWidth / width;
        float scaleY = (float) componentHeight / height;
        float scale = Math.min(scaleX, scaleY);

        float newWidth = scale * width;
        float newHeight = scale * height;

        float x = (componentWidth - newWidth) / 2;
        float y = (componentHeight - newHeight) / 2;

        touchX -= x;
        touchY -= y;

        return new Point((int) (touchX / scale), (int) (touchY / scale));
    }

    public static void post(Runnable runnable) {
        handler.post(runnable);
    }
}
