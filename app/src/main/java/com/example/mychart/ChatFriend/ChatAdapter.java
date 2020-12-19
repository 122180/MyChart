package com.example.mychart.ChatFriend;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychart.Extra;
import com.example.mychart.R;
import com.example.mychart.common.Common;

import com.example.mychart.common.Util;
import com.example.mychart.sendMessage.SendMessage;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private Context mContext;
    private List<ChatModel> mChatModelList;

    public ChatAdapter(Context context, List<ChatModel> chatModelList) {
        mContext = context;
        mChatModelList = chatModelList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.chat_view,parent,false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatViewHolder holder, int position) {
        final ChatModel item=mChatModelList.get(position);
        holder.UserName.setText(item.getUserName());

            if(!item.getUnReadCountMessage().equals("0"))
            {
                holder.unReadMessage.setVisibility(View.VISIBLE);
                holder.unReadMessage.setText(item.getUnReadCountMessage());
            }else
            {
                holder.unReadMessage.setVisibility(View.GONE);

            }
         String lastMessage=item.getLastMessage();
            lastMessage=lastMessage.length()>30?lastMessage.substring(0,30):lastMessage;
        holder.tvlastMessage.setText(lastMessage);

        String lastMessageTime=item.getLastMessageTime();
        if(lastMessageTime==null)
            lastMessageTime="";
      if(!TextUtils.isEmpty(lastMessageTime))
          holder.tvlastMessageTime.setText(Util.timeAgo(Long.parseLong(lastMessageTime)));

        StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(Common.PROFILEIMAGE+item.getPhotoName());

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(mContext)
                        .load(uri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(holder.ivProfile);
            }
        });

holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent=new Intent(mContext, SendMessage.class);
        intent.putExtra(Extra.CHAT_ID,item.getUserId());
        intent.putExtra(Extra.USER_NAME,item.getUserName());
        intent.putExtra(Extra.PHOTO_NAME,item.getPhotoName());
        mContext.startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
        return mChatModelList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView UserName,tvlastMessage,tvlastMessageTime,unReadMessage;
        private ImageView ivProfile;
        private LinearLayout mLinearLayout;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            UserName=itemView.findViewById(R.id.userName);
            tvlastMessage=itemView.findViewById(R.id.lastmessage);
            tvlastMessageTime=itemView.findViewById(R.id.lastTime);
            unReadMessage=itemView.findViewById(R.id.unreadMeassage);
            ivProfile=itemView.findViewById(R.id.ivprofile);
            mLinearLayout=itemView.findViewById(R.id.linearlayout);

        }
    }
}
