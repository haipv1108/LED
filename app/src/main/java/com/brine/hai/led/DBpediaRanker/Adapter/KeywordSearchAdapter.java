package com.brine.hai.led.DBpediaRanker.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.brine.hai.led.DBpediaRanker.Model.KeywordSearch;
import com.brine.hai.led.R;

import java.util.List;

/**
 * Created by hai on 24/12/2016.
 */

public class KeywordSearchAdapter extends BaseAdapter{
    private static final String TAG =
            KeywordSearchAdapter.class.getCanonicalName();

    private List<KeywordSearch> mListData;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public KeywordSearchAdapter(Context context,
                                List<KeywordSearch> listData){
        this.mContext = context;
        this.mListData = listData;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public Object getItem(int i) {
        return mListData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if(view == null){
            view = mLayoutInflater.inflate(
                    R.layout.keyword_search_item_layout, null);
            holder = new ViewHolder();
            holder.tvUri = (TextView) view.findViewById(R.id.tv_uri);
            holder.tvLabel = (TextView) view.findViewById(R.id.tv_label);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }

        KeywordSearch keywordSearch = mListData.get(i);
        holder.tvUri.setText(keywordSearch.getUri());
        holder.tvLabel.setText(keywordSearch.getLabel());
        return view;
    }

    static class ViewHolder {
        TextView tvUri;
        TextView tvLabel;
    }
}
