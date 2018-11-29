package id.developer.fauzan.ecurhat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.developer.fauzan.ecurhat.model.Users;
import id.developer.fauzan.ecurhat.model.UsersImage;
import id.developer.fauzan.ecurhat.util.Constant;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    @BindView(R.id.nama)
    EditText nama;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.registrasi)
    Button registrasi;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        //init firebase auth
        auth = FirebaseAuth.getInstance();
        //register button
        registrasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle register process
                handleRegister();
            }
        });
    }

    private void handleRegister() {
        final String inputNama = nama.getText().toString().trim();
        final String inputEmail = email.getText().toString().trim();
        final String inputPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(inputEmail)) {
            email.setError("Enter email address!");
            return;
        }
        if (TextUtils.isEmpty(inputPassword)) {
            password.setError("Password Tidak boleh kosong");
            return;
        }
        if (inputPassword.length() < 6) {
            password.setError("password kurang dari 6 karakter");
            return;
        }
        //show progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);

        //start register on firebase
        auth.createUserWithEmailAndPassword(inputEmail, inputPassword)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            //when failed on register
                            //hide progressbar
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Register Failed : " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            //when success on register
                            String inputUid = task.getResult().getUser().getUid();
                            //set database field
                            Users user = new Users();
                            user.setNama(inputNama);
                            user.setEmail(inputEmail);
                            user.setPassword(inputPassword);
                            user.setStatus(Constant.ROLE_USER);
                            //mapping users image database
                            UsersImage userImage = new UsersImage();
                            userImage.setImageUrl(Constant.NONE);

                            //input user data to database users
                            createUser(inputUid, user, userImage);
                        }
                    }
                });
    }

    private void createUser(final String inputUid, Users user, final UsersImage userImage) {
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.USERS_TABLE);
        databaseReference.child(inputUid).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            //create image table
                            createImageTable(inputUid, userImage);
                            //hide progress bar
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Register Berhasil",
                                    Toast.LENGTH_SHORT).show();

                            Log.i(TAG, "Register is Success");
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        }else {
                            progressDialog.dismiss();
                            Log.e(TAG, "Register gagal : " + task.getException());
                            Toast.makeText(RegisterActivity.this, "Register gagal",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createImageTable(String inputUid, UsersImage userImage){
        final DatabaseReference databaseImageReference = FirebaseDatabase.getInstance()
                .getReference(Constant.USERS_PHOTO_TABLE);
        databaseImageReference.child(inputUid).setValue(userImage)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.i(TAG, "create image table is success");
                }else {
                    Log.e(TAG, "create image table is failed : " + task.getException());
                }
            }
        });
    }
}
