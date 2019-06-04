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
    public static final String KEY_SETTINGS_CHANNEL_NAME = "channel_name";
    public static final String KEY_SETTINGS_CHANNEL_ID = "channel_id";
    public static final String KEY_SETTINGS_CHANNEL_FREQUENCY = "channel_frequency";
    public static final String KEY_SETTINGS_CHANNEL_NETWORK_ID = "network_id";
    public static final String KEY_SETTINGS_CHANNEL_IS_FAVOURITE = "is_favourite";
    public static final String KEY_SETTINGS_CHANNEL_JSONOBJ = "channel_jsonobj";
    public static final String KEY_SETTINGS_CHANNEL_FAV_INDEX = "fav_index";
    public static final String KEY_SETTINGS_FAV_NAME = "fav_name";
    public static final String KEY_SETTINGS_FAV_INDEX = "original_index";
    public static final String KEY_SETTINGS_IS_ALL_FAV_LIST = "is_all_fav_list";
    public static final String KEY_SETTINGS_FAV_IS_ADDED = "is_added";

    public ChannelDataManager(Context context) {
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
    }

    private List<String> initTestChannelList() {
        List<String> result = new ArrayList<String>();
        JSONArray array = new JSONArray();
        String DEFAULTNAME = "Channel";
        JSONObject childObj;
        JSONObject childTmpObj;
        byte[] A = new byte[1];
        try {
            for (int i = 0; i < 26; i++) {
                A[0] = (byte)('A' + (byte)i);//(byte)'A' + (byte)i;
                childObj = new JSONObject();
                childObj.put(KEY_SETTINGS_CHANNEL_NAME, (new String(A)) + DEFAULTNAME + String.valueOf(2 * i));
                childObj.put(KEY_SETTINGS_CHANNEL_FREQUENCY, (2 * i + 1) * 10);
                childObj.put(KEY_SETTINGS_CHANNEL_NETWORK_ID, i + 1);
                childObj.put(KEY_SETTINGS_CHANNEL_IS_FAVOURITE, false);
                childObj.put(KEY_SETTINGS_CHANNEL_FAV_INDEX, new JSONArray().toString());
                childObj.put(KEY_SETTINGS_CHANNEL_ID, (long)(2 * i));
                result.add(childObj.toString());
                array.put(childObj);
                childObj = new JSONObject();
                childObj.put(KEY_SETTINGS_CHANNEL_NAME, (new String(A)) + DEFAULTNAME + String.valueOf(2 * i + 1));
                childObj.put(KEY_SETTINGS_CHANNEL_FREQUENCY, (2 * i + 2) * 10);
                childObj.put(KEY_SETTINGS_CHANNEL_NETWORK_ID, i + 1);
                childObj.put(KEY_SETTINGS_CHANNEL_IS_FAVOURITE, false);
                childObj.put(KEY_SETTINGS_CHANNEL_FAV_INDEX, new JSONArray().toString());
                childObj.put(KEY_SETTINGS_CHANNEL_ID, (long)(2 * i + 1));
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
        String jsonStr = getStringFromXml(KEY_SETTINGS_CHANNELLIST, null);
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
                        //Log.d(TAG, "getChannelListItem jsonObj = " + jsonObj.toString());
                        if (jsonObj != null && jsonObj.length() > 0) {
                            String favArrayStr = jsonObj.getString(KEY_SETTINGS_CHANNEL_FAV_INDEX);
                            JSONArray favArray = new JSONArray(favArrayStr);
                            boolean isFaved = false;
                            if (favArray != null && favArray.length() > 0) {
                                isFaved = true;
                            }
                            item = new ChannelListItem(mContext, jsonObj.getString(KEY_SETTINGS_CHANNEL_NAME), isFaved, jsonObj.toString());
                            result.add(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.d(TAG, "getChannelListItem result size = " + result.size());
        return result;
    }

    public String genarateUpdatedChannelListJsonSrt(final List<String> all, long channelId, String favArrayString) {
        String result = null;
        if (all != null && all.size() > 0 && channelId > -1) {
            int count = 0;
            Iterator<String> iterator = all.iterator();
            while (iterator.hasNext()) {
                String value = (String)iterator.next();
                try {
                    JSONObject obj = new JSONObject(value);
                    if (channelId == obj.getLong(KEY_SETTINGS_CHANNEL_ID)) {
                        if (!TextUtils.equals(favArrayString, obj.getString(KEY_SETTINGS_CHANNEL_FAV_INDEX))) {
                            obj.put(KEY_SETTINGS_CHANNEL_FAV_INDEX, favArrayString);
                            Log.d(TAG, "genarateUpdatedChannelListJsonSrt count = " + count + ", name = " + obj.getString(KEY_SETTINGS_CHANNEL_NAME));
                            all.set(count, obj.toString());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                count++;
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
                childObj.put(KEY_SETTINGS_FAV_NAME, DEFAULTNAME + String.valueOf(i));
                childObj.put(KEY_SETTINGS_FAV_INDEX, i);
                childObj.put(KEY_SETTINGS_FAV_IS_ADDED, false);
                childObj.put(KEY_SETTINGS_IS_ALL_FAV_LIST, true);
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
            Log.d(TAG, "getInit64FavList size = " + initArray.length());
            for (int i = 0; i < initArray.length(); i++) {
                try {
                    result.add(initArray.getJSONObject(i).getString(KEY_SETTINGS_FAV_NAME));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public List<String> getFavList(String inputId) {
        List<String> result = new ArrayList<String>();
        String jsonStr = getStringFromXml(KEY_SETTINGS_FAVLIST, null);
        if (TextUtils.isEmpty(jsonStr)) {
            result = getInit64FavList();
        } else {
            result = produceListFromJsonStr(jsonStr);
        }
        return result;
    }

    public LinkedList<Item> getFavListItem() {
        LinkedList<Item> result = new LinkedList<Item>();
        JSONArray initArray = null;
        String jsonStr = getStringFromXml(KEY_SETTINGS_FAVLIST, null);
        if (TextUtils.isEmpty(jsonStr)) {
            initArray = init64FavList();
            if (initArray != null && initArray.length() > 0) {
                saveStringToXml(KEY_SETTINGS_FAVLIST, initArray.toString());
            }
        } else {
            try {
                initArray = new JSONArray(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (initArray != null && initArray.length() > 0) {
            Log.d(TAG, "getFavListItem length = " + initArray.length());
            Item item = null;
            for (int i = 0; i < initArray.length(); i++) {
                try {
                    JSONObject obj = (JSONObject)initArray.get(i);
                    //Log.d(TAG, "getFavListItem jsonObj = " + obj.toString());
                    item = new FavListItem(mContext, obj.getString(KEY_SETTINGS_FAV_NAME), obj.getBoolean(KEY_SETTINGS_FAV_IS_ADDED), obj.toString());
                    result.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private List<String> getFavList() {
        List<String> result = new ArrayList<String>();
        JSONArray array = null;
        String jsonStr = getStringFromXml(KEY_SETTINGS_FAVLIST, null);
        if (TextUtils.isEmpty(jsonStr)) {
            result = getInit64FavList();
        } else {
            result = produceListFromJsonStr(jsonStr);
        }
        return result;
    }

    public LinkedList<Item> getChannelFavListItem(ChannelListItem chanelItem) {
        LinkedList<Item> result = new LinkedList<Item>();
        JSONArray initArray = null;
        String jsonStr = getStringFromXml(KEY_SETTINGS_FAVLIST, null);
        if (TextUtils.isEmpty(jsonStr)) {
            initArray = init64FavList();
            if (initArray != null && initArray.length() > 0) {
                saveStringToXml(KEY_SETTINGS_FAVLIST, initArray.toString());
            }
        } else {
            try {
                initArray = new JSONArray(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (initArray != null && initArray.length() > 0) {
            Log.d(TAG, "getChannelFavListItem length = " + initArray.length());
            Item item = null;
            List<Integer> channelFav = chanelItem.getFavAllIndex();
            for (int i = 0; i < initArray.length(); i++) {
                try {
                    JSONObject obj = (JSONObject)initArray.get(i);
                    obj.put(KEY_SETTINGS_IS_ALL_FAV_LIST, false);
                    //Log.d(TAG, "getChannelFavListItem jsonObj = " + obj.toString());
                    boolean isFavSelected = false;
                    if (channelFav != null && channelFav.size() > 0) {
                        if (channelFav.indexOf(Integer.valueOf(i)) > -1) {
                            isFavSelected = true;
                        }
                    }
                    item = new FavListItem(mContext, obj.getString(KEY_SETTINGS_FAV_NAME), isFavSelected, obj.toString());
                    result.add(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public LinkedList<Item> getChannelItemByFavPage(int favId) {
        LinkedList<Item> result = new LinkedList<Item>();
        List<String> list = getChannelList("");
        if (list != null && list.size() > 0) {
            Iterator<String> iterator = list.iterator();
            JSONObject obj = null;
            JSONArray array = null;
            String channelObjjsonSrt = null;
            ChannelListItem oneItem = null;
            boolean hasFav = false;
            while (iterator.hasNext()) {
                hasFav = false;
                channelObjjsonSrt = (String)iterator.next();
                try {
                    obj = new JSONObject(channelObjjsonSrt);
                    if (obj != null && obj.length() > 0) {
                        array = new JSONArray(obj.getString(KEY_SETTINGS_CHANNEL_FAV_INDEX));
                    }
                    if (array != null && array.length() > 0) {
                        for (int i = 0; i < array.length(); i++) {
                            if (favId == (int)array.get(i)) {
                                hasFav = true;
                                oneItem = new ChannelListItem(mContext, obj.getString(KEY_SETTINGS_CHANNEL_NAME), hasFav, obj.toString());
                                result.add(oneItem);
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public void updateFavListChangeToDatabase(String data) {
        saveStringToXml(KEY_SETTINGS_FAVLIST, data);
    }

    private List<String> produceListFromJsonStr(String json) {
        List<String> result = new ArrayList<String>();
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONArray array = new JSONArray(json);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        result.add(array.getString(i));
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
        String result = null;
        Iterator<String> iterator = list.iterator();
        JSONArray array = new JSONArray();
        String item = null;
        while (iterator.hasNext()) {
            item = (String)iterator.next();
            Log.d(TAG, "convertListToJsonStr item = " + item);
            array.put(item);
        }
        if (array != null && array.length() > 0) {
            result = array.toString();
        }
        return result;
    }

    /*public boolean putStringToSettings(String name, String value) {
        return Settings.System.putString(mContentResolver, name, value);
    }

    public String getStringFromSettings(String name) {
        return Settings.System.getString(mContentResolver, name);

    }*/

    public void saveStringToXml(String key, String jsonValue) {
        SharedPreferences userSettings = mContext.getSharedPreferences("channel_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.putString(key, String.valueOf(jsonValue));
        editor.commit();
    }

    public String getStringFromXml(String key, String defValue) {
        String result = null;
        SharedPreferences userSettings = mContext.getSharedPreferences("channel_info", Context.MODE_PRIVATE);
        result = userSettings.getString(key, defValue);
        return result;
    }
}
