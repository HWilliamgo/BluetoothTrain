package com.example.admin.bluetoothtrain.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerViewAccessibilityDelegate;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.bluetoothtrain.R;

import java.util.List;

public class RvPairedDeviceAdapter extends RecyclerView.Adapter<RvPairedDeviceAdapter.MyViewHolder> {
    private List<BluetoothDevice> pairedDevice;
    private Context context;

    public RvPairedDeviceAdapter(List<BluetoothDevice> pairedDevice, Context context) {
        this.pairedDevice = pairedDevice;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.rv_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = pairedDevice.get(position).getName();
        if (TextUtils.isEmpty(name)) {
            holder.tvDeviceName.setText(name);
        } else {
            holder.tvDeviceName.setText(pairedDevice.get(position).getAddress());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("hanwuji", "onClick: 点击了配对设备的列表");
            }
        });
    }

    @Override
    public int getItemCount() {
        return pairedDevice.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvDeviceName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvDeviceName = itemView.findViewById(R.id.tv_device_name);
        }
    }
}
