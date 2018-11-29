package id.developer.fauzan.ecurhat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import id.developer.fauzan.ecurhat.adapter.PostingAdapter;
import id.developer.fauzan.ecurhat.model.Posting;
import id.developer.fauzan.ecurhat.util.Constant;

public class HomeActivity extends AppCompatActivity implements PostingAdapter.DataListener{
    private RecyclerView recyclerView;
    private TextView emptyMessage;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference databaseReference;
    private PostingAdapter postingAdapter;

    private ArrayList<Posting> postingArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setTitle("Home");
        auth = FirebaseAuth.getInstance();
        //get current user
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                }
            }
        };

        emptyMessage = (TextView)findViewById(R.id.empty_message_user);
        recyclerView = (RecyclerView)findViewById(R.id.timeline);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        postingArrayList = new ArrayList<>();
        postingAdapter = new PostingAdapter(getApplicationContext(),this);
        postingArrayList.clear();

        getData();
        //add Adapter to RecyclerView
        recyclerView.setAdapter(postingAdapter);
    }

    private void getData() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constant.POSTING);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Mapping data pada DataSnapshot ke dalam objek mahasiswa
                    Posting posting = snapshot.getValue(Posting.class);

                    posting.setKey(snapshot.getKey());
                    postingArrayList.add(posting);
                }

                //posting size
                if (postingArrayList.size() == 0) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    //init list data to adapter
                    postingAdapter.setPostingData(postingArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child(Constant.USERS_TABLE)
                        .child(auth.getUid())
                        .child("status").getValue(String.class);

                if (status.equals("admin")){
                    getMenuInflater().inflate(R.menu.menu_admin, menu);
                }else {
                    getMenuInflater().inflate(R.menu.menu_user, menu);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.profil:
                return true;
            case R.id.posting:
                startActivity(new Intent(getApplicationContext(), PostingActivity.class));
                return true;
            case R.id.logout:
                auth.signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }


    @Override
    public void onClick(Posting dataPosition) {
        Bundle bundle = new Bundle();

        ArrayList<Posting> postingList = new ArrayList<>();
        postingList.add(dataPosition);
        //put parcelable
        bundle.putParcelableArrayList(getString(R.string.GET_SELECTED_ITEM), postingList);
        //send data via intent
        Intent intent = new Intent(getApplicationContext(), DetailPostingActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);

        //Toast.makeText(this, "Position " + dataPosition, Toast.LENGTH_SHORT).show();
    }
}
