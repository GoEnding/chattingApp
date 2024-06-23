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

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> chatMessages;
    private String currentUser;

    public ChatAdapter(List<ChatMessage> chatMessages, String currentUser) {
        this.chatMessages = chatMessages;
        this.currentUser = currentUser;
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessages.get(position).getNickname() != null && chatMessages.get(position).getNickname().equals(currentUser)) {
            return 0;
        } else {
            return 1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myimagechat_row, parent, false);
            return new MyChatViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.otherimagechat_row, parent, false);
            return new OtherChatViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (holder instanceof MyChatViewHolder) {
            ((MyChatViewHolder) holder).bind(chatMessage);
        } else {
            ((OtherChatViewHolder) holder).bind(chatMessage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class MyChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime, txtName;
        ImageView imgMessage;

        public MyChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMyMessage);
            txtTime = itemView.findViewById(R.id.txtMyTime);
            txtName = itemView.findViewById(R.id.txtMyName);
            imgMessage = itemView.findViewById(R.id.imgMessage);
        }

        public void bind(ChatMessage chatMessage) {
            txtName.setText(chatMessage.getNickname());
            txtTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.getTimestamp().toDate()));

            if (chatMessage.getMessage() != null) {
                txtMessage.setVisibility(View.VISIBLE);
                txtMessage.setText(chatMessage.getMessage());
                imgMessage.setVisibility(View.GONE);
            } else if (chatMessage.getImageUrl() != null) {
                imgMessage.setVisibility(View.VISIBLE);
                Glide.with(imgMessage.getContext())
                        .load(chatMessage.getImageUrl())
                        .placeholder(R.drawable.default_profile_image)
                        .into(imgMessage);
                txtMessage.setVisibility(View.GONE);
            } else {
                txtMessage.setVisibility(View.GONE);
                imgMessage.setVisibility(View.GONE);
            }
        }
    }

    static class OtherChatViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTime, txtName;
        ImageView imgMessage;

        public OtherChatViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtOtherMessage);
            txtTime = itemView.findViewById(R.id.txtOtherTime);
            txtName = itemView.findViewById(R.id.txtOtherName);
            imgMessage = itemView.findViewById(R.id.imgMessageOther);
        }

        public void bind(ChatMessage chatMessage) {
            txtName.setText(chatMessage.getNickname());
            txtTime.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(chatMessage.getTimestamp().toDate()));

            if (chatMessage.getMessage() != null) {
                txtMessage.setVisibility(View.VISIBLE);
                txtMessage.setText(chatMessage.getMessage());
                imgMessage.setVisibility(View.GONE);
            } else if (chatMessage.getImageUrl() != null) {
                imgMessage.setVisibility(View.VISIBLE);
                Glide.with(imgMessage.getContext())
                        .load(chatMessage.getImageUrl())
                        .placeholder(R.drawable.default_profile_image)
                        .into(imgMessage);
                txtMessage.setVisibility(View.GONE);
            } else {
                txtMessage.setVisibility(View.GONE);
                imgMessage.setVisibility(View.GONE);
            }
        }
    }
}
