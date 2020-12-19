package com.example.mychart.Find_Friend;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychart.R;
import com.example.mychart.common.Common;
import com.example.mychart.common.Contents;
import com.example.mychart.common.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class MyFindAdapter extends RecyclerView.Adapter<MyFindAdapter.MyViewHolder> {
    private Context mContext;
    private List<FInd_Model> mFInd_models_list;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;
    private String userId;

    public MyFindAdapter(Context context, List<FInd_Model> FInd_models_list) {
        mContext = context;
        mFInd_models_list = FInd_models_list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.find_friend_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final FInd_Model item = mFInd_models_list.get(position);
        holder.userName.setText(item.getUserName());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Common.PROFILEIMAGE + "/" + item.getPhotoName());
        Log.d("checkPath : ", Common.PROFILEIMAGE+ item.getPhotoName());
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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Common.FRIEND_REQUEST);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(item.isRequest())
        {
            holder.btnSendRequest.setVisibility(View.GONE);
            holder.btmCancelRequest.setVisibility(View.VISIBLE);

        }
        else {
            holder.btnSendRequest.setVisibility(View.VISIBLE);
            holder.btmCancelRequest.setVisibility(View.GONE);

        }
        holder.btmCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = item.getUserId();
                holder.btnSendRequest.setVisibility(View.GONE);
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.btmCancelRequest.setVisibility(View.VISIBLE);
                mDatabaseReference.child(mFirebaseUser.getUid()).child(userId).child(Contents.REQUEST_TYPE).setValue(null)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mDatabaseReference.child(userId).child(mFirebaseUser.getUid()).child(Contents.REQUEST_TYPE).setValue(null)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        String title="New friend request";
                                                      //<!--TOdo  mFirebaseUser is known as current user its programming mistaake

                                                        String message="Friend request from"+mFirebaseUser.getDisplayName();
                                                        Util.sendNotification(mContext,title,message,userId);

                                                        holder.btnSendRequest.setVisibility(View.VISIBLE);
                                                        holder.progressBar.setVisibility(View.GONE);
                                                        holder.btmCancelRequest.setVisibility(View.GONE);
                                                        Toast.makeText(mContext, mContext.getString(R.string.Request_sent_successfull), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(mContext, "Reqest not cancel", Toast.LENGTH_SHORT).show();
                                                        holder.btnSendRequest.setVisibility(View.GONE);
                                                        holder.progressBar.setVisibility(View.GONE);
                                                        holder.btmCancelRequest.setVisibility(View.VISIBLE);
                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(mContext, "failed to sent : %1$s" + task.getException(), Toast.LENGTH_SHORT).show();
                                    holder.btnSendRequest.setVisibility(View.GONE);
                                    holder.progressBar.setVisibility(View.GONE);
                                    holder.btmCancelRequest.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });
        holder.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = item.getUserId();
                holder.btnSendRequest.setVisibility(View.VISIBLE);
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.btmCancelRequest.setVisibility(View.GONE);
                mDatabaseReference.child(mFirebaseUser.getUid()).child(userId).child(Contents.REQUEST_TYPE).setValue(Contents.SENT)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    mDatabaseReference.child(userId).child(mFirebaseUser.getUid()).child(Contents.REQUEST_TYPE).setValue(Contents.RECEIVE)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        holder.btnSendRequest.setVisibility(View.GONE);
                                                        holder.progressBar.setVisibility(View.GONE);
                                                        holder.btmCancelRequest.setVisibility(View.VISIBLE);
                                                        Toast.makeText(mContext, mContext.getString(R.string.Request_sent_successfull), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(mContext, "Request not sent", Toast.LENGTH_SHORT).show();
                                                        holder.btnSendRequest.setVisibility(View.VISIBLE);
                                                        holder.progressBar.setVisibility(View.GONE);
                                                        holder.btmCancelRequest.setVisibility(View.GONE);
                                                    }
                                                }
                                            });

                                } else {
                                    Toast.makeText(mContext, "failed to sent : %1$s" + task.getException(), Toast.LENGTH_SHORT).show();
                                    holder.btnSendRequest.setVisibility(View.VISIBLE);
                                    holder.progressBar.setVisibility(View.GONE);
                                    holder.btmCancelRequest.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFInd_models_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfile;
        private TextView userName;
        private Button btnSendRequest;
        private Button btmCancelRequest;
        private View progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivprofile);
            userName = itemView.findViewById(R.id.tvusername);
            btnSendRequest = itemView.findViewById(R.id.btnsend);
            btmCancelRequest = itemView.findViewById(R.id.btnCancel);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
