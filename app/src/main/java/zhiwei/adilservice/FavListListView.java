package zhiwei.adilservice;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class FavListListView extends CustomedListView {
    private static final String TAG = FavListListView.class.getSimpleName();

    public FavListListView(Context context) {
        this(context, null);
    }

    public FavListListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FavListListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public FavListListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes, FavListListView.class.getSimpleName());
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {

        //super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            super.onFocusChanged(false, direction, previouslyFocusedRect);
            View view = getChildAt(0);
            if  (view != null && view instanceof View) {
                setSelection(0);
            }
        } else {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }
    }
}
