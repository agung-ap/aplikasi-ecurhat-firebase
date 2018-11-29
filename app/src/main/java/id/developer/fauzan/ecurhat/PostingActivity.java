package id.developer.fauzan.ecurhat;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import id.developer.fauzan.ecurhat.model.Comment;
import id.developer.fauzan.ecurhat.model.Posting;
import id.developer.fauzan.ecurhat.util.Constant;

public class PostingActivity extends AppCompatActivity {
    @BindView(R.id.dari)
    EditText dari;
    @BindView(R.id.untuk)
    EditText untuk;
    @BindView(R.id.pesan)
    EditText pesan;
    @BindView(R.id.posting)
    Button posting;

    private ArrayList<Posting> userId;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        ButterKnife.bind(this);

        getSupportActionBar().setTitle("Posting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //init firebase
        auth = FirebaseAuth.getInstance();
        getUserId();

        //posting magang
        posting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPosting(data());

            }
        });

    }

    private void getUserId() {
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constant.POSTING);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Inisialisasi ArrayList
                userId = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Mapping data pada DataSnapshot ke dalam objek mahasiswa
                    Posting posting = snapshot.getValue(Posting.class);

                    posting.setKey(snapshot.getKey());
                    userId.add(posting);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private Posting data(){
        String inputDari = dari.getText().toString().trim();
        String inputUntuk = untuk.getText().toString().trim();
        String inputPesan = pesan.getText().toString().trim();

        Posting posting = new Posting();
        posting.setDari(inputDari);
        posting.setUntuk(inputUntuk);
        posting.setPesan(inputPesan);

        return posting;
    }

    private void setPosting(Posting data){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constant.POSTING);
        databaseReference.child(databaseReference.push().getKey()).setValue(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            /*Comment comment = new Comment();
                            comment.setComment(Constant.NONE);
                            comment.setImageUrl(Constant.NONE);

                            for (int i = 0; i < userId.size(); i++) {
                                setComment(databaseReference.push().getKey(),
                                        comment);
                            }*/

                        } else {

                        }
                    }
                });

    }

    private void setComment(String postingId, Comment comment){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constant.COMMENT);
        databaseReference.child(postingId)
                .setValue(comment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(PostingActivity.this, "Pesan Telah Di Posting", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {

                        }
                    }
                });
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
}
