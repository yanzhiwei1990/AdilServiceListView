package zhiwei.adilservice;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Item {
    private static final String TAG = "Item";
    private String mItemName;

    public Item(String name) {
        this.mItemName = name;
    }

    protected abstract int getResourceId();
    protected abstract String getTitle();
    protected abstract boolean needTitle();
    protected abstract Drawable getDrawable();
    protected abstract boolean needIconDrawable();
    protected abstract String getLayoutIdInfo();
    protected abstract JSONObject getJSONObject();

    protected String getStringValueFromJSONObject(String key) {
        String result = null;
        JSONObject obj = getJSONObject();
        if (!TextUtils.isEmpty(key) && obj != null && obj.length() > 0) {
            try {
                String value = obj.getString(key);
                result = value;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    protected int getIntValueFromJSONObject(String key) {
        int result = -1;
        JSONObject obj = getJSONObject();
        if (!TextUtils.isEmpty(key) && obj != null && obj.length() > 0) {
            try {
                int value = obj.getInt(key);
                result = value;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    protected boolean isNeedShowIcon() {
        return false;
    }

    protected int getTitleId() {
        return R.id.title_text;
    }

    protected  int getIconTextId() {
        return R.id.icon_text;
    }

    protected String addTextView(int resId, String value) {
        String result = null;
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", TextView .class);
            obj.put("resId", resId);
            obj.put("value", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (obj != null && obj.length() > 0) {
            result = obj.toString();
        }
        return result;
    }

    protected String addIconTextView(int resId, int iconRes) {
        String result = null;
        JSONObject obj = new JSONObject();
        try {
            obj.put("type", TextView .class);
            obj.put("resId", resId);
            obj.put("iconRes", iconRes);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (obj != null && obj.length() > 0) {
            result = obj.toString();
        }
        return result;
    }
}
