package zhiwei.adilservice;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public abstract class CustomedListView extends ListView {
    private static final String TAG = CustomedListView.class.getSimpleName();

    private String mName;

    public CustomedListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, String name) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mName = name;
    }

    public String getCustomedTile() {
        return mName;
    }
}
