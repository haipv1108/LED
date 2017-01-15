package com.brine.hai.led.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.brine.hai.led.Model.FSResult;
import com.brine.hai.led.R;

import java.util.List;

/**
 * Created by phamhai on 14/01/2017.
 */

public class FSAdapter extends RecyclerView.Adapter<FSAdapter.ViewHolder> {

    private static final String TAG = FSAdapter.class.getCanonicalName();
    private Context mContext;
    private List<FSResult> mSearchResults;
    private FSCallBack mCallBack;

    public interface FSCallBack{
        void showDetailsUri(String uri);
        void addSearchExploratory(String label, String uri);
    }

    public FSAdapter(Context context, List<FSResult> searchResults, FSCallBack callBack){
        this.mContext = context;
        this.mSearchResults = searchResults;
        this.mCallBack = callBack;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fs_item_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FSResult result = mSearchResults.get(position);
        holder.tvUri.setText(result.getUri());
        holder.tvLabel.setText(result.getLabel());
        holder.tvDescription.setText(Html.fromHtml(result.getDescription()));
        holder.tvScore.setText(String.valueOf(result.getScore()));
        holder.tvRank.setText(String.valueOf(result.getRank()));
        holder.tvOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionMenu(v, result);
            }
        });
    }

    private void showOptionMenu(View view, final FSResult result){
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_fs_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_details:
                        mCallBack.showDetailsUri(result.getUri());
                        return true;
                    case R.id.menu_add_search:
                        mCallBack.addSearchExploratory(result.getLabel(), result.getUri());
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @Override
    public int getItemCount() {
        return mSearchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tvUri;
        public TextView tvLabel;
        public TextView tvDescription;
        public TextView tvScore;
        public TextView tvRank;
        public TextView tvOptions;

        public ViewHolder(View view){
            super(view);
            tvUri = (TextView)view.findViewById(R.id.tv_uri);
            tvLabel = (TextView)view.findViewById(R.id.tv_label);
            tvDescription = (TextView)view.findViewById(R.id.tv_description);
            tvScore = (TextView)view.findViewById(R.id.tv_score);
            tvRank = (TextView)view.findViewById(R.id.tv_rank);
            tvOptions = (TextView)view.findViewById(R.id.tv_options);
        }
    }
}