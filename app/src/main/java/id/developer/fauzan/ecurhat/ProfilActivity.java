package id.developer.fauzan.ecurhat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.developer.fauzan.ecurhat.model.Posting;
import id.developer.fauzan.ecurhat.model.Users;
import id.developer.fauzan.ecurhat.model.UsersImage;
import id.developer.fauzan.ecurhat.util.Constant;

public class ProfilActivity extends AppCompatActivity {
    private static final String TAG = ProfilActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 71;
    @BindView(R.id.profil_image)
    ImageView imageProfil;
    @BindView(R.id.nama_user)
    EditText nameProfil;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private Uri filePath;
    private ProgressDialog progressDialog;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        getImageProfil();
        getNameProfil();

        imageProfil.setClickable(true);
        imageProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choseImage();
            }
        });

    }

    private void choseImage() {
        try{
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        }catch (Exception e){
            Toast.makeText(this, "Exception : " + e, Toast.LENGTH_SHORT).show();
        }

    }

    private void getImageProfil(){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constant.USERS_PHOTO_TABLE);
        databaseReference.child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Mapping data pada DataSnapshot ke dalam objek mahasiswa
                UsersImage usersImage = dataSnapshot.getValue(UsersImage.class);
                Picasso.get().load(usersImage.getImageUrl())
                        .placeholder(R.drawable.ic_launcher_background)
                        .into(imageProfil);

                Log.i(TAG, "image url : " + usersImage.getImageUrl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getNameProfil(){
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constant.USERS_TABLE);
        databaseReference.child(auth.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Mapping data pada DataSnapshot ke dalam objek mahasiswa
                Users users = dataSnapshot.getValue(Users.class);
                nameProfil.setText(users.getNama());
                Log.i(TAG, "user name : " + users.getNama());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            uploadImage(filePath);
        }
    }

    private void uploadImage(Uri s) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference().child("images/"+ UUID.randomUUID().toString());
        storageReference.putFile(s)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                progressDialog.dismiss();

                                UsersImage user = new UsersImage();
                                user.setImageUrl(uri.toString());

                                createImageUser(auth.getUid(), user);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    private void createImageUser(String uid, UsersImage usersImager) {
        //start saving data on firebase realtime database
        databaseReference = FirebaseDatabase.getInstance()
                .getReference(Constant.USERS_PHOTO_TABLE);
        databaseReference.child(uid).setValue(usersImager)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload Berhasil", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
