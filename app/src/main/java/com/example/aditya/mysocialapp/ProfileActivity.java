package com.example.aditya.mysocialapp;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProfileActivity extends AppCompatActivity implements PostsAdapter.updateList{

    FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();

    ArrayList<Post> posts = new ArrayList<>();

    RecyclerView.LayoutManager mLayoutManager;
    PostsAdapter mAdapter;

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final TextView postsTV = (TextView) findViewById(R.id.posts);
        final TextView username = findViewById(R.id.userName);
        ImageView editButton = findViewById(R.id.profileEdit);
        ImageView friendsButton = findViewById(R.id.friendsButton);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.app_icon_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        mRecyclerView = findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getBaseContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        final String uid = getIntent().getExtras().getString("USERID");

        if (uid.equals(mAuth.getCurrentUser().getUid()))
            editButton.setVisibility(View.VISIBLE);

        else{
            editButton.setVisibility(View.GONE);
            friendsButton.setImageResource(R.drawable.home);
        }


        if (uid.equals(mAuth.getCurrentUser().getUid()))
            editButton.setVisibility(View.VISIBLE);
        else
            editButton.setVisibility(View.GONE);

        mRootRef.child("USERS").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                username.setText(user.getFirstName()+" "+user.getLastName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (uid.equals(mAuth.getCurrentUser().getUid())){
            postsTV.setText("My Posts");
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                    startActivity(intent);
                }
            });
        }else {
            mRootRef.child("USERS").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    postsTV.setText(user.getFirstName()+" " + "'s Posts");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


        mRootRef.child("POSTS").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);
                if (post.getPostedBy().equals(uid)){
                    posts.add(post);
                    setAdapter();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Post postItem = dataSnapshot.getValue(Post.class);
                removePost(postItem);
                setAdapter();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (uid.equals(mAuth.getCurrentUser().getUid())){
                    Intent intent = new Intent(ProfileActivity.this, FriendsActivity.class);
                    startActivity(intent);
                }


                else{
                    Intent intent = new Intent(ProfileActivity.this, HomeScreen.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }


            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId()  == R.id.action_logout)
        {
            mAuth.signOut();
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    public void setAdapter(){
        if (posts.size() >= 0)
        {
            Collections.sort(posts, new Comparator<Post>() {
                @Override
                public int compare(Post lhs, Post rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.getTimestamp().after(rhs.getTimestamp()) ? -1 : 1;
                }
            });
            mAdapter = new PostsAdapter(posts, getBaseContext(), ProfileActivity.this,"profile");
            Log.d("Adapter","adapter");
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void openProfile(String uid) {

    }

    void removePost(Post post){
        Post postToRemove = null;

        for (Post post1 : posts){
            if (post1.getPost_id().equals(post.getPost_id())){
                postToRemove = post1;
                break;
            }
        }

        if (postToRemove != null)
            posts.remove(postToRemove);
    }
}
