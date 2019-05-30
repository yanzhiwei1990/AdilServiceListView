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
import android.widget.AdapterView;
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


public class SortFavActivity extends Activity {

    private static final String TAG = SortFavActivity.class.getSimpleName();
    private static final boolean DEBUG = true;
    private static final String LOGD = "logd";
    private static final String LOGE = "loge";

    private ListView mAllListView;
    private ListView mSortListView;
    private ListView mContentListView;
    private ListView mFavListView;

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

        mSortListView = (ListView) findViewById(R.id.sort_key);
        mContentListView = (ListView)findViewById(R.id.sort_channel);
        mAllListView = (ListView)findViewById(R.id.sort_channel_all);
        mFavListView = (ListView)findViewById(R.id.favourite);
        ItemAdapter adapter = null;
        adapter = new ItemAdapter(mChannelDataManager.getChannelListItem("adtv"), this);
        mAllListView.setAdapter(adapter);

        mSortListView.setVisibility(View.GONE);
        mContentListView.setVisibility(View.GONE);

        adapter = new ItemAdapter(mChannelDataManager.getFavListItem(), this);
        mFavListView.setAdapter(adapter);

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
                        break;
                    case R.id.sort_channel_all:
                        LOG(LOGD, null, "onItemClick sort_channel position = " + position + ", id = " + id);
                        break;
                    case R.id.favourite:
                        LOG(LOGD, null, "onItemClick favourite position = " + position + ", id = " + id);
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
                        break;
                    case R.id.sort_channel_all:
                        LOG(LOGD, null, "onItemSelected sort_channel_all position = " + position + ", id = " + id);
                        break;
                    case R.id.favourite:
                        LOG(LOGD, null, "onItemSelected favourite position = " + position + ", id = " + id);
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

    private AdapterView.OnFocusChangeListener mOnItemFocusChangeListener = new AdapterView.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v != null) {
                int listViewRes = v.getId();
                switch (listViewRes) {
                    case R.id.sort_key:
                        LOG(LOGD, null, "onFocusChange sort_key hasFocus = " + hasFocus);
                        if (hasFocus) {
                            mSortListView.setSelection(mSortListView.getSelectedItemPosition());
                        }
                        break;
                    case R.id.sort_channel:
                        LOG(LOGD, null, "onFocusChange sort_channel hasFocus = " + hasFocus);
                        if (hasFocus) {
                            mContentListView.setSelection(mContentListView.getSelectedItemPosition());
                        }
                        break;
                    case R.id.sort_channel_all:
                        LOG(LOGD, null, "onFocusChange sort_channel_all hasFocus = " + hasFocus);
                        if (hasFocus) {
                            mAllListView.setSelection(mAllListView.getSelectedItemPosition());
                        }
                        break;
                    case R.id.favourite:
                        LOG(LOGD, null, "onFocusChange favourite hasFocus = " + hasFocus);
                        if (hasFocus) {
                            mFavListView.setSelection(mFavListView.getSelectedItemPosition());
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
            }
        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v != null) {
                int buttonRes = v.getId();
                switch (buttonRes) {
                    case R.id.red_sort_button:
                        LOG(LOGD, null, "onClick red_sort_button");
                        break;
                    case R.id.green_sort_button:
                        LOG(LOGD, null, "onClick green_sort_button");
                        break;
                    case R.id.yellow_sort_button:
                        LOG(LOGD, null, "onClick yellow_sort_button");
                        break;
                    case R.id.blue_sort_button:
                        LOG(LOGD, null, "onClick blue_sort_button");
                        break;
                    case R.id.sort_sort_button:
                        LOG(LOGD, null, "onClick sort_sort_button");
                        break;
                    case R.id.f1_button:
                        LOG(LOGD, null, "onClick f1_button");
                        break;
                    case R.id.f2_button:
                        LOG(LOGD, null, "onClick f2_button");
                        break;
                    case R.id.f3_button:
                        LOG(LOGD, null, "onClick f3_button");
                        break;
                    case R.id.f4_button:
                        LOG(LOGD, null, "onClick f4_button");
                        break;
                    default:
                        LOG(LOGD, null, "onClick unkown");
                        break;
                }
            }
        }
    };

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
