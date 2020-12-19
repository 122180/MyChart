package com.example.mychart.Find_Friend;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mychart.Find_Friend.FInd_Model;
import com.example.mychart.Find_Friend.MyFindAdapter;
import com.example.mychart.R;
import com.example.mychart.common.Common;
import com.example.mychart.common.Contents;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Find_Request extends Fragment {
    private List<FInd_Model> mFInd_models_listl;
    private RecyclerView mRecyclerView;
    private MyFindAdapter mMyFindAdapter;
    private DatabaseReference mDatabaseReference,mDatabaseReferenceRquest ;
    private FirebaseUser currentUser;
    private TextView showMesage;
    private View progressbar;



    public Find_Request() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_find__request, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView=view.findViewById(R.id.findRecyleview);
        showMesage=view.findViewById(R.id.tvfindMessage);
        progressbar=view.findViewById(R.id.progressBar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFInd_models_listl=new ArrayList<>();

        mDatabaseReference= FirebaseDatabase.getInstance().getReference().child(Common.USERS);
        currentUser= FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReferenceRquest=FirebaseDatabase.getInstance().getReference().child(Common.FRIEND_REQUEST).child(currentUser.getUid());
        mMyFindAdapter=new MyFindAdapter(getContext(),mFInd_models_listl);
        mRecyclerView.setAdapter(mMyFindAdapter);
        showMesage.setVisibility(View.VISIBLE);
        progressbar.setVisibility(View.VISIBLE);
        Log.d("user Info :","app is run");
        Query query=mDatabaseReference.orderByChild(Common.USERS);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFInd_models_listl.clear();

                for(DataSnapshot ds:snapshot.getChildren())
                {
                    final String userId=ds.getKey();
                    if(userId.equals(currentUser.getUid()))
                    {
                        showMesage.setVisibility(View.VISIBLE);
                        progressbar.setVisibility(View.GONE);

                    }else {


                        if (ds.child(Common.NAME).getValue() != null) {
                            final String name = ds.child(Common.NAME).getValue().toString();
                            final String photoName = ds.child(Common.PHOTO).getValue().toString();

                            Log.d("useri:", "this is workin");
                            mDatabaseReferenceRquest.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String RequestType = snapshot.child(Contents.REQUEST_TYPE).getValue().toString();

                                        if (RequestType.equals(Contents.SENT)) {
                                            mFInd_models_listl.add(new FInd_Model(name, photoName, userId, true));
                                            mMyFindAdapter.notifyDataSetChanged();

                                        } else {
                                            mFInd_models_listl.add(new FInd_Model(name, photoName, userId, false));
                                            mMyFindAdapter.notifyDataSetChanged();

                                        }


                                    } else {
                                        mFInd_models_listl.add(new FInd_Model(name, photoName, userId, false));
                                        mMyFindAdapter.notifyDataSetChanged();

                                    }
                                    showMesage.setVisibility(View.GONE);
                                    progressbar.setVisibility(View.GONE);
                                }


                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "database Error :" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressbar.setVisibility(View.GONE);

                                }
                            });
//
                        }
                    }

                    showMesage.setVisibility(View.GONE);
                    progressbar.setVisibility(View.GONE);
                }

                showMesage.setVisibility(View.GONE);
                progressbar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressbar.setVisibility(View.GONE);
                showMesage.setVisibility(View.GONE);
                Toast.makeText(getContext(), error.getMessage()+"", Toast.LENGTH_SHORT).show();
            }
        });

    }
}