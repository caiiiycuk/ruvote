package com.github.caiiiycuk.ruvote.screen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.litho.ClickEvent;
import com.facebook.litho.Column;
import com.facebook.litho.Component;
import com.facebook.litho.ComponentContext;
import com.facebook.litho.Row;
import com.facebook.litho.StateValue;
import com.facebook.litho.annotations.LayoutSpec;
import com.facebook.litho.annotations.OnAttached;
import com.facebook.litho.annotations.OnCreateLayout;
import com.facebook.litho.annotations.OnDetached;
import com.facebook.litho.annotations.OnEvent;
import com.facebook.litho.annotations.OnUpdateState;
import com.facebook.litho.annotations.Param;
import com.facebook.litho.annotations.Prop;
import com.facebook.litho.annotations.State;
import com.facebook.litho.widget.Image;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaEdge;
import com.facebook.yoga.YogaJustify;
import com.facebook.yoga.YogaPositionType;
import com.github.caiiiycuk.ruvote.R;
import com.github.caiiiycuk.ruvote.activity.Router;
import com.github.caiiiycuk.ruvote.cv.ROI;
import com.github.caiiiycuk.ruvote.cv.ROICalculator;
import com.github.caiiiycuk.ruvote.ui.Ui;
import com.github.caiiiycuk.ruvote.ui.widget.ProgressWheel;
import com.github.caiiiycuk.ruvote.ui.widget.Title;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.Executor;

@LayoutSpec
public class ResultScreenSpec {

    @OnCreateLayout
    static Component onCreateLayout(ComponentContext c,
                                    @Prop Bitmap bitmap) {
        return Column.create(c)
                .child(Title.create(c)
                        .textRes(R.string.select_method)
                        .build())
                .child(Image.create(c)
                        .flexGrow(1)
                        .scaleType(ImageView.ScaleType.FIT_CENTER)
                        .drawable(new BitmapDrawable(c.getResources(), bitmap))
                        .build())
                .child(Image.create(c)
                        .background(Ui.circle(R.color.colorPrimaryDark))
                        .paddingRes(YogaEdge.ALL, R.dimen.ident)
                        .drawableRes(android.R.drawable.ic_media_ff)
                        .widthRes(R.dimen.icon_size)
                        .aspectRatio(1.0f)
                        .scaleType(ImageView.ScaleType.FIT_CENTER)
                        .positionType(YogaPositionType.ABSOLUTE)
                        .positionPercent(YogaEdge.RIGHT, 10)
                        .positionPercent(YogaEdge.BOTTOM, 10)
                        .clickHandler(ResultScreen.onShare(c))
                        .build())
                .build();
    }

    @OnEvent(ClickEvent.class)
    static void onShare(ComponentContext c,
                        @Prop Bitmap bitmap) {
        Context context = c.getAndroidContext();
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File targetFile = new File(picturesDir, "IMG_" + System.currentTimeMillis() + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(targetFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 65, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Ui.post(() -> Toast.makeText(context, "Unable to save file: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            return;
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/jpg");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + targetFile.getAbsolutePath()));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        ContextCompat.startActivity(context,
                Intent.createChooser(intent, "Share"), null);
    }

}