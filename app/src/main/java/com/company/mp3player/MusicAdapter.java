package com.company.mp3player;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicHolder> {

    ArrayList<String> list;
    Context mContext;

    public MusicAdapter(ArrayList<String> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MusicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_song, parent, false);
        return new MusicHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicHolder holder, int position) {
        String filePath = list.get(position);
        String songTitle = filePath.substring(filePath.lastIndexOf("/") + 1);
        holder.songName.setText(songTitle);
        holder.cardView.setOnClickListener(v -> {
            Intent i = new Intent(mContext, Play_Activity.class);
            i.putExtra("filePath", filePath);
            i.putExtra("songTitle", songTitle);
            i.putExtra("position", position);
            i.putExtra("list", list);
            mContext.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MusicHolder extends RecyclerView.ViewHolder {
        private final TextView songName;
        private final CardView cardView;
        public MusicHolder(@NonNull View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.songName);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
