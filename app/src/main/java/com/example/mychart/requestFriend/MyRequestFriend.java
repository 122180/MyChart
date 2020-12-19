package com.example.mychart.requestFriend;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MyRequestFriend extends RecyclerView.Adapter<MyRequestFriend.RequestViewHolder> {
    private Context mContext;
    private List<RequestModel> mRequestModelList;
    private FirebaseUser CurrentUser;
    private DatabaseReference mDatabaseReference, mDatabaseReferenceChat;

    public MyRequestFriend(Context context, List<RequestModel> requestModelList) {
        mContext = context;
        mRequestModelList = requestModelList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.requesfriend_view, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RequestViewHolder holder, int position) {
        final RequestModel item = mRequestModelList.get(position);

        holder.UserName.setText(item.getUserName());

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Common.PROFILEIMAGE + "/" + item.getPhotoName());
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
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Common.FRIEND_REQUEST);
        mDatabaseReferenceChat = FirebaseDatabase.getInstance().getReference().child(Common.CHAT);

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                final String userId = item.getUserId();
                mDatabaseReferenceChat.child(CurrentUser.getUid()).child(userId).child(Common.TIME_STAMP)
                        .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mDatabaseReferenceChat.child(userId).child(CurrentUser.getUid()).child(Common.TIME_STAMP)
                                    .setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mDatabaseReference.child(CurrentUser.getUid()).child(userId).child(Contents.REQUEST_TYPE)
                                                .setValue(Contents.REQUEST_STATUS_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mDatabaseReference.child(userId).child(CurrentUser.getUid()).child(Contents.REQUEST_TYPE)
                                                            .setValue(Contents.REQUEST_STATUS_ACCEPTED).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                String title="Friend Request Accepted";
                                                                //<!--TOdo  mFirebaseUser is known as current user its programming mistaake

                                                                String message="Friend request accepted by"+CurrentUser.getDisplayName();
                                                                Util.sendNotification(mContext,title,message,userId);
                                                                holder.mProgressBar.setVisibility(View.GONE);
                                                                holder.btnAccept.setVisibility(View.VISIBLE);
                                                                holder.btnCancel.setVisibility(View.VISIBLE);
                                                            } else {
                                                                showMessage(holder, task);
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    showMessage(holder, task);
                                                }
                                            }
                                        });

                                    } else {
                                        showMessage(holder, task);
                                    }
                                }
                            });

                        } else {
                            showMessage(holder, task);
                        }
                    }
                });


            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mProgressBar.setVisibility(View.VISIBLE);
                holder.btnAccept.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);


                final String UserId = item.getUserId();
                mDatabaseReference.child(CurrentUser.getUid()).child(UserId).child(Contents.REQUEST_TYPE)
                        .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        holder.mProgressBar.setVisibility(View.GONE);
                        holder.btnAccept.setVisibility(View.VISIBLE);
                        holder.btnCancel.setVisibility(View.VISIBLE);
                        if (task.isSuccessful()) {
                            mDatabaseReference.child(UserId).child(CurrentUser.getUid()).child(Contents.REQUEST_TYPE)
                                    .setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        holder.mProgressBar.setVisibility(View.GONE);
                                        holder.btnAccept.setVisibility(View.VISIBLE);
                                        holder.btnCancel.setVisibility(View.VISIBLE);
                                        String title="Friend Request Accepted";


                                        String message="Friend request Denied by"+CurrentUser.getDisplayName();
                                        Util.sendNotification(mContext,title,message,UserId);
                                        Toast.makeText(mContext, "Friend request is cancel", Toast.LENGTH_SHORT).show();
                                    } else {
                                        showMessage(holder, task);
                                    }
                                }
                            });
                        } else {
                            showMessage(holder, task);

                        }
                    }
                });

            }
        });


    }

    private void showMessage(RequestViewHolder holder, Task<Void> task) {
        holder.mProgressBar.setVisibility(View.GONE);
        holder.btnAccept.setVisibility(View.VISIBLE);
        holder.btnCancel.setVisibility(View.VISIBLE);
        Toast.makeText(mContext, "Rquest not Canceled" + task.getException(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mRequestModelList.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProfile;
        private Button btnAccept, btnCancel;
        private ProgressBar mProgressBar;
        private TextView UserName;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivprofile);
            btnAccept = itemView.findViewById(R.id.btnaccept);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            mProgressBar = itemView.findViewById(R.id.progressBar);
            UserName = itemView.findViewById(R.id.username);
        }
    }
}
