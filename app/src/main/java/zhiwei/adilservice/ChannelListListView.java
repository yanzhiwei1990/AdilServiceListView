package zhiwei.adilservice;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class ChannelListListView extends CustomedListView {
    private static final String TAG = ChannelListListView.class.getSimpleName();

    public ChannelListListView(Context context) {
        this(context, null);
    }

    public ChannelListListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChannelListListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, defStyleAttr);
    }

    public ChannelListListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes, ChannelListListView.class.getSimpleName());
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        int lastSelectItem = getSelectedItemPosition();
        //super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            super.onFocusChanged(false, direction, previouslyFocusedRect);
            //setSelection(lastSelectItem);
            //ChannelListItem item = (ChannelListItem) getSelectedItem();
            View view = getChildAt(lastSelectItem);
            if  (view != null && view instanceof View) {
                view.requestFocus();
            }
        } else {
            super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        }
    }
}
