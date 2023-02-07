package ru.qualitylab.evotor.evotorMKH;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewClass> {
    ArrayList<JSONObject> array;
    Context context;
    Boolean completed;

    public RecyclerAdapter(ArrayList<JSONObject> array, Boolean completed, Context context) {
        // Get parameter for displaying list
        this.array = array;
        this.context = context;
        this.completed = completed;
    }

    @NonNull
    @Override
    public MyViewClass onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Setting the element design
        // Setting displaying simple and complete order
        View view = LayoutInflater.from(parent.getContext()).inflate((completed ? R.layout.row_complited : R.layout.row),parent,false);
        return new MyViewClass(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewClass holder, @SuppressLint("RecyclerView") final int position) {
        // Setting values for an element
        try {
            holder.item_address.setText(array.get(position).getString("АдресПолучателя"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Setting click element
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (v.getContext(), ItemOrder.class);

                intent.putExtra("json", array.get(position).toString());
                v.getContext().startActivity(intent);
                }
            }
        );

    }

    @Override
    public int getItemCount() {
        // Set the list length for efficiency
        return array.size();
    }

    public class MyViewClass extends RecyclerView.ViewHolder{
        TextView item_address;
        public MyViewClass(@NonNull View itemView) {
            super(itemView);
            item_address = itemView.findViewById(R.id.item_address);
        }
    }

}
