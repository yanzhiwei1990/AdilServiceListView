package zhiwei.adilservice;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;


public class SortFavActivity extends Activity {

    private static final String TAG = SortFavActivity.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final String LOGD = "logd";
    private static final String LOGE = "loge";

    private ChannelListListView mAllListView;
    private ChannelListListView mSortListView;
    private ChannelListListView mContentListView;
    private FavListListView mFavListView;
    private LinearLayout mRightShowContainner;

    private TextView mLeftTitle;
    private TextView mRightTitle;
    private Button mAllButton;
    private Button mATwoZButton;
    private Button mTPButton;
    private Button mNetWorkIdButton;
    private Button mExtraSortButton;

    private Button mFindButton;
    private Button mAddToFavButton;
    private Button mSatelliteButton;
    private Button mFavListButton;

    private ChannelDataManager mChannelDataManager;

    private static final int CONNECT = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CONNECT:
                    bindInterationService();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.channel_sort_edit);
        mHandler.sendEmptyMessage(CONNECT);
        mChannelDataManager = new ChannelDataManager(this);
        init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //LOG(LOGD, null, "onKeyDown " + event);
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (mRightShowContainner.getVisibility() == View.VISIBLE) {
                mRightShowContainner.setVisibility(View.GONE);
                return true;
            }
        }
        dealAction(getKeyEventAction(keyCode, event));
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        //LOG(LOGD, null, "onKeyUp " + event);
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (mRightShowContainner.getVisibility() == View.VISIBLE) {
                mRightShowContainner.setVisibility(View.GONE);
                if (mAllListView.getVisibility() == View.VISIBLE) {
                    mAllListView.requestFocus();
                } else if (mContentListView.getVisibility() == View.VISIBLE) {
                    mContentListView.requestFocus();
                } else if (mSortListView.getVisibility() == View.VISIBLE) {
                    mSortListView.requestFocus();
                }
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mClient.unRegisterInterationCallback(mIInterationCallback);
        } catch (Exception e) {
            Log.d(TAG, "onDestroy unRegisterInterationCallback e = " + e.getMessage());
            e.printStackTrace();
        }
        unbindInterationService();
    }

    private static final int ACTION_CHANNEL_SORT_ALL = 0;
    private static final int ACTION_CHANNEL_SORT_AZ = 1;
    private static final int ACTION_CHANNEL_SORT_TP = 2;
    private static final int ACTION_CHANNEL_SORT_NETWORKID = 3;
    private static final int ACTION_CHANNEL_SORT_SORT = 4;
    private static final int ACTION_FUNVTION_FIND = 5;
    private static final int ACTION_FUNVTION_ADD_FAV = 6;
    private static final int ACTION_FUNVTION_SATELLITE = 7;
    private static final int ACTION_FUNVTION_FAVLIST = 8;

    private int getKeyEventAction(int keyCode, KeyEvent event) {
        int resultAction = -1;
        switch (keyCode) {
            case KeyEvent.KEYCODE_PROG_RED:
                resultAction = ACTION_CHANNEL_SORT_ALL;
                break;
            case KeyEvent.KEYCODE_PROG_GREEN:
                resultAction = ACTION_CHANNEL_SORT_AZ;
                break;
            case KeyEvent.KEYCODE_PROG_YELLOW:
                resultAction = ACTION_CHANNEL_SORT_TP;
                break;
            case KeyEvent.KEYCODE_PROG_BLUE:
                resultAction = ACTION_CHANNEL_SORT_NETWORKID;
                break;
            case KeyEvent.KEYCODE_INFO:
                resultAction = ACTION_CHANNEL_SORT_SORT;
                break;
            case KeyEvent.KEYCODE_F1:
                resultAction = ACTION_FUNVTION_FIND;
                break;
            case KeyEvent.KEYCODE_F2:
                resultAction = ACTION_FUNVTION_ADD_FAV;
                break;
            case KeyEvent.KEYCODE_F3:
                resultAction = ACTION_FUNVTION_SATELLITE;
                break;
            case KeyEvent.KEYCODE_F4:
                resultAction = ACTION_FUNVTION_FAVLIST;
                break;
            default:
                break;
        }
        return resultAction;
    }

    private void dealAction(int action) {
        LOG(LOGD, null, "dealAction = " + action);
        switch (action) {
            case ACTION_CHANNEL_SORT_ALL:
                if (mAllListView.getVisibility() != View.VISIBLE) {
                    mAllListView.setVisibility(View.VISIBLE);
                }
                if (mSortListView.getVisibility() == View.VISIBLE) {
                    mSortListView.setVisibility(View.GONE);
                }
                if (mContentListView.getVisibility() == View.VISIBLE) {
                    mContentListView.setVisibility(View.GONE);
                }
                if (mRightShowContainner.getVisibility() == View.VISIBLE) {
                    mRightShowContainner.setVisibility(View.GONE);
                }
                dealActionUI(action);
                break;
            case ACTION_CHANNEL_SORT_AZ:
            case ACTION_CHANNEL_SORT_TP:
            case ACTION_CHANNEL_SORT_NETWORKID:
            case ACTION_CHANNEL_SORT_SORT:
                if (mSortListView.getVisibility() != View.VISIBLE) {
                    mSortListView.setVisibility(View.VISIBLE);
                }
                if (mContentListView.getVisibility() != View.VISIBLE) {
                    mContentListView.setVisibility(View.VISIBLE);
                }
                if (mAllListView.getVisibility() == View.VISIBLE) {
                    mAllListView.setVisibility(View.GONE);
                }
                if (mRightShowContainner.getVisibility() == View.VISIBLE) {
                    mRightShowContainner.setVisibility(View.GONE);
                }
                dealActionUI(action);
                break;
            case ACTION_FUNVTION_FIND:

                break;
            case ACTION_FUNVTION_ADD_FAV:
                mRightTitle.setText(R.string.f2_add_fav);
                if (mRightShowContainner.getVisibility() != View.VISIBLE) {
                    mRightShowContainner.setVisibility(View.VISIBLE);
                }
                dealActionUI(action);
                break;
            case ACTION_FUNVTION_FAVLIST:
                mRightTitle.setText(R.string.favourite_list);
                if (mRightShowContainner.getVisibility() != View.VISIBLE) {
                    mRightShowContainner.setVisibility(View.VISIBLE);
                }
                dealActionUI(action);
                break;
            case ACTION_FUNVTION_SATELLITE:

                break;
            default:
                break;
        }
    }

    private void dealActionUI(int action) {
        LOG(LOGD, null, "dealActionUI = " + action);
        switch (action) {
            case ACTION_CHANNEL_SORT_ALL:
                mLeftTitle.setText(R.string.channel_list_all);
                mAllListView.updateAllItem(this, mChannelDataManager.getChannelListItem(""));
                break;
            case ACTION_CHANNEL_SORT_AZ:
                mLeftTitle.setText(R.string.channel_list_all);
                //mSortListView.updateAllItem(mChannelDataManager.getAZSortKeyList());
                //mContentListView.updateAllItem(mChannelDataManager.getAZSortChannelList());
                break;
            case ACTION_CHANNEL_SORT_TP:
                mLeftTitle.setText(R.string.channel_list_all);
                //mSortListView.updateAllItem(mChannelDataManager.getTPSortKeyList());
                //mContentListView.updateAllItem(mChannelDataManager.getTPSortChannelList());

                break;
            case ACTION_CHANNEL_SORT_NETWORKID:
                mLeftTitle.setText(R.string.channel_list_all);
                //mSortListView.updateAllItem(mChannelDataManager.getOperatorSortKeyList());
                //mContentListView.updateAllItem(mChannelDataManager.getOperatorSortChannelList());
                break;
            case ACTION_CHANNEL_SORT_SORT:

                break;
            case ACTION_FUNVTION_FIND:

                break;
            case ACTION_FUNVTION_ADD_FAV:
                if (mCurrentEditChannelList != null && mCurrentEditChannelIndex > -1 && mCurrentEditChannelId > -1) {
                    if (TextUtils.equals(mCurrentEditChannelList, LIST_ALL_CHANNEL)) {
                        ItemAdapter adapter = (ItemAdapter)mAllListView.getAdapter();
                        if (adapter != null && adapter.getCount() > 0 && adapter.getCount() > mCurrentEditChannelIndex) {
                            mFavListView.updateAllItem(this, mChannelDataManager.getChannelFavListItem((ChannelListItem)adapter.getItem(mCurrentEditChannelIndex)));
                            mFavListView.requestFocus();
                            mFavListView.setSelection(0);
                        }
                    }
                }
                break;
            case ACTION_FUNVTION_SATELLITE:
                mLeftTitle.setText(R.string.channel_list_all);
                //mAllListView.updateAllItem(mChannelDataManager.getChannelListItem(""));
                break;
            case ACTION_FUNVTION_FAVLIST:
                mFavListView.updateAllItem(this, mChannelDataManager.getFavListItem());
                mFavListView.requestFocus();
                mFavListView.setSelection(0);
                break;
            default:
                break;

        }
    }

    /*private void init() {
        Button start = findViewById(R.id.start);
        Button stop = findViewById(R.id.stop);
        Button bind = findViewById(R.id.bind);
        Button unbind = findViewById(R.id.unbind);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startInterationService();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopInterationService();
            }
        });
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindInterationService();
            }
        });
        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unbindInterationService();
            }
        });
    }*/

    private void init() {
        mLeftTitle = (TextView)findViewById(R.id.channellist_title);
        mRightTitle = (TextView)findViewById(R.id.favlist_title);
        mAllButton = (Button)findViewById(R.id.red_sort_button);
        mATwoZButton = (Button)findViewById(R.id.green_sort_button);
        mTPButton = (Button)findViewById(R.id.yellow_sort_button);
        mNetWorkIdButton = (Button)findViewById(R.id.blue_sort_button);
        mExtraSortButton = (Button)findViewById(R.id.sort_sort_button);

        mFindButton = (Button)findViewById(R.id.f1_button);
        mAddToFavButton = (Button)findViewById(R.id.f2_button);
        mSatelliteButton = (Button)findViewById(R.id.f3_button);
        mFavListButton = (Button)findViewById(R.id.f4_button);

        mSortListView = (ChannelListListView) findViewById(R.id.sort_key);
        mContentListView = (ChannelListListView)findViewById(R.id.sort_channel);
        mAllListView = (ChannelListListView)findViewById(R.id.sort_channel_all);
        mFavListView = (FavListListView) findViewById(R.id.favourite);
        mRightShowContainner = (LinearLayout) findViewById(R.id.right_show);
        ItemAdapter adapter = null;
        adapter = new ItemAdapter(mChannelDataManager.getChannelListItem("adtv"), this, ChannelListItem.class.getSimpleName());
        mAllListView.setAdapter(adapter);

        mAllListView.setVisibility(View.VISIBLE);
        mSortListView.setVisibility(View.GONE);
        mContentListView.setVisibility(View.GONE);

        //adapter = new ItemAdapter(mChannelDataManager.getFavListItem(), this, FavListItem.class.getSimpleName());
        //mFavListView.setAdapter(adapter);
        mRightShowContainner.setVisibility(View.GONE);
        //mFavListView.setVisibility(View.GONE);

        setListener();
        /*mAllListView.setSelection(2);
        mFavListView.setSelection(5);
        mAllListView.clearFocus();
        mFavListView.clearFocus();
        mAllButton.requestFocus();*/
    }

    private void setListener() {
        mAllListView.setOnItemClickListener(mOnItemClickListener);
        mAllListView.setOnItemSelectedListener(mOnItemSelectedListener);
        mAllListView.setOnFocusChangeListener(mOnItemFocusChangeListener);

        mSortListView.setOnItemClickListener(mOnItemClickListener);
        mSortListView.setOnItemSelectedListener(mOnItemSelectedListener);
        mSortListView.setOnFocusChangeListener(mOnItemFocusChangeListener);

        mContentListView.setOnItemClickListener(mOnItemClickListener);
        mContentListView.setOnItemSelectedListener(mOnItemSelectedListener);
        mContentListView.setOnFocusChangeListener(mOnItemFocusChangeListener);

        mFavListView.setOnItemClickListener(mOnItemClickListener);
        mFavListView.setOnItemSelectedListener(mOnItemSelectedListener);
        mFavListView.setOnFocusChangeListener(mOnItemFocusChangeListener);

        mAllButton.setOnClickListener(mOnClickListener);
        mAllButton.setOnFocusChangeListener(mOnItemFocusChangeListener);
        mATwoZButton.setOnClickListener(mOnClickListener);
        mATwoZButton.setOnFocusChangeListener(mOnItemFocusChangeListener);
        mTPButton.setOnClickListener(mOnClickListener);
        mTPButton.setOnFocusChangeListener(mOnItemFocusChangeListener);
        mNetWorkIdButton.setOnClickListener(mOnClickListener);
        mNetWorkIdButton.setOnFocusChangeListener(mOnItemFocusChangeListener);
        mExtraSortButton = (Button)findViewById(R.id.sort_sort_button);
        mExtraSortButton.setOnClickListener(mOnClickListener);
        mExtraSortButton.setOnFocusChangeListener(mOnItemFocusChangeListener);

        mFindButton.setOnClickListener(mOnClickListener);
        mFindButton.setOnFocusChangeListener(mOnItemFocusChangeListener);
        mAddToFavButton.setOnClickListener(mOnClickListener);
        mAddToFavButton.setOnFocusChangeListener(mOnItemFocusChangeListener);
        mSatelliteButton.setOnClickListener(mOnClickListener);
        mSatelliteButton.setOnFocusChangeListener(mOnItemFocusChangeListener);
        mFavListButton.setOnClickListener(mOnClickListener);
        mFavListButton.setOnFocusChangeListener(mOnItemFocusChangeListener);
    }

    private void LOG(String type, String extra, String value) {
        if (DEBUG && LOGD.equals(type)) {
            if (TextUtils.isEmpty(extra)) {
                Log.d(TAG, value);
            } else {
                Log.d(extra, value);
            }
        } else if (LOGE.equals(type)) {
            if (TextUtils.isEmpty(extra)) {
                Log.e(TAG, value);
            } else {
                Log.e(extra, value);
            }
        }
    }

    private static final String LIST_ALL_CHANNEL = "all_channel_list";
    private static final String LIST_SORT_KEY = "sort_key_list";
    private static final String LIST_SORT_CONTENT = "sort_content_list";
    private static final String LIST_ALL_FAV_LIST = "all_fav_list";
    private static final String LIST_CHANNEL_FAV_LIST = "all_channel_fav_list";
    private String mCurrentEditChannelList =  LIST_ALL_CHANNEL;//content or all channel
    private long mCurrentEditChannelId =  -1;
    private int mCurrentEditChannelIndex =  -1;
    private String mCurrentFavlList =  LIST_ALL_FAV_LIST;//all or channel fav
    private int mCurrentFavId =  -1;
    private int mCurrentFavIndex =  -1;

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (parent != null) {
                int parentRes = parent.getId();
                switch (parentRes) {
                    case R.id.sort_key:
                        LOG(LOGD, null, "onItemSelected sort_key position = " + position + ", id = " + id);
                        break;
                    case R.id.sort_channel:
                        LOG(LOGD, null, "onItemSelected sort_channel position = " + position + ", id = " + id);
                        updateEditChannelInfo(parent, parentRes, position);
                        break;
                    case R.id.sort_channel_all:
                        LOG(LOGD, null, "onItemSelected sort_channel_all position = " + position + ", id = " + id + ", view = " + view);
                        updateEditChannelInfo(parent, parentRes, position);
                        break;
                    case R.id.favourite:
                        LOG(LOGD, null, "onItemSelected favourite position = " + position + ", id = " + id);
                        FavListItem item = null;
                        if (parent instanceof FavListListView) {
                            Object obj = parent.getItemAtPosition(position);
                            if (obj instanceof FavListItem) {
                                item = (FavListItem)obj;
                            }
                        }
                        if (item != null) {
                            if (item.isAllFavList()) {
                                mCurrentFavlList = LIST_ALL_FAV_LIST;
                                mCurrentFavId = item.getFavId();
                                mCurrentFavIndex = position;
                            } else {
                                mCurrentFavlList = LIST_CHANNEL_FAV_LIST;
                                mCurrentFavId = item.getFavId();
                                mCurrentFavIndex = position;
                            }
                            LOG(LOGD, null, "onItemSelected favourite mCurrentFavlList = " + mCurrentFavlList + ", mCurrentFavId = " + mCurrentFavId + ", mCurrentFavIndex = " + mCurrentFavIndex);
                        }
                        break;
                    default:
                        LOG(LOGD, null, "onItemSelected parent = " + parent + ", view = " + view + ", position = " + position + ", id = " + id);
                        break;
                }
            } else {
                LOG(LOGD, null, "onItemSelected parent null");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            LOG(LOGD, null, "onNothingSelected parent = " + parent);
        }
    };

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (parent != null) {
                int parentRes = parent.getId();
                switch (parentRes) {
                    case R.id.sort_key:
                        LOG(LOGD, null, "onItemClick sort_key position = " + position + ", id = " + id);
                        break;
                    case R.id.sort_channel:
                        LOG(LOGD, null, "onItemClick sort_channel position = " + position + ", id = " + id);
                        updateEditChannelInfo(parent, parentRes, position);
                        break;
                    case R.id.sort_channel_all:
                        LOG(LOGD, null, "onItemClick sort_channel position = " + position + ", id = " + id + "，　view = " + view);
                        updateEditChannelInfo(parent, parentRes, position);
                        break;
                    case R.id.favourite:
                        LOG(LOGD, null, "onItemClick favourite position = " + position + ", id = " + id);
                        FavListItem favItem = null;
                        if (parent instanceof FavListListView) {
                            favItem = (FavListItem)(parent.getItemAtPosition(position));
                        }
                        if (favItem != null) {
                            if (favItem.isAllFavList()) {
                                mCurrentFavlList = LIST_ALL_FAV_LIST;
                                mCurrentFavId = favItem.getFavId();
                                mCurrentFavIndex = position;
                            } else {
                                mCurrentFavlList = LIST_CHANNEL_FAV_LIST;
                                mCurrentFavId = favItem.getFavId();
                                mCurrentFavIndex = position;
                            }
                            LOG(LOGD, null, "OnItemClickListener favourite mCurrentFavlList = " + mCurrentFavlList + ", mCurrentFavId = " + mCurrentFavId + ", mCurrentFavIndex = " + mCurrentFavIndex);
                        }
                        updateChannelFavInfo(parent, parentRes, position);
                        break;
                    default:
                        LOG(LOGD, null, "onItemClick parent = " + parent + ", view = " + view + ", position = " + position + ", id = " + id);
                        break;
                }
            } else {
                LOG(LOGD, null, "onItemClick parent null");
            }
        }
    };

    private AdapterView.OnFocusChangeListener mOnItemFocusChangeListener = new AdapterView.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            /*if (v != null) {
                int listViewRes = v.getId();
                switch (listViewRes) {
                    case R.id.sort_key:
                        LOG(LOGD, null, "onFocusChange sort_key hasFocus = " + hasFocus);
                        if (hasFocus) {
                            //mSortListView.setSelection(mSortListView.getSelectedItemPosition());
                        }
                        break;
                    case R.id.sort_channel:
                        LOG(LOGD, null, "onFocusChange sort_channel hasFocus = " + hasFocus);
                        if (hasFocus) {
                            //mContentListView.setSelection(mContentListView.getSelectedItemPosition());
                        }
                        break;
                    case R.id.sort_channel_all:
                        LOG(LOGD, null, "onFocusChange sort_channel_all hasFocus = " + hasFocus);
                        if (hasFocus) {
                            //mAllListView.setSelection(mAllListView.getSelectedItemPosition());
                        }
                        break;
                    case R.id.favourite:
                        LOG(LOGD, null, "onFocusChange favourite hasFocus = " + hasFocus);
                        if (hasFocus) {
                            //mFavListView.setSelection(mFavListView.getSelectedItemPosition());
                        }
                        break;
                    case R.id.red_sort_button:
                        LOG(LOGD, null, "onFocusChange red_sort_button hasFocus = " + hasFocus);

                        break;
                    case R.id.green_sort_button:
                        LOG(LOGD, null, "onFocusChange green_sort_button hasFocus = " + hasFocus);

                        break;
                    case R.id.yellow_sort_button:
                        LOG(LOGD, null, "onFocusChange yellow_sort_button hasFocus = " + hasFocus);

                        break;
                    case R.id.blue_sort_button:
                        LOG(LOGD, null, "onFocusChange blue_sort_button hasFocus = " + hasFocus);

                        break;
                    case R.id.sort_sort_button:
                        LOG(LOGD, null, "onFocusChange sort_sort_button hasFocus = " + hasFocus);

                        break;
                    case R.id.f1_button:
                        LOG(LOGD, null, "onFocusChange f1_button hasFocus = " + hasFocus);

                        break;
                    case R.id.f2_button:
                        LOG(LOGD, null, "onFocusChange f2_button hasFocus = " + hasFocus);

                        break;
                    case R.id.f3_button:
                        LOG(LOGD, null, "onFocusChange f3_button hasFocus = " + hasFocus);

                        break;
                    case R.id.f4_button:
                        LOG(LOGD, null, "onFocusChange f4_button hasFocus = " + hasFocus);

                        break;
                    default:
                        LOG(LOGD, null, "onFocusChange other = " + v + ", hasFocus = " + hasFocus);
                        break;
                }
            } else {
                LOG(LOGD, null, "onFocusChange v null");
            }*/
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v != null) {
                int buttonRes = v.getId();
                dealAction(getButtonClickAction(buttonRes));
            }
        }
    };

    //update by select channel fav
    private void updateChannelFavInfo(AdapterView<?> parent, int resId, int position) {
        if (parent instanceof FavListListView) {
            FavListItem favItem = null;
            ItemAdapter favAdapter = null;
            boolean isAllFav = true;
            boolean isSelectedFav = false;
            int favId = -1;
            favItem = (FavListItem)parent.getItemAtPosition(position);
            LinkedList<Item> data =  ((ItemAdapter)parent.getAdapter()).getAllData();
            if (favItem != null) {
                isAllFav = favItem.isAllFavList();
                favId = favItem.getFavId();
                isSelectedFav = favItem.isNeedShowIcon();
                LOG(LOGD, null, "updateChannelFavInfo isAllFav = " + isAllFav + ", favId = " + favId + ", isSelectedFav = " + isSelectedFav);
                if (!isAllFav) {
                    isSelectedFav = !isSelectedFav;
                    favItem.setNeedShowIcon(isSelectedFav);
                    mFavListView.updateItem(position, favItem);
                    if (TextUtils.equals(mCurrentEditChannelList, LIST_ALL_CHANNEL)) {
                        ItemAdapter adapter = (ItemAdapter)(mAllListView.getAdapter());
                        ChannelListItem channelItem = (ChannelListItem)(adapter.getItem(mCurrentEditChannelIndex));
                        channelItem.getUpdateFavAllIndexArrayString(isSelectedFav, favId);
                        mAllListView.updateItem(mCurrentEditChannelIndex, channelItem);
                        //adapter = (ItemAdapter)(mAllListView.getAdapter());
                        String updateChannel = mChannelDataManager.genarateUpdatedChannelListJsonSrt(mChannelDataManager.getChannelList(""), channelItem.getChannelId(), channelItem.getFavArrayJsonStr());
                        mChannelDataManager.updateChannelListChangeToDatabase(updateChannel);
                    } else if (TextUtils.equals(mCurrentEditChannelList, LIST_SORT_CONTENT)) {
                        ItemAdapter adapter = (ItemAdapter)(mAllListView.getAdapter());
                        ChannelListItem channelItem = (ChannelListItem)(adapter.getItem(mCurrentEditChannelIndex));
                        channelItem.getUpdateFavAllIndexArrayString(isSelectedFav, favId);
                        mContentListView.updateItem(mCurrentEditChannelIndex, channelItem);
                        String updateChannel = mChannelDataManager.genarateUpdatedChannelListJsonSrt(mChannelDataManager.getChannelList(""), channelItem.getChannelId(), channelItem.getFavArrayJsonStr());
                        mChannelDataManager.updateChannelListChangeToDatabase(updateChannel);
                    }
                } else {
                    if (mSortListView.getVisibility() == View.VISIBLE) {
                        mSortListView.setVisibility(View.GONE);
                    }
                    if (mContentListView.getVisibility() == View.VISIBLE) {
                        mContentListView.setVisibility(View.GONE);
                    }
                    if (mAllListView.getVisibility() != View.VISIBLE) {
                        mContentListView.setVisibility(View.VISIBLE);
                    }
                    mLeftTitle.setText(favItem.getTitle());
                    mAllListView.updateAllItem(this, mChannelDataManager.getChannelItemByFavPage(favItem.getFavId()));
                }
            }
        }
    }

    //update when channel item is focused
    private void updateEditChannelInfo(AdapterView<?> parent, int resId, int position) {
        switch (resId) {
            case R.id.sort_channel:
                mCurrentEditChannelList = LIST_SORT_CONTENT;
                break;
            case R.id.sort_channel_all:
                mCurrentEditChannelList = LIST_ALL_CHANNEL;
                break;
            default:
                mCurrentEditChannelList = null;
                mCurrentEditChannelIndex = -1;
                mCurrentEditChannelId = -1;
                return;
        }
        mCurrentEditChannelIndex = position;
        if (parent instanceof ChannelListListView) {
            Object obj = parent.getItemAtPosition(position);
            if (obj instanceof ChannelListItem) {
                ChannelListItem item = (ChannelListItem)obj;
                if (item != null) {
                    mCurrentEditChannelId = item.getChannelId();
                }
            }
        }
        LOG(LOGD, null, ("updateEditChannelInfo mCurrentEditChannelList = " + mCurrentEditChannelList + ", mCurrentEditChannelIndex = " + mCurrentEditChannelIndex + ", mCurrentEditChannelId =" + mCurrentEditChannelId));
    }

    private int getButtonClickAction(int buttonRes) {
        int resultAction = -1;
        switch (buttonRes) {
            case R.id.red_sort_button:
                resultAction = ACTION_CHANNEL_SORT_ALL;
                break;
            case R.id.green_sort_button:
                resultAction = ACTION_CHANNEL_SORT_AZ;
                break;
            case R.id.yellow_sort_button:
                resultAction = ACTION_CHANNEL_SORT_TP;
                break;
            case R.id.blue_sort_button:
                resultAction = ACTION_CHANNEL_SORT_NETWORKID;
                break;
            case R.id.sort_sort_button:
                resultAction = ACTION_CHANNEL_SORT_SORT;
                break;
            case R.id.f1_button:
                resultAction = ACTION_FUNVTION_FIND;
                break;
            case R.id.f2_button:
                resultAction = ACTION_FUNVTION_ADD_FAV;
                break;
            case R.id.f3_button:
                resultAction = ACTION_FUNVTION_SATELLITE;
                break;
            case R.id.f4_button:
                resultAction = ACTION_FUNVTION_FAVLIST;
        }
        return resultAction;
    }



    //add for service connect demo
    private IInterationService mClient = null;
    private InterationServiceConnection mInterationServiceConnection = new InterationServiceConnection();
    private boolean mIsBounded = false;
    private boolean mStartedBound = false;

    private final class InterationServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {
            LOG(LOGD, null, "onServiceConnected name = " + name + ", service = " + service);
            mClient = IInterationService.Stub.asInterface(service);
            try {
                mClient.registerInterationCallback(mIInterationCallback);
            } catch (Exception e) {
                LOG(LOGD, null, "registerInterationCallback e = " + e.getMessage());
                e.printStackTrace();
            }
            mIsBounded = true;
        }
        public void onServiceDisconnected(ComponentName name) {
            LOG(LOGD, null, "onServiceDisconnected name = " + name);
            mIsBounded = false;
            try {
                mClient.unRegisterInterationCallback(mIInterationCallback);
            } catch (Exception e) {
                LOG(LOGD, null, "unRegisterInterationCallback e = " + e.getMessage());
                e.printStackTrace();
            }
            mClient = null;
        }
    }

    private IInterationCallback mIInterationCallback = new IInterationCallback.Stub() {
        @Override
        public void onReceiveInterationCallback(String json) {
            LOG(LOGD, null, "onReceiveInterationCallback = " + json);
        }
    };

    private void startInterationService() {
        LOG(LOGD, null, "startInterationService");
        Intent intent = new Intent("droidlogic.intent.action.InterationService");
        //intent.setClassName("com.droidlogic.droidlivetv", "com.droidlogic.droidlivetv.interationservice.InterationService");
        intent.setClassName("zhiwei.adilservice", "zhiwei.adilservice.InterationService");
        startService(intent);
    }

    private void stopInterationService() {
        LOG(LOGD, null, "stopInterationService");
        Intent intent = new Intent("droidlogic.intent.action.InterationService");
        intent.setClassName("zhiwei.adilservice", "zhiwei.adilservice.InterationService");
        stopService(intent);
    }

    private void bindInterationService() {
        if (!mStartedBound) {
            LOG(LOGD, null, "bindInterationService");
            mStartedBound = true;
            Intent intent = new Intent("droidlogic.intent.action.InterationService");
            intent.setClassName("zhiwei.adilservice", "zhiwei.adilservice.InterationService");
            bindService(intent, mInterationServiceConnection, Service.BIND_AUTO_CREATE);
        } else {
            LOG(LOGD, null, "bindInterationService has started");
        }
    }

    private void unbindInterationService() {
        if (mStartedBound) {
            LOG(LOGD, null, "unbindInterationService");
            mStartedBound = false;
            unbindService(mInterationServiceConnection);
        } else {
            LOG(LOGD, null, "unbindInterationService not started");
        }
    }
}
