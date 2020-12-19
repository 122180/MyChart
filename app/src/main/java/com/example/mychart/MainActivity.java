package com.example.mychart;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.mychart.ChatFriend.Chat;
import com.example.mychart.Find_Friend.Find_Request;
import com.example.mychart.common.Common;
import com.example.mychart.requestFriend.Request_friend;
import com.example.mychart.profile.User_Profile;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private TabLayout mTabLayout;
    private ViewPager mViewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTabLayout=findViewById(R.id.tablayout);
        mViewPager2=findViewById(R.id.vpPager);
        FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference()
                .child(Common.USERS).child(firebaseAuth.getCurrentUser().getUid());
        databaseReference.child(Common.ONLINE).setValue(true);
        databaseReference.child(Common.ONLINE).onDisconnect().setValue(false);

        setPagerAdpter();

    }

    class  Adapter extends FragmentPagerAdapter
    {

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    Chat chat=new Chat();
                    return chat;
                case 1:
                    Request_friend request_friend=new Request_friend();
                    return request_friend;
                case 2:
                    Find_Request find_request=new Find_Request();
                    return find_request;
            }
            return null;
        }

        @Override
        public int getCount() {
            return mTabLayout.getTabCount();
        }
    }
 private void setPagerAdpter()
 {
     mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.tab_chat));
     mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.tab_request));
     mTabLayout.addTab(mTabLayout.newTab().setCustomView(R.layout.tab_find_friend));

     mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
   Adapter myAdapter=new Adapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
     mViewPager2.setAdapter(myAdapter);

     mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
         @Override
         public void onTabSelected(TabLayout.Tab tab) {
             mViewPager2.setCurrentItem(tab.getPosition());
         }

         @Override
         public void onTabUnselected(TabLayout.Tab tab) {

         }

         @Override
         public void onTabReselected(TabLayout.Tab tab) {

         }
     });
     mViewPager2.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

 }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
     getMenuInflater().inflate(R.menu.show_profile,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.showpicture)
        {
            startActivity(new Intent(MainActivity.this, User_Profile.class));

        }
        return super.onOptionsItemSelected(item);
    }

 private boolean doublePressed=false;
    @Override
    public void onBackPressed() {

        if(mTabLayout.getSelectedTabPosition()>0)
        {
            mTabLayout.selectTab(mTabLayout.getTabAt(0));
        }
    if(doublePressed)
    {
        finishAffinity();
    }
    else
    {
        doublePressed=true;
        Toast.makeText(this, "press the back to exist", Toast.LENGTH_SHORT).show();
        //dalay
        android.os.Handler handler=new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                doublePressed=false;
            }
        },2000);
    }
    }
}