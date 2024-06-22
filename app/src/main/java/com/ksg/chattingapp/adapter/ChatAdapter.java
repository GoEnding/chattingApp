package com.ksg.chattingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ksg.chattingapp.R;
import com.ksg.chattingapp.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> chatMessages;
    private String currentUser;

    public ChatAdapter(List<ChatMessage> chatMessages, String currentUser) {
        this.chatMessages = chatMessages;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_my, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_other, parent, false);
        }
        return new ChatViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        holder.txtMessage.setText(chatMessage.getMessage());
        holder.txtTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.getTimestamp().toDate()));
        holder.txtName.setText(chatMessage.getNickname());


        if (chatMessage.getMessage() != null) {

            holder.txtMessage.setText(chatMessage.getMessage());

        } else if (chatMessage.getImageUrl() != null) {

            Glide.with(holder.imgMessage.getContext()).load(chatMessage.getImageUrl()).into(holder.imgMessage);

        } else {


        }

        // 이미지 관련 코드는 제거
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getNickname() != null && chatMessages.get(position).getNickname().equals(currentUser)) {
            return 0;
        } else {
            return 1;
        }
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime, txtName;

        ImageView imgMessage;



        public ChatViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) {
                txtMessage = itemView.findViewById(R.id.txtMyMessage);
                txtTime = itemView.findViewById(R.id.txtMyTime);
                txtName = itemView.findViewById(R.id.txtMyName);
                imgMessage = itemView.findViewById(R.id.imgMessage);

            } else {
                txtMessage = itemView.findViewById(R.id.txtOtherMessage);
                txtTime = itemView.findViewById(R.id.txtOtherTime);
                txtName = itemView.findViewById(R.id.txtOtherName);
                imgMessage = itemView.findViewById(R.id.imgMessage);
            }
        }
    }
}
