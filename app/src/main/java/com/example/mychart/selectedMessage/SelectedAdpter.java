package com.example.mychart.selectedMessage;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mychart.R;
import com.example.mychart.common.Common;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SelectedAdpter extends RecyclerView.Adapter<SelectedAdpter.viewHolders> {

 private List<SeltedModel> mSeltedModelsList;
 private Context mContext;

    public SelectedAdpter(List<SeltedModel> seltedModelsList, Context context) {
        mSeltedModelsList = seltedModelsList;
        mContext = context;
    }

    @NonNull
    @Override
    public viewHolders onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(mContext).inflate(R.layout.share_with_friend_view,parent,false);
        return new viewHolders(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolders holder, int position) {
        final SeltedModel messageItem=mSeltedModelsList.get(position);

        holder.tvUsername.setText(messageItem.getUserName());
        StorageReference reference= FirebaseStorage.getInstance().getReference().child(Common.PROFILEIMAGE+"/"+messageItem.getPhotoName());
        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext)
                        .load(uri)
                        .placeholder(R.drawable.default_profile)
                        .error(R.drawable.default_profile)
                        .into(holder.pvimage);

            }
        });

       holder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(mContext instanceof SelectedMessage)
               {
                   ((SelectedMessage)mContext).returnSeletedFriend(messageItem.getUserName(),messageItem.getUserId(),messageItem.getPhotoName()+".jpg");
               }
           }
       });
    }

    @Override
    public int getItemCount() {
        return mSeltedModelsList.size();
    }

    public class viewHolders extends RecyclerView.ViewHolder {
        private ImageView pvimage;
        private TextView tvUsername;
        private LinearLayout mLinearLayout;

        public viewHolders(@NonNull View itemView) {
            super(itemView);
            pvimage=itemView.findViewById(R.id.profile_image);
            tvUsername=itemView.findViewById(R.id.tvusername);
            mLinearLayout=itemView.findViewById(R.id.linearlayoutt);

        }
    }
}
