package zhiwei.adilservice;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ChannelListItem extends Item {
    private static final String TAG = ChannelListItem.class.getSimpleName();

    private Context mContext;
    private String mName;
    private boolean mNeedShowIcon = false;
    private String mInfoJson;
    private JSONObject mJSONObject;

    public ChannelListItem(Context context, String name, boolean needShow, String infoJson) {
        super(ChannelListItem.class.getSimpleName());
        this.mContext = context;
        this.mName = name;
        this.mNeedShowIcon = needShow;
        this.mInfoJson = infoJson;
        this.initJSONObject(infoJson);
    }

    private void initJSONObject(String json) {
        try {
            if (!TextUtils.isEmpty(json)) {
                mJSONObject =  new JSONObject(json);
            }
        } catch (JSONException e) {
            Log.e(TAG, "initJSONObject e = " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject getJSONObject() {
        return mJSONObject;
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
        return mNeedShowIcon;
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

    public List<Integer> getFavAllIndex() {
        List<Integer> result = new ArrayList<Integer>();
        try {
            if (!TextUtils.isEmpty(mInfoJson)) {
                JSONObject obj =  new JSONObject(mInfoJson);
                if (obj != null && obj.length() > 0) {
                    JSONArray array = obj.getJSONArray("fav_index");
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            result.add(array.getInt(i));
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "getFavAllIndex e = " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
