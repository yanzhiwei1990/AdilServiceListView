package zhiwei.adilservice;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

public class ItemAdapter extends CommonAdapter<Item> {
    private static final String TAG = "ItemAdapter";

    private LinkedList<Item> mData;
    private Context mContext;

    public ItemAdapter(LinkedList<Item> data, Context context) {
        super(data, context);
        this.mData = data;
        this.mContext = context;
    }

    @Override
    public CommonViewHolder setViewContent(Context context, View view, ViewGroup parent, int position, Item item) {
        CommonViewHolder viewHolder = CommonViewHolder.get(context, view, parent, item.getResourceId(), position);
        int titleTextResId = item.getTitleId();
        int iconTextResId = item.getIconTextId();
        String title = item.getTitle();
        Drawable iconDrawable = item.getDrawable();
        //Log.d(TAG, "setViewContent fixed titleTextResId = " + titleTextResId + ", iconTextResId = " + iconTextResId + ", item = " + item + ", iconDrawable = " + iconDrawable);
        //add extend intreface
        try {
            JSONArray array = new JSONArray(item.getLayoutIdInfo());
            if (array != null && array.length() > 1) {
                int viewNum = (int)array.get(0);
                if (viewNum >= 1) {
                    titleTextResId = ((JSONObject)array.get(1)).getInt("resId");
                    title = ((JSONObject)array.get(1)).getString("value");
                    //Log.d(TAG, "setViewContent parsed titleTextResId = " + titleTextResId + ", title = " + title);
                }
                if (viewNum >= 2) {
                    JSONObject obj = (JSONObject)array.get(2);
                    iconTextResId = obj.getInt("resId");
                    iconDrawable = mContext.getDrawable(obj.getInt("iconRes"));
                    //Log.d(TAG, "setViewContent parsed iconTextResId = " + iconTextResId + ", iconDrawable = " + iconDrawable);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (item.needTitle()) {
            viewHolder.setText(titleTextResId, title);
        }
        if (item.needIconDrawable()) {
            //Log.d(TAG, "setViewContent need icon");
            if (item.isNeedShowIcon()) {
                //Log.d(TAG, "setViewContent display icon");
                viewHolder.setIcon(iconTextResId, iconDrawable);
            } else {
                //Log.d(TAG, "setViewContent hide icon");
                viewHolder.setIcon(iconTextResId, null);
            }
        }
        return viewHolder;
    }
}
