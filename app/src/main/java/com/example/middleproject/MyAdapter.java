package com.example.middleproject;




import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<DataClass> dataList;
    private Context context;
    public MyAdapter(Context context, ArrayList<DataClass> dataList) {

        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String img =dataList.get(position).getImageURL();
        Glide.with(context).load(dataList.get(position).getImageURL()).into(holder.recyclerImage);
        holder.recyclerCaption.setText(dataList.get(position).getCaption());
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the key of the item to be deleted
                String key = dataList.get(holder.getAdapterPosition()).getKey();

                // Get a reference to the image in Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(img);

                // Delete the image
                storageRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Image deleted successfully from Firebase Storage
                            // Now, remove the corresponding entry from Firebase Realtime Database
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");
                            databaseReference.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Item deleted from Firebase Realtime Database
                                        // Now, remove the item from the RecyclerView and update the dataList
                                        dataList.remove(holder.getAdapterPosition());
                                        notifyItemRemoved(holder.getAdapterPosition());
                                    } else {
                                        // Failed to delete item from Firebase Realtime Database
                                        // Handle the error
                                    }
                                }
                            });
                        } else {
                            // Failed to delete image from Firebase Storage
                            // Handle the error
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerCaption;

        ImageView btn_delete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCaption = itemView.findViewById(R.id.recyclerCaption);
            btn_delete= itemView.findViewById(R.id.btn_delete);

        }

    }

}