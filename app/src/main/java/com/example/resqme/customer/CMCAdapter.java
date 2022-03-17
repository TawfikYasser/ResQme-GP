package com.example.resqme.customer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.resqme.R;
import com.example.resqme.model.CMC;

import java.util.ArrayList;

public class CMCAdapter extends RecyclerView.Adapter<CMCAdapter.CMCViewHolder> {

    Context context;
    ArrayList<CMC> cmcs;

    public CMCAdapter(Context context, ArrayList<CMC> cmcs) {
        this.context = context;
        this.cmcs = cmcs;
    }

    @NonNull
    @Override
    public CMCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cmc_item_customer_home, parent, false);
        return new CMCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CMCViewHolder holder, int position) {
        CMC cmc = cmcs.get(position);
        Glide.with(context).load(cmc.getCmcImage()).into(holder.cmcImage);
        holder.cmcName.setText(cmc.getCmcName());
        holder.cmcLocation.setText(cmc.getCmcLocation());
        holder.cmcBrand.setText(cmc.getCmcBrand());
    }

    @Override
    public int getItemCount() {
        return cmcs.size();
    }

    public class CMCViewHolder extends RecyclerView.ViewHolder{
        ImageView cmcImage;
        TextView cmcName, cmcLocation, cmcBrand;
        public CMCViewHolder(@NonNull View itemView) {
            super(itemView);
            cmcImage = itemView.findViewById(R.id.cmc_item_image);
            cmcName = itemView.findViewById(R.id.cmc_name_item);
            cmcLocation = itemView.findViewById(R.id.cmc_location_item);
            cmcBrand = itemView.findViewById(R.id.cmc_brand_item);
        }
    }
}
