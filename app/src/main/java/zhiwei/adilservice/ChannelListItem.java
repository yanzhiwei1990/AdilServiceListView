package zhiwei.adilservice;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChannelListItem extends Item {
    private static final String TAG = ChannelListItem.class.getSimpleName();

    private Context mContext;
    private String mName;
    private boolean needShowIcon = false;

    public ChannelListItem(Context context, String name, boolean show) {
        super(ChannelListItem.class.getSimpleName());
        this.mContext = context;
        this.mName = name;
        this.needShowIcon = show;
    }

    @Override
    protected int getResourceId() {
        return R.layout.item_channellist_layout;
    }

    @Override
    protected String getTitle() {
        return mName;
    }

    @Override
    protected boolean isNeedShowIcon() {
        return needShowIcon;
    }

    @Override
    protected boolean needTitle() {
        return true;
    }

    @Override
    protected boolean needIconDrawable() {
        return true;
    }

    @Override
    protected Drawable getDrawable() {
        return mContext.getDrawable(R.drawable.ic_star_white);
    }

    @Override
    protected String getLayoutIdInfo() {
        String result = null;
        JSONArray array = new JSONArray();
        JSONObject obj = new JSONObject();
        try {
            array.put(2);//first element represent total number of sub view
            obj = new JSONObject(addTextView(R.id.title_text, mName));
            array.put(obj);
            obj = new JSONObject(addIconTextView(R.id.icon_text, R.drawable.ic_star_white));
            array.put(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (array != null && array.length() > 2) {
            result = array.toString();
        }
        return result;
    }
}
