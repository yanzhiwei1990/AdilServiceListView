package zhiwei.adilservice;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.LinkedList;

public abstract class CommonAdapter<T> extends BaseAdapter {
    private static final String TAG = "CommonAdapter";

    private LinkedList<T> mData;
    private Context mContext;

    public CommonAdapter(LinkedList<T> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        T item = mData.get(position);
        CommonViewHolder viewHolder = setViewContent(mContext, convertView, parent, position, item);
        return viewHolder.getConvertView();
    }

    protected abstract CommonViewHolder setViewContent(Context context, View view, ViewGroup parent, int position, T t);
}
