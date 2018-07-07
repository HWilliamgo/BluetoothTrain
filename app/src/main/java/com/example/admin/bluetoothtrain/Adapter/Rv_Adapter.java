package com.example.admin.bluetoothtrain.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.bluetoothtrain.R;

import java.util.List;

public class Rv_Adapter extends RecyclerView.Adapter<Rv_Adapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onClick(BluetoothDevice device);
    }

    private Context context;
    private List<BluetoothDevice> devicesList;
    private OnItemClickListener onItemClickListener;


    public Rv_Adapter(Context context, List<BluetoothDevice> nameList) {
        this.context = context;
        this.devicesList = nameList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        holder.tvDeviceName.setText(devicesList.get(position).getName());

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onClick(devicesList.get(holder.getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName;
        TextView tvDisconnect;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
            tvDisconnect = itemView.findViewById(R.id.tv_disconnect);
        }
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
