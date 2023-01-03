package com.gotaca.gotaca;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class HomeUser extends AppCompatActivity {

    ImageView userPic;
    TextView userName, userEmail, deletAccountBttn;
    String nameUser, emailUser, uIDUser;
    String firstTimeKey;

    public static final String NAME_KEY = "Name";
    public static final String EMAIL_KEY = "E-mail";
    final static int GALLERY_RESULT = 2;

    boolean firstTimeDetector;

    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onStart(){
        super.onStart();
        verifyFirstTimeAccess();
    }

    private void verifyFirstTimeAccess(){
        firstTimeDetector = getPreferences(MODE_PRIVATE).getBoolean(firstTimeKey, true);
        if (firstTimeDetector){
            callGameTutorial();
            getPreferences(MODE_PRIVATE).edit().putBoolean(firstTimeKey, false).apply();
        }
    }

    private void callGameTutorial(){
        startActivity(new Intent(getApplicationContext(), Tickets.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_user);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        uIDUser = mCurrentUser.getUid();

        deletAccountBttn = (TextView) findViewById(R.id.delete_account_t);
        deletAccountBttn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDelete();
            }
        });

        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Profile");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    createView(nameUser, emailUser);
                }
            }
        });
    }

    public void createView(String nameUser, String emailUser){
        userPic = (ImageView) findViewById(R.id.user_pic);
        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accessGallery(null);
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference picRef = storageRef.child("Profiles/Prof"+uIDUser+".jpg");

        final long ONE_MEGABYTE = 1024 * 1024;
        picRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                userPic.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
                userPic.setImageBitmap(bitmap);
            }
        });
    }

    public void accessGallery(View view){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_RESULT){
            if (resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                try {
                    InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                    userPic.setImageBitmap(bitmap);
                    photoProfiletoCloud(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Foto não carregada.", Toast.LENGTH_SHORT).show();
        }
    }

    public void photoProfiletoCloud(Bitmap photoProf){

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference picRef = storageRef.child("Profiles/Prof"+uIDUser+".jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoProf.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] data = baos.toByteArray();
        UploadTask uploadTask = picRef.putBytes(data);
    }

    private void confirmDelete(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Deletar conta")
                .setMessage("Você tem certeza que deseja deletar a sua conta?")
                .setPositiveButton("Deletar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        databaseInfoDelete(uIDUser);
                        deleteAccount();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteAccount(){
        mCurrentUser.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HomeUser.this,"Conta deletada com sucesso.", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomeUser.this,"Não foi possível deletar a conta. Favor tentar mais tarde.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void databaseInfoDelete(String uIDUserDel){
        String deleteText = "Intend to delete account";
        db = FirebaseFirestore.getInstance();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("Status", deleteText);
        db.collection("Users").document(uIDUserDel).collection("Delete Status")
                .document("Confirmation").set(dataMap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.TutorialGame){
            callGameTutorial();
            return true;
        }

        if (id == R.id.Premiação){
            startActivity(new Intent(getApplicationContext(), Lottery.class));
            return true;
        }

        if (id == R.id.backToMain){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
