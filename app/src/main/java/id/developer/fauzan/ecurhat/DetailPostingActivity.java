package id.developer.fauzan.ecurhat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.developer.fauzan.ecurhat.adapter.CommentAdapter;
import id.developer.fauzan.ecurhat.adapter.PostingAdapter;
import id.developer.fauzan.ecurhat.model.Comment;
import id.developer.fauzan.ecurhat.model.Posting;
import id.developer.fauzan.ecurhat.util.Constant;

public class DetailPostingActivity extends AppCompatActivity {
    private static final String TAG = DetailPostingActivity.class.getSimpleName();

    @BindView(R.id.dari_detail)
    TextView dari;
    @BindView(R.id.untuk_detail)
    TextView untuk;
    @BindView(R.id.pesan_detail)
    TextView pesan;
    @BindView(R.id.comment_form)
    EditText commentForm;
    @BindView(R.id.send_comment)
    Button sendComment;

    private RecyclerView recyclerView;
    private TextView emptyMessage;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference databaseReference;

    private ArrayList<Posting> getPostingList;
    private ArrayList<Comment> commentArrayList;

    private CommentAdapter commentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_posting);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //init firebase auth
        auth = FirebaseAuth.getInstance();
        if (savedInstanceState == null){
            Bundle getBundle = getIntent().getExtras();
            //get data from intent
            getPostingList = new ArrayList<>();
            getPostingList = getBundle
                    .getParcelableArrayList(getString(R.string.GET_SELECTED_ITEM));
        }
        //set action bar title
        getSupportActionBar().setTitle("Dari : "+getPostingList.get(0).getDari());
        //show magang detail
        showPosting();
        Log.i(TAG, "dari " + getPostingList.get(0).getDari());

        emptyMessage = (TextView)findViewById(R.id.empty_comment);
        recyclerView = (RecyclerView)findViewById(R.id.comment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        commentArrayList = new ArrayList<>();
        commentAdapter = new CommentAdapter(getApplicationContext());
        commentArrayList.clear();

        getComment();
        //add Adapter to RecyclerView
        recyclerView.setAdapter(commentAdapter);

        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setComment(data());
            }
        });
    }

    private Comment data(){
        String inputComment = commentForm.getText().toString().trim();
        Comment comment = new Comment();
        comment.setComment(inputComment);

        return comment;
    }

    private void setComment(final Comment comment) {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constant.COMMENT);
        databaseReference
                .child(getPostingList.get(0).getKey())
                .child(databaseReference.push().getKey())
                .setValue(comment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        commentForm.setText("");
                    }
                });
    }

    private void getComment(){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constant.COMMENT);
        databaseReference.child(getPostingList.get(0).getKey())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Mapping data pada DataSnapshot ke dalam objek mahasiswa
                    Comment comment = snapshot.getValue(Comment.class);
                    commentArrayList.add(comment);
                }

                //posting size
                if (commentArrayList.size() == 0) {
                    emptyMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    //init list data to adapter
                    commentAdapter.setCommentData(commentArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showPosting() {
        dari.setText("Dari : " + getPostingList.get(0).getDari());
        untuk.setText("Untuk : " + getPostingList.get(0).getUntuk());
        pesan.setText("Pesan : " + getPostingList.get(0).getPesan());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //commentAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //commentAdapter.notifyDataSetChanged();
    }
}
