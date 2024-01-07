package com.example.MusicX;

import android.content.Context;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder>{

    ArrayList<AudioModel>songsList;
    Context context;

    public MusicAdapter(ArrayList<AudioModel> songsList, Context context){
        this.songsList = songsList;
        this.context = context;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item,parent,false);
        return new MusicAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicAdapter.ViewHolder holder , int position) {
        AudioModel SongData = songsList.get(position);
        holder.getAdapterPosition();
        holder.titleTextView.setText(SongData.getTitle());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Navigate to another activity

                MyMediaPlayer.getInstance().reset();
                MyMediaPlayer.currentIndex = holder.getAdapterPosition();
                Intent intent = new Intent(context,MusicPlayerActivity.class);
                intent.putExtra("LIST",songsList);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {

        return songsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

    TextView titleTextView;
    ImageView iconView;

        public ViewHolder( View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.title_view);
            iconView = itemView.findViewById(R.id.icon_view);
        }
    }

}
