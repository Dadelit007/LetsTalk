package com.example.letstalk;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.letstalk.Fragments.ChatFragment;
import com.example.letstalk.Fragments.UserFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final int RC_SIGN_IN = 1;

    private boolean newVisit;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRef;

    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    final     List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().setPermissions(Arrays.asList("default", "email"))
                    .build(),
            new AuthUI.IdpConfig.GoogleBuilder().build());


        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user==null){
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setLogo(R.drawable.letstalk)
                                    .setTheme(R.style.AuthenticationTheme)
//                                    .setTosAndPrivacyPolicyUrls(
//                                            "https://",
//                                            "https://")
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN
                    );
                }
            }
        };

        TabLayout tabLayout =findViewById(R.id.Tab_Layout);
        ViewPager viewPager =findViewById(R.id.View_Pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatFragment(),"Chats");
        viewPagerAdapter.addFragment(new UserFragment(),"Users");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            if(resultCode==RESULT_OK){
                Toast.makeText(this, "Signed In ", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(getIntent());

                fun();
            }
            else if(resultCode==RESULT_CANCELED)
            {
                Toast.makeText(this, "Sign In Canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void fun() {
        firebaseAuth = FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();
        mRef=firebaseDatabase.getReference("Users");


        mRef.orderByChild("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int p=1;
                for(DataSnapshot ds : dataSnapshot.getChildren()) {

                    if(ds.child("email").getValue().equals(user.getEmail()))
                    {
                        p=0;
//                        Toast.makeText(StartActivity.this, "Exist", Toast.LENGTH_SHORT).show();
                    }
                }
                if(p==1)
                {
//                    Toast.makeText(StartActivity.this, "Added", Toast.LENGTH_SHORT).show();
                    HashMap<Object,String> result=new HashMap<>();
                    result.put("name",user.getDisplayName());
                    result.put("email",user.getEmail());
                    result.put("uid",user.getUid());
                    result.put("image","");
                    firebaseDatabase.getReference("Users").child(user.getUid()).setValue(result);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mFirebaseAuth.addAuthStateListener(mAuthStateListener);


    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fa){
            super(fa);
            this.fragments =new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public  void addFragment(Fragment fragment ,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.logout: {
                AuthUI.getInstance()
                        .signOut(StartActivity.this);
                return true;
            }
            case R.id.profile: {
                startActivity(new Intent(StartActivity.this, ProfileActivity.class));
                return true;
            }
            default:
                 return super.onOptionsItemSelected(item);
        }

    }
}
