package com.brine.hai.led.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.brine.hai.led.DBpediaRanker.Model.SLDResult;
import com.brine.hai.led.R;

import java.util.List;

/**
 * Created by phamhai on 15/01/2017.
 */

public class SLDAdapter extends RecyclerView.Adapter<SLDAdapter.ViewHolder> {
    private static final String TAG = SLDAdapter.class.getCanonicalName();
    private Context mContext;
    private List<SLDResult> mSearchResults;
    private SLDCallBack mCallBack;

    public interface SLDCallBack{
        void showDetailsUriSLD(String uri);
        void addSearchExploratorySLD(String label, String uri);
    }

    public SLDAdapter(Context context, List<SLDResult> searchResults, SLDCallBack callBack){
        this.mContext = context;
        this.mSearchResults = searchResults;
        this.mCallBack = callBack;
    }

    @Override
    public SLDAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sld_item_row, parent, false);
        return new SLDAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final SLDResult result = mSearchResults.get(position);
        holder.tvUri.setText(result.getUri());
        holder.tvLabel.setText(result.getLabel());
        holder.tvAbtract.setText(result.getAbtract());
        holder.tvOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showOptionMenu(view, result);
            }
        });
    }

    private void showOptionMenu(View view, final SLDResult result){
        PopupMenu popupMenu = new PopupMenu(mContext, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_sld_option, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_details:
                        mCallBack.showDetailsUriSLD(result.getUri());
                        return true;
                    case R.id.menu_add_search:
                        mCallBack.addSearchExploratorySLD(result.getLabel(), result.getUri());
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

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvUri;
        TextView tvLabel;
        TextView tvAbtract;
        TextView tvOptions;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUri = (TextView) itemView.findViewById(R.id.tv_uri);
            tvLabel = (TextView) itemView.findViewById(R.id.tv_label);
            tvAbtract = (TextView) itemView.findViewById(R.id.tv_abtract);
            tvOptions = (TextView) itemView.findViewById(R.id.tv_options);
        }
    }
}
