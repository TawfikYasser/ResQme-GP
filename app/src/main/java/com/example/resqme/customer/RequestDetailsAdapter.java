package com.example.resqme.customer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.resqme.R;
import com.example.resqme.model.RequestDetailsModel;
import com.example.resqme.model.WinchRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RequestDetailsAdapter extends RecyclerView.Adapter<RequestDetailsAdapter.RequestDetailsAdapterViewHolder> {

    Context context, context_2;
    ArrayList<RequestDetailsModel> requestDetails;
    DatabaseReference WinchRequests;
    FirebaseAuth firebaseAuth;
    View view_2;
    String fixType = "";
    public RequestDetailsAdapter(Context context, ArrayList<RequestDetailsModel> requestDetails,DatabaseReference WinchRequests
            , View view, Context context_2) {
        this.context = context;
        this.requestDetails = requestDetails;
        this.WinchRequests = WinchRequests;
        this.view_2 = view;
        firebaseAuth = FirebaseAuth.getInstance();
        this.context_2 = context_2;
    }

    @NonNull
    @Override
    public RequestDetailsAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_details_item, parent, false);
        return new RequestDetailsAdapter.RequestDetailsAdapterViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return requestDetails.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RequestDetailsAdapterViewHolder holder, @SuppressLint("RecyclerView") int position) {

        DatabaseReference winchRequests = FirebaseDatabase.getInstance().getReference().child("WinchRequests");

        // Check which items was fixed
        if(requestDetails.get(position).getBattery().equals("1")){// If battery was fixed
            holder.tvRequestDetailsFixedItem.setText("البطارية");
        }
        else if(requestDetails.get(position).getLights().equals("1")){// If lights was fixed
            holder.tvRequestDetailsFixedItem.setText("الإضاءة");
        }
        else if(requestDetails.get(position).getOil().equals("1")){// If oil was fixed
            holder.tvRequestDetailsFixedItem.setText("الزيت");
        }
        else if(requestDetails.get(position).getFilter().equals("1")){// If filter was fixed
            holder.tvRequestDetailsFixedItem.setText("الفلتر");
        }
        else if(requestDetails.get(position).getTier().equals("1")){// If tier was fixed
            holder.tvRequestDetailsFixedItem.setText("الكاوتش");
        }
        else if(requestDetails.get(position).getEngine().equals("1")){// If engine was fixed
            holder.tvRequestDetailsFixedItem.setText("الموتور");
        }
        else if(!requestDetails.get(position).getOther().isEmpty()){// If other item was fixed
            holder.tvRequestDetailsFixedItem.setText(requestDetails.get(position).getOther().toString());
        }


        winchRequests.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    WinchRequest winchReq = dataSnapshot.getValue(WinchRequest.class);
                    if (winchReq.getWinchRequestID().equals(requestDetails.get(position).getWinchRequestId())) {
                        holder.tvRequestItemDescription.setText(winchReq.getWinchRequestDescription());
                        holder.tvWinchRequestServiceCost.setText(winchReq.getServiceCost());
                        holder.tvWinchRequestTimestamp.setText(winchReq.getWinchRequestInitiationDate());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }





    class RequestDetailsAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvRequestItemDescription, tvRequestDetailsFixedItem, tvWinchRequestServiceCost, tvWinchRequestTimestamp;

        public RequestDetailsAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequestItemDescription = itemView.findViewById(R.id.winch_request_item_description_txt_car_history);
            tvRequestDetailsFixedItem = itemView.findViewById(R.id.request_details_fixed_item_txt_car_history);
            tvWinchRequestServiceCost = itemView.findViewById(R.id.winch_request_service_cost_txt_car_history);
            tvWinchRequestTimestamp = itemView.findViewById(R.id.winch_request_timestamp_txt_car_history);


        }
    }
}
