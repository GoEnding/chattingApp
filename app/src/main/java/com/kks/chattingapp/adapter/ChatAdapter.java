package com.kks.chattingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.kks.chattingapp.R;
import com.kks.chattingapp.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> chatMessages;
    private String currentUserNickname;

    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;

    public ChatAdapter(List<ChatMessage> chatMessages, String currentUserNickname) {
        this.chatMessages = chatMessages;
        this.currentUserNickname = currentUserNickname;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (currentUserNickname != null && currentUserNickname.equals(message.getUserName())) {
            return VIEW_TYPE_MY_MESSAGE;
        } else {
            return VIEW_TYPE_OTHER_MESSAGE;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_my, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_chat_other, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);

        if (holder.getItemViewType() == VIEW_TYPE_MY_MESSAGE) {
            ((MyMessageViewHolder) holder).bind(message);
        } else {
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    static class MyMessageViewHolder extends RecyclerView.ViewHolder {

        TextView txtMyName, txtMyMessage, txtMyTime;
        CircleImageView profileImage;

        MyMessageViewHolder(View itemView) {
            super(itemView);
            txtMyName = itemView.findViewById(R.id.txtMyName);
            txtMyMessage = itemView.findViewById(R.id.txtMyMessage);
            txtMyTime = itemView.findViewById(R.id.txtMyTime);
            profileImage = itemView.findViewById(R.id.profileImage);
        }

        void bind(ChatMessage message) {
            txtMyName.setText(message.getUserName());
            txtMyMessage.setText(message.getMessage());
            txtMyTime.setText(formatTimestamp(message.getTimestamp()));
            Glide.with(profileImage.getContext()).load(message.getProfileImageUrl()).into(profileImage);
        }
    }

    static class OtherMessageViewHolder extends RecyclerView.ViewHolder {

        TextView txtOtherName, txtOtherMessage, txtOtherTime;
        CircleImageView profileImage2;

        OtherMessageViewHolder(View itemView) {
            super(itemView);
            txtOtherName = itemView.findViewById(R.id.txtOtherName);
            txtOtherMessage = itemView.findViewById(R.id.txtOtherMessage);
            txtOtherTime = itemView.findViewById(R.id.txtOtherTime);
            profileImage2 = itemView.findViewById(R.id.profileImage2);
        }

        void bind(ChatMessage message) {
            txtOtherName.setText(message.getUserName());
            txtOtherMessage.setText(message.getMessage());
            txtOtherTime.setText(formatTimestamp(message.getTimestamp()));
            Glide.with(profileImage2.getContext()).load(message.getProfileImageUrl()).into(profileImage2);
        }
    }

    private static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return sdf.format(timestamp.toDate());
    }
}
