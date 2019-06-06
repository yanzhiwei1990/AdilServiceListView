package zhiwei.adilservice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomedDialogView {
    private static final String TAG = CustomedDialogView.class.getSimpleName();

    private Context mContext;
    private DialogCallback mDialogCallback;

    public static final String DIALOG_ACTION = "dialog_action";
    public static final String DIALOG_EVENT = "dialog_event";
    public static final String DIALOG_LIST_POSITION = "list_position";
    public static final String DIALOG_ACTION_SORT_LIST_CLICKED = "sort_list_clicked";
    public static final String DIALOG_ACTION_EXIT = "exit";
    public static final String DIALOG_ACTION_SEARCH_BUTTON_CLICKED = "search_button_clicked";
    public static final String DIALOG_ACTION_SEARCH_CONTENT_CHANGED = "search_content_changed";
    public static final String DIALOG_ACTION_SEARCH_VALUE = "search_channel_value";


    public static final int[] SORT_ITEM = {R.string.sort_a_z, R.string.sort_z_a};
    public static final int FLAG_SORT_ITEM_AZ = 0;
    public static final int FLAG_SORT_ITEM_ZA = 1;

    public CustomedDialogView(Context context, DialogCallback dialogCallback) {
        this.mContext = context;
        this.mDialogCallback = dialogCallback;
    }

    public AlertDialog creatSortOrderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog alertDialog = builder.create();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        List<ListItem> itemList = new ArrayList<ListItem>();
        ListItem item = null;
        for (int i = 0; i < SORT_ITEM.length; i++) {
            item = new ListItem(mContext.getString(SORT_ITEM[i]));
            itemList.add(item);
        }
        View dialogView = View.inflate(mContext, R.layout.dialog_list, null);
        MyAdapter myAdapter = new MyAdapter(mContext,itemList);
        ListView list = dialogView.findViewById(R.id.dialog_listview);
        list.setAdapter(myAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDialogCallback != null) {
                    alertDialog.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(DIALOG_ACTION, true);
                    bundle.putString(DIALOG_EVENT, DIALOG_ACTION_SORT_LIST_CLICKED);
                    bundle.putInt(DIALOG_LIST_POSITION, position);
                    mDialogCallback.onDialogEvent(bundle);
                }
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mDialogCallback != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(DIALOG_ACTION, true);
                    bundle.putString(DIALOG_EVENT, DIALOG_ACTION_EXIT);
                    mDialogCallback.onDialogEvent(bundle);
                }
            }
        });
        alertDialog.setView(dialogView);
        return alertDialog;
    }

    public AlertDialog creatSeachChannelDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog alertDialog = builder.create();
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        alertDialog.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        View dialogView = View.inflate(mContext, R.layout.dialog_search_channel, null);
        final EditText edit = dialogView.findViewById(R.id.search_channel_edittext);
        final Button search = dialogView.findViewById(R.id.search_channel_ok);
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "beforeTextChanged s = " + s + ", start = " + start + ", count = " + count + ", after = " + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "onTextChanged s = " + s + ", start = " + start + ", before = " + before + ", count = " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "afterTextChanged s = " + s);
                if (mDialogCallback != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(DIALOG_ACTION, true);
                    bundle.putString(DIALOG_EVENT, DIALOG_ACTION_SEARCH_CONTENT_CHANGED);
                    Editable editable = edit.getText();
                    bundle.putString(DIALOG_ACTION_SEARCH_VALUE, !TextUtils.isEmpty(editable) ? editable.toString() : "");
                    mDialogCallback.onDialogEvent(bundle);
                }
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogCallback != null) {
                    alertDialog.dismiss();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(DIALOG_ACTION, true);
                    bundle.putString(DIALOG_EVENT, DIALOG_ACTION_SEARCH_BUTTON_CLICKED);
                    Editable editable = edit.getText();
                    bundle.putString(DIALOG_ACTION_SEARCH_VALUE, !TextUtils.isEmpty(editable) ? editable.toString() : "");
                    mDialogCallback.onDialogEvent(bundle);
                }
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (mDialogCallback != null) {
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(DIALOG_ACTION, true);
                    bundle.putString(DIALOG_EVENT, DIALOG_ACTION_EXIT);
                    mDialogCallback.onDialogEvent(bundle);
                }
            }
        });
        alertDialog.setView(dialogView);
        return alertDialog;
    }

    public void setDialogCallback(DialogCallback dialogCallback) {
        this.mDialogCallback = dialogCallback;
    }

    public interface DialogCallback {
        void onDialogEvent(Bundle bundle);
    }

    public class MyAdapter extends BaseAdapter {
        private List<ListItem> mList;
        private Context mAdapterContext;

        public MyAdapter(Context context, List<ListItem> list) {
            this.mList = list;
            this.mAdapterContext = context;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ListViewHolder holder = null;
            if(convertView == null){
                convertView = LayoutInflater.from(mAdapterContext).inflate(R.layout.dialog_list_item,parent,false);
                holder = new ListViewHolder();
                holder.mListTextView = (TextView) convertView.findViewById(R.id.list_item_text);
                convertView.setTag(holder);
            }else{
                holder = (ListViewHolder) convertView.getTag();
            }
            holder.mListTextView.setText(mList.get(position).getTitle());
            return convertView;
        }
    }

    public class ListItem {

        private String mTitle = null;

        public ListItem(String title){
            this.mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }
    }

    public class ListViewHolder {
        private TextView mListTextView;

        public ListViewHolder(){

        }

        public void setListTextView(TextView textView) {
            mListTextView = textView;
        }

        public TextView getListTextView() {
            return mListTextView;
        }
    }
}
