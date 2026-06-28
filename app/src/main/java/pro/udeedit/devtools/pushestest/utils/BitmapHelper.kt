package pro.udeedit.devtools.pushestest.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap

/**
 * Utility function to convert Android [Drawable] resources into [Bitmap] objects.
 *
 * Technical Requirement:
 * The Android Notification API (specifically LargeIcon and BigPictureStyle)
 * strictly requires rasterized [Bitmap] data. This helper ensures that modern
 * XML Vector Drawables are correctly rendered onto a [Canvas] before being
 * passed to the system notification service.
 *
 * @param context The application or activity context.
 * @param drawableId The resource ID of the drawable to convert.
 * @param tintColor Optional color int to apply as a tint to the drawable before conversion.
 * @return A high-quality [Bitmap] representation of the resource, or null if the resource is invalid.
 */
fun getBitmapFromDrawable(context: Context, drawableId: Int, tintColor: Int? = null): Bitmap? {
    // Retrieve the drawable and mutate it to ensure tinting doesn't affect other instances
    val drawable = ContextCompat.getDrawable(context, drawableId)?.mutate() ?: return null

    // Apply the tint to the vector before drawing it to the canvas
    tintColor?.let {
        androidx.core.graphics.drawable.DrawableCompat.setTint(drawable, it)
    }

    // Optimization: If the resource is already a rasterized Bitmap (PNG/JPG),
    // return the underlying bitmap directly to save memory and CPU cycles.
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    // For Vectors/XML: Create a new Bitmap using ARGB_8888 config
    // to ensure maximum compatibility and transparency support.
    val bitmap = createBitmap(
        drawable.intrinsicWidth.coerceAtLeast(1),
        drawable.intrinsicHeight.coerceAtLeast(1),
        Bitmap.Config.ARGB_8888
    )

    // Render the drawable onto the bitmap canvas
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}
