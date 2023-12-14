package com.example.mytestpeoject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mytestpeoject.Utils.LocationManagerService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class LocarionServicePractise extends AppCompatActivity {

    List<Location> locList = new ArrayList<>();
    RecyclerView rvList;
    LocationListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locarion_service_practise);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        rvList.setLayoutManager(new LinearLayoutManager(LocarionServicePractise.this));
        adapter = new LocationListAdapter(locList);
        rvList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        startService(new Intent(LocarionServicePractise.this, LocationManagerService.class));
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update your ListView here
            if(intent != null){
                String json = intent.getStringExtra("listData");
                if(json !=  null && !json.isEmpty()) {


                    List<Location> list = new Gson().fromJson(json, new TypeToken<List<Location>>() {
                    }.getType());
                    if(list != null && !list.isEmpty()){
                        locList.addAll(list);
                        updateListView();
                    }else{
                        Toast.makeText(context,"Location List is Null",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(context,"Location Json is Null",Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(context,"Intent is Null",Toast.LENGTH_LONG).show();
            }

        }
    };
    private void updateListView(){
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("your.package.action.UPDATE_LIST_VIEW");
        registerReceiver(receiver, filter);
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    class LocationListAdapter extends RecyclerView.Adapter<LocationListAdapter.ViewHolder>{

        private List<Location>dktsList;

        LocationListAdapter(List<Location> list){
            dktsList = list;
        }


        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item,viewGroup,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

            String displayText = dktsList.get(i).getLatitude()+" - "+dktsList.get(i).getLongitude();
            viewHolder.getDktNo().setText(displayText);


        }

        @Override
        public int getItemCount() {
            return dktsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            private final TextView dktNo;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                dktNo = (TextView) itemView.findViewById(R.id.tv_data);

            }

            public TextView getDktNo() {
                return dktNo;
            }

        }

    }

}