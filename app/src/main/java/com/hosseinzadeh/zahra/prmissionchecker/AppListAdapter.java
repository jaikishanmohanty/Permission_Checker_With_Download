package com.hosseinzadeh.zahra.prmissionchecker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;




public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {
    private ArrayList<AppItem> mApps = new ArrayList<>();
    private Context mContext;
    OnAppClickListener onAppClickListener;



    public AppListAdapter(ArrayList<AppItem> apps, Context mContext) {
        this.mApps = apps;
        this.mContext = mContext;
    }

    public void setClickListener(OnAppClickListener itemClickListener) {
        this.onAppClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.app_item, viewGroup, false);
        AppViewHolder holder = new AppViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder appViewHolder, int i) {
        appViewHolder.bindApp(this.mApps.get(i));
    }

    @Override
    public int getItemCount() {
        return this.mApps.size();
    }

    public class AppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView mAppIcon;
        TextView mAppName;
        private Context mContext;

        public AppViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            mAppIcon = itemView.findViewById(R.id.appIcon);
            mAppName = itemView.findViewById(R.id.tvAppName);
            itemView.setOnClickListener(this);
        }

        public void bindApp(AppItem app){
            this.mAppName.setText(app.getAppName());
            this.mAppIcon.setImageDrawable(app.getAppIcon());
        }

        @Override
        public void onClick(View v) {
            if (onAppClickListener != null) onAppClickListener.onAppSelected(v,getAdapterPosition());
        }
    }
}
