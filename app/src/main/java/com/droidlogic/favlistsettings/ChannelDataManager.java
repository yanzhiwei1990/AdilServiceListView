package com.droidlogic.favlistsettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ChannelDataManager {
    private static final String TAG = "ChannelDataManager";
    private Context mContext;
    private ContentResolver mContentResolver;
    private static final String KEY_SETTINGS_FAVLIST = "settings_favlist";
    private static final String KEY_SETTINGS_CHANNELLIST = "settings_channellist";
    public static final String KEY_SETTINGS_CHANNEL_NAME = "channel_name";
    public static final String KEY_SETTINGS_CHANNEL_ITEM_TYPE = "item_type";
    public static final String KEY_SETTINGS_CHANNEL_CONTAINER_TYPE = "container_type";
    public static final String KEY_SETTINGS_CHANNEL_ID = "channel_id";
    public static final String KEY_SETTINGS_CHANNEL_FREQUENCY = "channel_frequency";
    public static final String KEY_SETTINGS_CHANNEL_ITEM_KEY = "item_key";
    public static final String KEY_SETTINGS_CHANNEL_TRANSPONDER = "channel_transponder";
    public static final String KEY_SETTINGS_CHANNEL_SATELLITE = "channel_satellite";
    public static final String KEY_SETTINGS_CHANNEL_NETWORK_ID = "network_id";
    public static final String KEY_SETTINGS_CHANNEL_IS_FAVOURITE = "is_favourite";
    public static final String KEY_SETTINGS_CHANNEL_JSONOBJ = "channel_jsonobj";
    public static final String KEY_SETTINGS_CHANNEL_FAV_INDEX = "fav_index";
    public static final String KEY_SETTINGS_FAV_NAME = "fav_name";
    public static final String KEY_SETTINGS_FAV_INDEX = "original_index";
    //public static final String KEY_SETTINGS_IS_ALL_FAV_LIST = "is_all_fav_list";
    public static final String KEY_SETTINGS_FAV_LIST_TYPE = "fav_list_type";
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
                childObj.put(KEY_SETTINGS_CHANNEL_SATELLITE, ((new String(A)) + DEFAULTNAME + i));
                childObj.put(KEY_SETTINGS_CHANNEL_TRANSPONDER, ((new String(A)) + DEFAULTNAME + "T" + i));
                childObj.put(KEY_SETTINGS_CHANNEL_IS_FAVOURITE, false);
                childObj.put(KEY_SETTINGS_CHANNEL_FAV_INDEX, new JSONArray().toString());
                childObj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_ALL);
                childObj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_ALL_CHANNEL);
                childObj.put(KEY_SETTINGS_CHANNEL_ID, (long)(2 * i));
                result.add(childObj.toString());
                array.put(childObj);
                childObj = new JSONObject();
                childObj.put(KEY_SETTINGS_CHANNEL_NAME, (new String(A)) + DEFAULTNAME + String.valueOf(2 * i + 1));
                childObj.put(KEY_SETTINGS_CHANNEL_FREQUENCY, (2 * i + 2) * 10);
                childObj.put(KEY_SETTINGS_CHANNEL_NETWORK_ID, i + 1);
                childObj.put(KEY_SETTINGS_CHANNEL_SATELLITE, ((new String(A)) + DEFAULTNAME + i));
                childObj.put(KEY_SETTINGS_CHANNEL_TRANSPONDER, ((new String(A)) + DEFAULTNAME + "T" + i));
                childObj.put(KEY_SETTINGS_CHANNEL_IS_FAVOURITE, false);
                childObj.put(KEY_SETTINGS_CHANNEL_FAV_INDEX, new JSONArray().toString());
                childObj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_ALL);
                childObj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_ALL_CHANNEL);
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
            Log.d(TAG, "getChannelList init");
        } else {
            result = produceListFromJsonStr(jsonStr);
            Log.d(TAG, "getChannelList from data");
        }
        return result;
    }

    public LinkedList<Item> getChannelListItemWithoutIndex(String inputId) {
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
                        Log.d(TAG, "getChannelListItemWithoutIndex JSONException = " + e);
                        e.printStackTrace();
                    }
                }
            }
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
            int count = 1;
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
                            item = new ChannelListItem(mContext, String.valueOf(count) + "    " + jsonObj.getString(KEY_SETTINGS_CHANNEL_NAME), isFaved, jsonObj.toString());
                            result.add(item);
                            count++;
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "getChannelListItem JSONException = " + e);
                        e.printStackTrace();
                    }
                }
            }
        }
        Log.d(TAG, "getChannelListItem result size = " + result.size());
        return result;
    }



    public LinkedList<Item> getAZSortKeyChannelListItem(String inputId) {
        LinkedList<Item> result = new LinkedList<Item>();
        ChannelListItem item = null;
        byte[] A = new byte[1];
        JSONObject obj = null;

        obj = new JSONObject();
        try {
            obj.put(KEY_SETTINGS_CHANNEL_ITEM_KEY, "ALL");
            obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_AZ);
            obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_KEY);
        } catch (JSONException e) {
            Log.d(TAG, "getAZSortKeyChannelListItem JSONException1 = " + e);
            e.printStackTrace();
        }
        item = new ChannelListItem(mContext, "ALL", false, obj.toString());
        result.add(item);

        for (int i = 0; i < 26; i++) {
            A[0] = (byte)('A' + (byte)i);
            obj = new JSONObject();
            try {
                obj.put(KEY_SETTINGS_CHANNEL_ITEM_KEY, new String(A));
                obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_AZ);
                obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_KEY);
            } catch (JSONException e) {
                Log.d(TAG, "getAZSortKeyChannelListItem JSONException2 = " + e);
                e.printStackTrace();
            }
            item = new ChannelListItem(mContext, new String(A), false, obj.toString());
            result.add(item);
        }
        return result;
    }

    public LinkedList<Item> getAZSortChannelListItemByStartedAlphabet(String inputId, String startedAlphabet) {
        LinkedList<Item> result = new LinkedList<Item>();
        if ("ALL".equals(startedAlphabet)) {
            result = getChannelListItem(inputId);
        } else if (TextUtils.isEmpty(startedAlphabet)) {
            return result;
        }
        LinkedList<Item> list = getChannelListItemWithoutIndex(inputId);
        Iterator<Item> itemList = list.iterator();
        ChannelListItem singleItem = null;
        String singleName = null;
        String singleStartAlphabet = null;
        int count = 1;
        JSONObject obj = null;
        while (itemList.hasNext()) {
            singleItem = null;
            singleName = null;
            singleStartAlphabet = null;
            singleItem = (ChannelListItem)itemList.next();
            singleName = singleItem.getTitle();
            if (!TextUtils.isEmpty(singleName)) {
                if (singleName.length() == 1) {
                    singleStartAlphabet = singleName;
                } else {
                    singleStartAlphabet = singleName.substring(0, 1);
                }
                if (!TextUtils.isEmpty(startedAlphabet) && startedAlphabet.equalsIgnoreCase(singleStartAlphabet)) {
                    obj = singleItem.getJSONObject();
                    try {
                        obj.remove(KEY_SETTINGS_CHANNEL_ITEM_KEY);
                        obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_AZ);
                        obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_CONTENT);
                    } catch (JSONException e) {
                        Log.d(TAG, "getAZSortChannelListItemByStartedAlphabet JSONException = " + e);
                        e.printStackTrace();
                    }
                    singleItem = new ChannelListItem(mContext, (String.valueOf(count) + "    " + singleItem.getTitle()), singleItem.isNeedShowIcon(), obj.toString());
                    result.add(singleItem);
                    count++;
                }
            }
        }
        return result;
    }

    public LinkedList<Item> getTPSortKeyChannelListItem(String inputId) {
        LinkedList<Item> result = new LinkedList<Item>();
        LinkedList<Item> listItem = getChannelListItem(inputId);
        Map<String, String> map = new HashMap<String, String>();
        ChannelListItem item = null;
        String tpName = null;
        JSONObject obj = null;
        Iterator<Item> channelList = listItem.iterator();
        while (channelList.hasNext()) {
            item = (ChannelListItem)channelList.next();
            tpName = item.getTranponder();
            if (!TextUtils.isEmpty(tpName)) {
                map.put(tpName, tpName);
            }
        }
        map = new MapKeyAscendComparator().sortMapByKey(map);
        if (map != null && map.size() > 0) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();

                obj = new JSONObject();
                try {
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_KEY, key);
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_TP);
                    obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_KEY);
                } catch (JSONException e) {
                    Log.d(TAG, "getTPSortKeyChannelListItem JSONException = " + e);
                    e.printStackTrace();
                }

                item = new ChannelListItem(mContext, key, false, obj.toString());
                result.add(item);
            }
        }
        return result;
    }

    public LinkedList<Item> getTPSortChannelListItemByName(String inputId, String name) {
        LinkedList<Item> result = new LinkedList<Item>();
        if (TextUtils.isEmpty(name)) {
            return result;
        }
        LinkedList<Item> list = getChannelListItemWithoutIndex(inputId);
        Iterator<Item> itemList = list.iterator();
        ChannelListItem singleItem = null;
        String singleName = null;
        int count = 1;
        JSONObject obj = null;
        while (itemList.hasNext()) {
            singleItem = (ChannelListItem)itemList.next();
            singleName = singleItem.getTranponder();
            if (!TextUtils.isEmpty(name) && TextUtils.equals(singleName, name)) {
                obj = singleItem.getJSONObject();
                try {
                    obj.remove(KEY_SETTINGS_CHANNEL_ITEM_KEY);
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_TP);
                    obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_CONTENT);
                } catch (JSONException e) {
                    Log.d(TAG, "getTPSortChannelListItemByName JSONException = " + e);
                    e.printStackTrace();
                }
                singleItem = new ChannelListItem(mContext, (String.valueOf(count) + "    " + singleItem.getTitle()), singleItem.isNeedShowIcon(), obj.toString());
                result.add(singleItem);
                count++;
            }
        }
        return result;
    }

    public LinkedList<Item> getSatelliteSortKeyChannelListItem(String inputId) {
        LinkedList<Item> result = new LinkedList<Item>();
        LinkedList<Item> listItem = getChannelListItem(inputId);
        Map<String, String> map = new HashMap<String, String>();
        ChannelListItem item = null;
        String tpName = null;
        JSONObject obj = null;
        Iterator<Item> channelList = listItem.iterator();
        while (channelList.hasNext()) {
            item = (ChannelListItem)channelList.next();
            tpName = item.getSatellite();
            //Log.d(TAG, "getSatelliteSortKeyChannelListItem tpName = " + tpName);
            if (!TextUtils.isEmpty(tpName)) {
                map.put(tpName, tpName);
            }
        }
        map = new MapKeyAscendComparator().sortMapByKey(map);
        if (map != null && map.size() > 0) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();

                obj = new JSONObject();
                try {
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_KEY, key);
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_FUNVTION_SATELLITE);
                    obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_KEY);
                } catch (JSONException e) {
                    Log.d(TAG, "getSatelliteSortKeyChannelListItem JSONException = " + e);
                    e.printStackTrace();
                }

                item = new ChannelListItem(mContext, key, false, obj.toString());
                result.add(item);
            }
        }
        return result;
    }

    public LinkedList<Item> getSatelliteSortChannelListItemByName(String inputId, String name) {
        LinkedList<Item> result = new LinkedList<Item>();
        if (TextUtils.isEmpty(name)) {
            return result;
        }
        LinkedList<Item> list = getChannelListItemWithoutIndex(inputId);
        Iterator<Item> itemList = list.iterator();
        ChannelListItem singleItem = null;
        String singleName = null;
        int count = 1;
        JSONObject obj = null;
        while (itemList.hasNext()) {
            singleItem = (ChannelListItem)itemList.next();
            singleName = singleItem.getSatellite();
            if (!TextUtils.isEmpty(name) && TextUtils.equals(singleName, name)) {
                obj = singleItem.getJSONObject();
                try {
                    obj.remove(KEY_SETTINGS_CHANNEL_ITEM_KEY);
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_FUNVTION_SATELLITE);
                    obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_CONTENT);
                } catch (JSONException e) {
                    Log.d(TAG, "getSatelliteSortChannelListItemByName JSONException = " + e);
                    e.printStackTrace();
                }
                singleItem = new ChannelListItem(mContext, (String.valueOf(count) + "    " + singleItem.getTitle()), singleItem.isNeedShowIcon(), obj.toString());
                result.add(singleItem);
                count++;
            }
        }
        return result;
    }

    public LinkedList<Item> getFrequencySortKeyChannelListItem(String inputId) {
        LinkedList<Item> result = new LinkedList<Item>();
        LinkedList<Item> listItem = getChannelListItem(inputId);
        Map<String, Integer> map = new HashMap<String, Integer>();
        ChannelListItem item = null;
        int frequency = -1;

        Iterator<Item> channelList = listItem.iterator();
        while (channelList.hasNext()) {
            item = (ChannelListItem)channelList.next();
            frequency = item.getFrequency();
            if (frequency > 0) {
                map.put(String.valueOf(frequency), frequency);
            }
        }
        map = new MapValueAscendComparator().sortMapByValue(map);
        JSONObject obj = new JSONObject();
        if (map != null && map.size() > 0) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                obj = new JSONObject();
                try {
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_KEY, key);
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_TP);
                    obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_KEY);
                } catch (JSONException e) {
                    Log.d(TAG, "getFrequencySortKeyChannelListItem JSONException = " + e);
                    e.printStackTrace();
                }
                item = new ChannelListItem(mContext, key + "MHz", false, obj.toString());
                result.add(item);
            }
        }
        return result;
    }

    public LinkedList<Item> getFrequencySortChannelListItemByFrequrncy(String inputId, int freq) {
        LinkedList<Item> result = new LinkedList<Item>();
        LinkedList<Item> list = getChannelListItemWithoutIndex(inputId);
        Iterator<Item> itemList = list.iterator();
        ChannelListItem singleItem = null;
        int singleFreq = -1;
        int count = 1;
        JSONObject obj = null;
        while (itemList.hasNext()) {
            singleItem = (ChannelListItem)itemList.next();
            singleFreq = singleItem.getFrequency();
            if (freq == singleFreq) {
                obj = singleItem.getJSONObject();
                try {
                    obj.remove(KEY_SETTINGS_CHANNEL_ITEM_KEY);
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_TP);
                    obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_CONTENT);
                } catch (JSONException e) {
                    Log.d(TAG, "getFrequencySortChannelListItemByFrequrncy JSONException = " + e);
                    e.printStackTrace();
                }
                singleItem = new ChannelListItem(mContext, (String.valueOf(count) + "    " + singleItem.getTitle()), singleItem.isNeedShowIcon(), obj.toString());
                result.add(singleItem);
                count++;
            }
        }
        return result;
    }

    public LinkedList<Item> getOperatorSortKeyChannelListItem(String inputId) {
        LinkedList<Item> result = new LinkedList<Item>();
        LinkedList<Item> listItem = getChannelListItem(inputId);
        Map<String, Integer> map = new HashMap<String, Integer>();
        ChannelListItem item = null;
        int networkId = -1;
        JSONObject obj = null;
        Iterator<Item> channelList = listItem.iterator();
        while (channelList.hasNext()) {
            item = (ChannelListItem)channelList.next();
            networkId = item.getNetworkId();
            map.put(String.valueOf(networkId), networkId);
        }
        map = new MapValueAscendComparator().sortMapByValue(map);
        if (map != null && map.size() > 0) {
            Iterator<String> iterator = map.keySet().iterator();
            while (iterator.hasNext()) {
                obj = new JSONObject();
                String key = iterator.next();
                obj = new JSONObject();
                try {
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_KEY, key);
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_NETWORKID);
                    obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_CONTENT);
                } catch (JSONException e) {
                    Log.d(TAG, "getOperatorSortKeyChannelListItem JSONException = " + e);
                    e.printStackTrace();
                }
                item = new ChannelListItem(mContext, "Operator Id:" + key, false, obj.toString());
                result.add(item);
            }
        }
        return result;
    }

    public LinkedList<Item> getOperatorSortChannelListItemByNetworkId(String inputId, int networkId) {
        LinkedList<Item> result = new LinkedList<Item>();
        LinkedList<Item> list = getChannelListItemWithoutIndex(inputId);
        Iterator<Item> itemList = list.iterator();
        ChannelListItem singleItem = null;
        int singlenetworkId = -1;
        int count = 1;
        JSONObject obj = null;
        while (itemList.hasNext()) {
            singleItem = (ChannelListItem)itemList.next();
            singlenetworkId = singleItem.getNetworkId();
            if (networkId == singlenetworkId) {
                obj = singleItem.getJSONObject();
                try {
                    obj.remove(KEY_SETTINGS_CHANNEL_ITEM_KEY);
                    obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_NETWORKID);
                    obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_SORT_CONTENT);
                } catch (JSONException e) {
                    Log.d(TAG, "getOperatorSortChannelListItemByNetworkId JSONException = " + e);
                    e.printStackTrace();
                }
                singleItem = new ChannelListItem(mContext, (String.valueOf(count) + "    " + singleItem.getTitle()), singleItem.isNeedShowIcon(), obj.toString());
                result.add(singleItem);
                count++;
            }
        }
        return result;
    }

    public LinkedList<Item> getMatchedSortChannelListItemByName(String inputId, String name) {
        LinkedList<Item> result = new LinkedList<Item>();
        if (TextUtils.isEmpty(name)) {
            return result;
        }
        LinkedList<Item> list = getChannelListItemWithoutIndex(inputId);
        Iterator<Item> itemList = list.iterator();
        ChannelListItem singleItem = null;
        String singleName = null;
        int count = 1;
        JSONObject obj = null;
        while (itemList.hasNext()) {
            singleItem = (ChannelListItem)itemList.next();
            singleName = singleItem.getTitle();
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(singleName)) {
                singleName = singleName.toUpperCase();
                name = name.toUpperCase();
                if (singleName.startsWith(name)) {
                    obj = singleItem.getJSONObject();
                    try {
                        obj.remove(KEY_SETTINGS_CHANNEL_ITEM_KEY);
                        obj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_CHANNEL_SORT_ALL);
                        obj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_ALL_CHANNEL);
                    } catch (JSONException e) {
                        Log.d(TAG, "getMatchedSortChannelListItemByName JSONException = " + e);
                        e.printStackTrace();
                    }
                    singleItem = new ChannelListItem(mContext, (String.valueOf(count) + "    " + singleItem.getTitle()), singleItem.isNeedShowIcon(), obj.toString());
                    result.add(singleItem);
                    count++;
                }
            }
        }
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
                    Log.d(TAG, "genarateUpdatedChannelListJsonSrt JSONException = " + e);
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
            Log.d(TAG, "getFavInfoFromChannel JSONException = " + e);
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
                //childObj.put(KEY_SETTINGS_IS_ALL_FAV_LIST, true);
                childObj.put(KEY_SETTINGS_FAV_LIST_TYPE, Item.LIST_ALL_FAV_LIST);
                childObj.put(KEY_SETTINGS_CHANNEL_ITEM_TYPE, Item.ACTION_FUNVTION_FAVLIST);
                childObj.put(KEY_SETTINGS_CHANNEL_CONTAINER_TYPE, Item.CONTAINER_ITEM_ALL_FAV);
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
                    Log.d(TAG, "getInit64FavList JSONException = " + e);
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
                    Log.d(TAG, "getFavListItem JSONException = " + e);
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public LinkedList<Item> getEditFavListItem() {
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
                    obj.put(KEY_SETTINGS_FAV_IS_ADDED, true);
                    obj.put(KEY_SETTINGS_FAV_LIST_TYPE, Item.LIST_EDIT_ALL_FAV_LIST);
                    //Log.d(TAG, "getFavListItem jsonObj = " + obj.toString());
                    item = new FavListItem(mContext, obj.getString(KEY_SETTINGS_FAV_NAME), true, obj.toString());
                    result.add(item);
                } catch (JSONException e) {
                    Log.d(TAG, "getFavListItem JSONException = " + e);
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
                Log.d(TAG, "getChannelFavListItem JSONException1 = " + e);
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
                    //obj.put(KEY_SETTINGS_IS_ALL_FAV_LIST, false);
                    obj.put(KEY_SETTINGS_FAV_LIST_TYPE, Item.LIST_CHANNEL_FAV_LIST);
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
                    Log.d(TAG, "getChannelFavListItem JSONException2 = " + e);
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public LinkedList<Item> getChannelItemByFavPage(int favId) {
        LinkedList<Item> result = new LinkedList<Item>();
        LinkedList<Item> listItem = getChannelListItemWithoutIndex("");
        if (listItem != null && listItem.size() > 0) {
            Iterator<Item> iterator = listItem.iterator();
            //SONObject obj = null;
            //JSONArray array = null;
            //String channelObjjsonSrt = null;
            ChannelListItem oneItem = null;
            boolean hasFav = false;
            List<Integer> favInfoList = null;
            int count = 1;
            while (iterator.hasNext()) {
                hasFav = false;
                oneItem = (ChannelListItem)iterator.next();
                favInfoList = oneItem.getFavAllIndex();
                if (favInfoList != null && favInfoList.size() > 0) {
                    for (int i = 0; i < favInfoList.size(); i++) {
                        if (favId == (int)favInfoList.get(i)) {
                            hasFav = true;
                            oneItem = new ChannelListItem(mContext, count + "    " + oneItem.getTitle(), hasFav, oneItem.getJSONObject().toString());
                            result.add(oneItem);
                            count++;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /*public LinkedList<Item> getChannelItemByFavPage(int favId) {
        LinkedList<Item> result = new LinkedList<Item>();
        List<String> list = getChannelList("");
        LinkedList<Item> listItem = getChannelListItemWithoutIndex("");
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
                    Log.d(TAG, "getChannelItemByFavPage JSONException = " + e);
                    e.printStackTrace();
                }
            }
        }

        return result;
    }*/

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
            //Log.d(TAG, "convertListToJsonStr item = " + item);
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

    public class MapKeyAscendComparator implements Comparator<Map.Entry<String, String>> {
        @Override
        public int compare(Map.Entry<String, String> mapping1, Map.Entry<String, String> mapping2) {
            return mapping1.getKey().compareTo(mapping2.getKey());
        }

        public Map<String, String> sortMapByKey(Map<String, String> map) {
            if (map == null || map.isEmpty()) {
                return null;
            }
            List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            Collections.sort(list, new MapKeyAscendComparator());
            Map<String, String> sortedMap = new LinkedHashMap<String, String>();
            Iterator<Map.Entry<String, String>> iter = list.iterator();
            Map.Entry<String, String> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
            /*for (Map.Entry<String, String> mapping : sortedMap.entrySet()) {
                Log.d(TAG,"MapKeyAscendComparator->" + mapping.getKey() + " ：" + mapping.getValue());
            }*/
            return sortedMap;
        }
    }

    public class MapKeyDescendComparator implements Comparator<Map.Entry<String, String>> {
        @Override
        public int compare(Map.Entry<String, String> mapping1, Map.Entry<String, String> mapping2) {
            return mapping1.getKey().compareTo(mapping2.getKey());
        }

        public Map<String, String> sortMapByKey(Map<String, String> map) {
            if (map == null || map.isEmpty()) {
                return null;
            }
            List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
            Collections.sort(list, new MapKeyDescendComparator());
            Map<String, String> sortedMap = new LinkedHashMap<String, String>();
            Iterator<Map.Entry<String, String>> iter = list.iterator();
            Map.Entry<String, String> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
            return sortedMap;
        }
    }

    public class MapValueAscendComparator implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> me1, Map.Entry<String, Integer> me2) {
            return me1.getValue().compareTo(me2.getValue());
        }

        public Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
            if (map == null || map.isEmpty()) {
                return null;
            }
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
            Collections.sort(list, new MapValueAscendComparator());
            Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
            Iterator<Map.Entry<String, Integer>> iter = list.iterator();
            Map.Entry<String, Integer> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
            /*for (Map.Entry<String, Integer> mapping : sortedMap.entrySet()) {
                Log.d(TAG,"MapValueAscendComparator->" + mapping.getKey() + " ：" + mapping.getValue());
            }*/
            return sortedMap;
        }
    }

    public class MapValueDescendComparator implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> me1, Map.Entry<String, Integer> me2) {
            return me1.getValue().compareTo(me2.getValue());
        }

        public Map<String, Integer> sortMapByValue(Map<String, Integer> map) {
            if (map == null || map.isEmpty()) {
                return null;
            }
            List<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>(map.entrySet());
            Collections.sort(list, new MapValueAscendComparator());
            Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
            Iterator<Map.Entry<String, Integer>> iter = list.iterator();
            Map.Entry<String, Integer> tmpEntry = null;
            while (iter.hasNext()) {
                tmpEntry = iter.next();
                sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
            }
            return sortedMap;
        }
    }

    public LinkedList<Item> getAZSortedItemList(LinkedList<Item> list) {
        LinkedList<Item> result = new LinkedList<Item>();
        if (list != null && list.size() > 0) {
            Collections.sort(list, new ItemAZComparator());
            result = list;
        }
        return result;
    }

    public LinkedList<Item> getZASortedItemList(LinkedList<Item> list) {
        LinkedList<Item> result = new LinkedList<Item>();
        if (list != null && list.size() > 0) {
            Collections.sort(list, new ItemZAComparator());
            result = list;
        }
        return result;
    }

    public class ItemAZComparator implements Comparator<Item> {
        @Override
        public int compare(Item me1, Item me2) {
            return me1.getOriginTitle().compareTo(me2.getOriginTitle());
        }
    }

    public class ItemZAComparator implements Comparator<Item> {
        @Override
        public int compare(Item me1, Item me2) {
            return me2.getOriginTitle().compareTo(me1.getOriginTitle());
        }
    }
}
