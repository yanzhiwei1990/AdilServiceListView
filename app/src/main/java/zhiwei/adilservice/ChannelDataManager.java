package zhiwei.adilservice;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ChannelDataManager {
    private static final String TAG = "ChannelDataManager";
    private Context mContext;
    private ContentResolver mContentResolver;
    private static final String KEY_SETTINGS_FAVLIST = "settings_favlist";
    private static final String KEY_SETTINGS_CHANNELLIST = "settings_channellist";

    public ChannelDataManager(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
    }

    public List<String> initTestChannelList() {
        List<String> result = new ArrayList<String>();
        JSONArray array = new JSONArray();
        String DEFAULTNAME = "Channel";
        JSONObject childObj;
        byte[] A = new byte[1];
        try {
            for (int i = 0; i < 26; i++) {
                A[0] = (byte)('A' + (byte)i);//(byte)'A' + (byte)i;
                childObj = new JSONObject();
                childObj.put("channel_name", (new String(A)) + DEFAULTNAME + String.valueOf(2 * i));
                childObj.put("frequency", (2 * i + 1) * 10);
                childObj.put("network_id", i + 1);
                childObj.put("is_favourite", false);
                childObj.put("original_index", i);
                result.add(childObj.toString());
                array.put(childObj);
                childObj = new JSONObject();
                childObj.put("channel_name", (new String(A)) + DEFAULTNAME + String.valueOf(2 * i + 1));
                childObj.put("frequency", (2 * i + 2) * 10);
                childObj.put("network_id", i + 1);
                childObj.put("original_index", i);
                childObj.put("is_favourite", true);
                result.add(childObj.toString());
                array.put(childObj);
            }
        } catch (JSONException e) {
            Log.d(TAG, "init64FavList JSONException = " + e);
            e.printStackTrace();
        }
        if (array != null && array.length() > 0) {
            saveStringToXml(KEY_SETTINGS_CHANNELLIST, array.toString());
        }
        return result;
    }

    public List<String> getChannelList(String inputId) {
        List<String> result = new ArrayList<String>();
        String jsonStr = getStringFromSettings(KEY_SETTINGS_CHANNELLIST);
        if (TextUtils.isEmpty(jsonStr)) {
            result = initTestChannelList();
        } else {
            result = produceListFromJsonStr(jsonStr);
        }
        return result;
    }

    public LinkedList<Item> getChannelListItem(String inputId) {
        LinkedList<Item> result = new LinkedList<Item>();
        List<String> list = getChannelList(inputId);
        if (list != null && list.size() > 0) {
            Iterator<String> iterator = list.iterator();
            JSONObject jsonObj = null;
            Item item = null;
            while (iterator.hasNext()) {
                String objStr = (String)iterator.next();
                if (!TextUtils.isEmpty(objStr)) {
                    try {
                        jsonObj = new JSONObject(objStr);
                        if (jsonObj != null && jsonObj.length() > 0) {
                            item = new ChannelListItem(mContext, (String)jsonObj.getString("channel_name"), (boolean)jsonObj.getBoolean("is_favourite"));
                            result.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public String genarateUpdatedChannelListJsonSrt(final List<String> all, int index, String oneItem) {
        String result = "{}";
        if (all != null && all.size() > 0 && index > 0 && !TextUtils.isEmpty(oneItem) && !oneItem.equals(result)) {
            String getOriginal = (String)all.get(index);
            if (!TextUtils.equals(oneItem, getOriginal)) {
                all.set(index, oneItem);
            }
            result = convertListToJsonStr(all);
        }
        return result;
    }

    public void updateChannelListChangeToDatabase(String data) {
        saveStringToXml(KEY_SETTINGS_CHANNELLIST, data);
    }

    public List<Integer> getFavInfoFromChannel(String oneChannelFavJsonArray) {
        List<Integer> result = new ArrayList<Integer>();
        try {
            JSONArray array = new JSONArray(oneChannelFavJsonArray);
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    result.add((Integer) array.get(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Returns defalut 64 group of fav list, obj key is "fav_list".
     *
     */
    private JSONArray init64FavList() {
        //JSONObject obj = new JSONObject();
        JSONArray array = new JSONArray();
        String DEFAULTNAME = "Favorite";
        JSONObject childObj;
        try {
            for (int i = 0; i < 64; i++) {
                childObj = new JSONObject();
                childObj.put("name", DEFAULTNAME + String.valueOf(i));
                childObj.put("original_index", i);
                childObj.put("is_favourite", (i % 2 == 0) ? true : false);
                array.put(childObj);
            }
        } catch (JSONException e) {
            Log.d(TAG, "init64FavList JSONException = " + e);
            e.printStackTrace();
        }
        return array;
    }

    private List<String> getInit64FavList() {
        List<String> result = new ArrayList<String>();
        JSONArray initArray = init64FavList();
        if (initArray != null && initArray.length() > 0) {
            saveStringToXml(KEY_SETTINGS_FAVLIST, initArray.toString());
        }
        if (initArray != null && initArray.length() > 0) {
            for (int i = 0; i < initArray.length(); i++) {
                try {
                    result.add(((JSONObject)initArray.get(i)).getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public LinkedList<Item> getFavListItem() {
        LinkedList<Item> result = new LinkedList<Item>();
        JSONArray initArray = init64FavList();
        if (initArray != null && initArray.length() > 0) {
            saveStringToXml(KEY_SETTINGS_FAVLIST, initArray.toString());
        }
        if (initArray != null && initArray.length() > 0) {
            Item item = null;
            for (int i = 0; i < initArray.length(); i++) {
                try {
                    JSONObject obj = (JSONObject)initArray.get(i);
                    item = new FavListItem(mContext, obj.getString("name"), obj.getBoolean("is_favourite"));
                    result.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public List<String> getFavList() {
        List<String> result = new ArrayList<String>();
        JSONArray array = null;
        String jsonStr = getStringFromSettings(KEY_SETTINGS_FAVLIST);
        if (TextUtils.isEmpty(jsonStr)) {
            result = getInit64FavList();
        } else {
            result = produceListFromJsonStr(jsonStr);
        }
        return result;
    }

    private List<String> produceListFromJsonStr(String json) {
        List<String> result = new ArrayList<String>();
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray array = new JSONArray(json);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        result.add(((JSONObject)array.get(i)).toString());
                    }
                }
            } catch (JSONException e) {
                Log.d(TAG, "produceListFromJsonStr JSONException = " + e);
                e.printStackTrace();
            }
        }
        return result;
    }

    private String convertListToJsonStr(final List<String> list) {
        String result = "{}";
        Iterator<String> iterator = list.iterator();
        JSONArray array = new JSONArray();
        String item = null;
        while (iterator.hasNext()) {
            item = (String)iterator.next();
            array.put(item);
        }
        if (array != null && array.length() > 0) {
            result = array.toString();
        }
        return result;
    }

    public boolean putStringToSettings(String name, String value) {
        return Settings.System.putString(mContentResolver, name, value);
    }

    public String getStringFromSettings(String name) {
        return Settings.System.getString(mContentResolver, name);

    }

    public void saveStringToXml(String key, String jsonValue) {
        SharedPreferences userSettings = mContext.getSharedPreferences("channel_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(key, jsonValue);
        editor.commit();
    }

    public String getStringFromXml(String key, String defValue) {
        String result = null;
        SharedPreferences userSettings = mContext.getSharedPreferences("channel_info", Context.MODE_PRIVATE);
        result = userSettings.getString(key, defValue);
        return result;
    }
}
