package com.example.mychart.sendMessage;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychart.R;
import com.example.mychart.common.Common;
import com.example.mychart.common.Contents;
import com.example.mychart.selectedMessage.SelectedMessage;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessageAdpter extends RecyclerView.Adapter<MessageAdpter.MessageViewHolder>{
    private Context mContext;
    private List<MessageModel> mMessageModelsList;
    private FirebaseAuth mFirebaseAuth;
    private ActionMode mActionMode;
    private ConstraintLayout selectedView;
    public MessageAdpter(Context context, List<MessageModel> messageModelsList) {
        mContext = context;
        mMessageModelsList = messageModelsList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.message_view,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        MessageModel messageModel=mMessageModelsList.get(position);
        mFirebaseAuth=FirebaseAuth.getInstance();
        String currentUserID=mFirebaseAuth.getCurrentUser().getUid();
        String FromUserId=messageModel.getFROM();
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd-mm-yyyy HH:mm");
        String DateTime=dateFormat.format(new Date(messageModel.getMessage_TIME()));
        String[] spliteString=DateTime.split(" ");
        String messageTime=spliteString[1];
        if(FromUserId.equals(currentUserID))
        {
            if (messageModel.getMessage_type().equals(Contents.Type_text))
            {
                holder.mLinearLayout1.setVisibility(View.VISIBLE);
                holder.llsendImage.setVisibility(View.GONE);

            }
            else {
                holder.mLinearLayout1.setVisibility(View.GONE);
                holder.llsendImage.setVisibility(View.VISIBLE);
            }


                 holder.llreceiveImage.setVisibility(View.GONE);
                holder.mLinearLayout2.setVisibility(View.GONE);
                holder.tvsendMessage.setText(messageModel.getMessage());
                 holder.sendTime.setText(messageTime);
                 holder.tvsendImageTime.setText(messageTime);
                Glide.with(mContext)
                    .load(messageModel.getMessage())
                    .placeholder(R.drawable.sendimage)
                    .into(holder.sendImage);
        }else
        {
            if(messageModel.getMessage_type().equals(Contents.Type_text))
            {
                holder.mLinearLayout2.setVisibility(View.VISIBLE);
                holder.llreceiveImage.setVisibility(View.GONE);
            }
            else
            {
                holder.mLinearLayout2.setVisibility(View.GONE);
                holder.llreceiveImage.setVisibility(View.VISIBLE);
            }
            holder.llsendImage.setVisibility(View.GONE);
            holder.mLinearLayout1.setVisibility(View.GONE);

            holder.RecieveMessage.setText(messageModel.getMessage());
            holder.receiveTime.setText(messageTime);
            holder.tvreceiveImageTime.setText(messageTime);
            Glide.with(mContext)
                    .load(messageModel.getMessage())
                    .placeholder(R.drawable.sendimage)
                    .into(holder.receiveImage);
        }

        holder.mConstraintLayout.setTag(R.id.TAG_MESSAGE,messageModel.getMessage());
        holder.mConstraintLayout.setTag(R.id.TAG_MESSAGE_ID,messageModel.getMessage_id());
        holder.mConstraintLayout.setTag(R.id.TAG_MESSAGE_TYPE,messageModel.getMessage_type());
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageType=v.getTag(R.id.TAG_MESSAGE_TYPE).toString();
                Uri uri=Uri.parse(v.getTag(R.id.TAG_MESSAGE).toString());
                if(messageType.equals(Common.VIDEO))
                {
                    Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                    intent.setDataAndType(uri,"video/mp4");
                    mContext.startActivity(intent);
                }
                else if(messageType.equals(Common.IMAGE))
                {
                    Intent intent=new Intent(Intent.ACTION_VIEW,uri);
                    intent.setDataAndType(uri,"image/jpg");
                    mContext.startActivity(intent);
                }
            }
        });


        holder.mConstraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(mContext, "long clicked", Toast.LENGTH_SHORT).show();
                if(mActionMode!=null)
                return false;

                selectedView=holder.mConstraintLayout;
                mActionMode=((AppCompatActivity)mContext).startSupportActionMode(mCallback);
               holder.mConstraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                return true;

            }
        });
        }




    @Override
    public int getItemCount() {
        return mMessageModelsList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLinearLayout1,mLinearLayout2;
        private ConstraintLayout mConstraintLayout;
        private TextView tvsendMessage,RecieveMessage;
        private TextView sendTime,receiveTime;
        private TextView tvsendImageTime,tvreceiveImageTime;
        private ImageView sendImage,receiveImage;
        private LinearLayout llsendImage,llreceiveImage;

            public MessageViewHolder(@NonNull View itemView) {
                super(itemView);
                mConstraintLayout=itemView.findViewById(R.id.constraintlayout);
                mLinearLayout1=itemView.findViewById(R.id.linearLayout1);
                mLinearLayout2=itemView.findViewById(R.id.linearlayout2);
                tvsendMessage=itemView.findViewById(R.id.tvMessagesent);
                RecieveMessage=itemView.findViewById(R.id.tvMessageRecieve);
                sendTime=itemView.findViewById(R.id.tvMessageTimeSent);
                receiveTime=itemView.findViewById(R.id.tvMessageTimeReceive);
                tvsendImageTime=itemView.findViewById(R.id.tvsendimageTime);
                tvreceiveImageTime=itemView.findViewById(R.id.tvreceveimageTime);
                sendImage=itemView.findViewById(R.id.sendImage);
                receiveImage=itemView.findViewById(R.id.receiveImage);
                llsendImage=itemView.findViewById(R.id.llsendimage);
                llreceiveImage=itemView.findViewById(R.id.llreceiveimage);
            }
        }


      public   ActionMode.Callback mCallback=new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater=mode.getMenuInflater();
              inflater.inflate(R.menu.chat_menu_option,menu);

              String selectedMessageType=String.valueOf(selectedView.getTag(R.id.TAG_MESSAGE_TYPE));
              if(selectedMessageType.equals(Contents.Type_text))
              {
                  MenuItem itemDownLoad=menu.findItem(R.id.download);
                  itemDownLoad.setVisible(false);

              }
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                String selectMessageId=String.valueOf(selectedView.getTag(R.id.TAG_MESSAGE_ID));
                String selectedMessage=String.valueOf(selectedView.getTag(R.id.TAG_MESSAGE));
                String selectMessageType=String.valueOf(selectedView.getTag(R.id.TAG_MESSAGE_TYPE));

                int itemclick=item.getItemId();
                switch (itemclick)
                {
                    case R.id.forward:
                        if(mContext instanceof SendMessage)
                        {
                            ((SendMessage)mContext).ForWord(selectedMessage,selectMessageType,selectMessageId);
                        }
                       mode.finish();
                        break;
                    case R.id.delete:
                      if(mContext instanceof SendMessage)
                      {
                          ((SendMessage)mContext).deleteMessage(selectMessageType,selectMessageId);
                      }
                        mode.finish();
                        break;
                    case R.id.share:
                      if(selectMessageType.equals(Contents.Type_text))
                      {
                          Intent intentShare=new Intent();
                          intentShare.setAction(Intent.ACTION_SEND);
                          intentShare.putExtra(Intent.EXTRA_TEXT,selectedMessage);
                          intentShare.setType("text/plain");
                          mContext.startActivity(intentShare);
                      }else
                      {
                          if(mContext instanceof  SendMessage)
                          {
                              ((SendMessage)mContext).DownLoadFile(selectMessageType,selectMessageId,true);
                          }
                      }
                        mode.finish();
                        break;
                    case R.id.download:
                        if(mContext instanceof SendMessage)
                        {
                            ((SendMessage)mContext).DownLoadFile(selectMessageType,selectMessageId,false);
                        }
                               mode.finish();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode=null;
                selectedView.setBackgroundColor(mContext.getResources().getColor(R.color.chat_color));

            }
        };
}
