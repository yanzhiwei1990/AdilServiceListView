package zhiwei.adilservice;


import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.LinkedList;

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

    public void updateItem(int position, Item item) {
        ItemAdapter adapter = (ItemAdapter)this.getAdapter();
        if (adapter == null) {
            Log.d(TAG, "updateItem null return");
            return;
        }
        adapter.setDataByPosition(position,item);
        int firstVisiblePosition = this.getFirstVisiblePosition();
        int lastVisiblePosition = this.getLastVisiblePosition();
        if (position >= firstVisiblePosition && position <= lastVisiblePosition) {
            View view = this.getChildAt(position - firstVisiblePosition);
            adapter.getView(position, view, this);
        }
    }

    public void updateAllItem(Context context, LinkedList<Item> data) {
        ItemAdapter adapter = (ItemAdapter)this.getAdapter();
        if (adapter == null) {
            adapter = new ItemAdapter(data, context, TAG);
            setAdapter(adapter);
            Log.d(TAG, "updateAllItem init adapter");
        }
        ((ItemAdapter)getAdapter()).setAllData(data);
        ((ItemAdapter)getAdapter()).notifyDataSetChanged();
    }
}
