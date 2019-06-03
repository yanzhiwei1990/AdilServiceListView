package zhiwei.adilservice;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FavListItem extends Item {
    private static final String TAG = FavListItem.class.getSimpleName();

    private Context mContext;
    private String mName;
    private boolean needShowIcon = false;
    private String mInfoJson;
    private JSONObject mJSONObject;

    public FavListItem(Context context, String name, boolean show, String objJson) {
        super(FavListItem.class.getSimpleName());
        this.mContext = context;
        this.mName = name;
        this.needShowIcon = show;
        this.mInfoJson = objJson;
        this.initJSONObject(objJson);
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
        return R.layout.item_favlist_layout;
    }

    @Override
    protected String getTitle() {
        return mName;
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
    protected boolean isNeedShowIcon() {
        return needShowIcon;
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

    public int getFavId() {
        int result = -1;
        try {
            if (!TextUtils.isEmpty(mInfoJson)) {
                JSONObject obj =  new JSONObject(mInfoJson);
                if (obj != null && obj.length() > 0) {
                    result = obj.getInt(ChannelDataManager.KEY_SETTINGS_FAV_INDEX);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "getChannelId e = " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public boolean isAllFavList() {
        boolean result = false;
        try {
            if (mJSONObject != null && mJSONObject.length() > 0) {
                result = mJSONObject.getBoolean(ChannelDataManager.KEY_SETTINGS_IS_ALL_FAV_LIST);
            }
        } catch (JSONException e) {
            Log.e(TAG, "isAllFavList e = " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}
