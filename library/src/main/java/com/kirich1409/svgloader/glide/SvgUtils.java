package com.kirich1409.svgloader.glide;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@RestrictTo(RestrictTo.Scope.LIBRARY)
final class SvgUtils {

    public static void fix(@NonNull SVG svg) {
        if (svg.getDocumentViewBox() == null) {
            svg.setDocumentViewBox(0F, 0F, svg.getDocumentWidth(), svg.getDocumentHeight());
        }
    }

    public static SVG getSvg(@NonNull File file) throws SVGParseException, IOException {
        if (!file.exists()) {
            throw new FileNotFoundException("File: '" + file.getAbsolutePath() + "' not exists");
        }

        try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
            return SVG.getFromInputStream(is);
        }
    }

    public static SVG getSvg(@NonNull FileDescriptor descriptor) throws SVGParseException, IOException {
        try (InputStream is = new BufferedInputStream(new FileInputStream(descriptor))) {
            return SVG.getFromInputStream(is);
        }
    }

    public static void scaleDocumentSize(
            @NonNull SVG svg,
            @FloatRange(from = 0, fromInclusive = false) float scale
    ) {
        svg.setDocumentWidth(svg.getDocumentWidth() * scale);
        svg.setDocumentHeight(svg.getDocumentHeight() * scale);
    }

    @NonNull
    public static Bitmap toBitmap(
            @NonNull SVG svg, @NonNull BitmapProvider provider, @NonNull Bitmap.Config config) {
        int outImageWidth = Math.round(svg.getDocumentWidth());
        int outImageHeight = Math.round(svg.getDocumentHeight());
        Bitmap bitmap = provider.get(outImageWidth, outImageHeight, config);
        Canvas canvas = new Canvas(bitmap);
        svg.renderToCanvas(canvas);
        return bitmap;
    }

    @NonNull
    public static Bitmap toBitmap(@NonNull SVG svg, @NonNull BitmapProvider provider) {
        return toBitmap(svg, provider, Bitmap.Config.ARGB_8888);
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    public interface BitmapProvider {

        @NonNull
        Bitmap get(@IntRange(from = 0) int width, @IntRange(from = 0) int height, @NonNull Bitmap.Config config);
    }

    private SvgUtils() {
    }
}
