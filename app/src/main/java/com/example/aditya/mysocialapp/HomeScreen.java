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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class HomeScreen extends AppCompatActivity implements PostsAdapter.updateList
{
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    PostsAdapter mAdapter;
    ArrayList<Post> posts;

    FirebaseAuth mAuth;
    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference messagesRef;

    ArrayList<Request> friends = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        final TextView userName = (TextView) findViewById(R.id.userName);

        mAuth = FirebaseAuth.getInstance();

        mRootRef.child("USERS").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                userName.setText(user.getFirstName()+" "+user.getLastName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRootRef.child("FRIENDS").child(mAuth.getCurrentUser().getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Request friend = dataSnapshot.getValue(Request.class);
                friends.add(friend);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ImageView friendsButton = (ImageView) findViewById(R.id.imageButton);
        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, FriendsActivity.class);
                startActivity(intent);
            }
        });

        userName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this, ProfileActivity.class);
                intent.putExtra("USERID", mAuth.getCurrentUser().getUid());
                startActivity(intent);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        messagesRef = mRootRef.child("POSTS");
        Log.d("posts", messagesRef.getKey());
        posts = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.drawable.app_icon_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(getBaseContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        messagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Post postItem = dataSnapshot.getValue(Post.class);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -2);
                Date date = cal.getTime();
                if ((mAuth.getCurrentUser().getUid().equals(postItem.getPostedBy()) || checkFriend(postItem)) && postItem.getTimestamp().after(date) ){
                    posts.add(postItem);
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

        final EditText post = (EditText) findViewById(R.id.post);
        ImageView sendButton = (ImageView) findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String postText = post.getText().toString();
                if (postText != null && postText.length() > 0 && postText.length() <= 200)
                {
                    Date date = new Date();
                    String key = messagesRef.push().getKey();
                    Post post1 = new Post(key,mAuth.getCurrentUser().getUid(),postText,date);
                    messagesRef.child(key).setValue(post1);
                    post.setText("");
                }else{
                    Toast.makeText(HomeScreen.this, "Post message length should be between 1 and 200",Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(HomeScreen.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setAdapter()
    {
        if (posts.size() >= 0)
        {
            Collections.sort(posts, new Comparator<Post>() {
                @Override
                public int compare(Post lhs, Post rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.getTimestamp().after(rhs.getTimestamp()) ? -1 : 1;
                }
            });
            mAdapter = new PostsAdapter(posts, getBaseContext(), HomeScreen.this, "home");
            Log.d("Adapter","adapter");
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void openProfile(String uid) {
        Intent intent = new Intent(HomeScreen.this, ProfileActivity.class);
        intent.putExtra("USERID", uid);
        startActivity(intent);
    }

    boolean checkFriend(Post post){
        for (Request friend : friends)
            if (friend.getUid().equals(post.getPostedBy()))
                return true;
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }

    void removePost(Post post){
        Post postToRemove = null;

        for (Post post1 : posts){
            if (post1.getPost_id().equals(post.getPost_id()))
                postToRemove = post1;
        }

        if (postToRemove != null)
            posts.remove(postToRemove);
    }


}
