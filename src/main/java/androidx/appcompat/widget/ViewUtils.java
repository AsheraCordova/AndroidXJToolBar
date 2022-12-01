package androidx.appcompat.widget;

import androidx.core.view.ViewCompat;
import r.android.view.View;

public class ViewUtils {
    private ViewUtils() {}

    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }
}
