package com.gotaca.gotaca;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.CAMERA;

public class SendPrice extends AppCompatActivity implements LocationListener {

    View view;
    static final int REQUEST_IMAGE_CAPTURE = 0;

    long tacasAmount, cumulativePoints;
    public static final String TACA_POINT = "Pontuação Tacas";
    public static final String CUMULATIVE_POINT = "Pontuação Acumulativa";

    FirebaseFirestore db;
    FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    String uIDUser;
    String mCurrentPhotoPath;

    File image;
    Uri photoURI;

    boolean isConnected;

    private Location bestLocation;
    private LocationManager manager;
    private static final int REQUEST_LOCATION_PERMISSION = 0;
    private static final long TWO_MINUTES = 2*60*1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        uIDUser = mCurrentUser.getUid();

        //Linhas abaixo fazem referência a verificação de conexão para envio do preço.
        /*
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (isConnected){
            getPrice();
            takePicturePermission();
            manager = (LocationManager)getSystemService(LOCATION_SERVICE);
            lastLocation();
        } else {
            Toast.makeText(SendPrice.this, "Sem conexão.", Toast.LENGTH_SHORT).show();
        }*/

        getPrice();

        takePicturePermission();

        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
        lastLocation();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {}
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.gotaca.gotaca.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityIfNeeded(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            try {
                Bitmap pricePicture = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                sendPic(pricePicture);
            } catch (IOException | RuntimeException e) {
                Toast.makeText(SendPrice.this, "Infelizmente não foi possível enviar a foto.\nPor favor, tente novamente.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }
    }

    public void sendPic(Bitmap pricePicture){
        db = FirebaseFirestore.getInstance();

        int currentTime = (int) Calendar.getInstance().getTimeInMillis();
        String cTA = String.valueOf(currentTime);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Name", Stations.actualPosition);
        dataMap.put("User", uIDUser);
        db.collection("Stations Prices").document(cTA).set(dataMap);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference picRef = storageRef.child("Prices/"+cTA+".jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        pricePicture.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte [] data = baos.toByteArray();

        UploadTask uploadTask = picRef.putBytes(data);
        Toast.makeText(SendPrice.this, "Simples assim! Muito obrigado.\nVocê ganhará 50 Tacas quando a foto for aprovada.", Toast.LENGTH_LONG).show();
        image.delete();
        sendPricePoint(); //Método para envio de pontos.
    }

    public void sendPricePoint(){
        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Gamification");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    tacasAmount = (long) document.get(TACA_POINT) + 50;
                    cumulativePoints = (long) document.get(CUMULATIVE_POINT) + 50;
                    sendPoints(tacasAmount, cumulativePoints);
                } else {
                    tacasAmount = 50;
                    cumulativePoints = 50;
                    sendPoints(tacasAmount, cumulativePoints);
                }
            }
        });
    }

    protected void sendPoints(long tacas, long cumulative){
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(TACA_POINT, tacas);
        dataMap.put(CUMULATIVE_POINT, cumulative);
        db.collection("Users").document(uIDUser).collection("Data").document("Gamification").update(dataMap);
        sendToHistory();
    }

    public void sendToHistory(){

        Date time = Calendar.getInstance().getTime();

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final String actualDate = simpleDateFormat.format(time);
        String timeInMillis = String.valueOf(Calendar.getInstance().getTimeInMillis());

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(TACA_POINT, 50);
        dataMap.put("Reason", "Tacas por enviar a foto de um preço");
        dataMap.put("Date", actualDate);
        dataMap.put("timeInMillis", timeInMillis);
        db.collection("Users").document(uIDUser).collection("Data").document("Gamification")
                .collection("Tacas History").document(timeInMillis).set(dataMap);
        backToMain();
    }

    public boolean takePicturePermission(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } if (shouldShowRequestPermissionRationale(CAMERA)) {
            Snackbar.make(findViewById(R.id.fAB3), "A sua permissão é necessária para enviar a foto do preço.", Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{CAMERA}, REQUEST_IMAGE_CAPTURE);
                        }
                    });
        } else {
            requestPermissions(new String[]{CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
        return false;
    }

    public void getPrice(){
        dispatchTakePictureIntent();
        /*Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            startActivityIfNeeded(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }*/
    }

    void lastLocation(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                updateBestLocation(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            } if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                updateBestLocation(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            } else {
                requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, "Nós precisamos da sua permissão para definir a localização.", REQUEST_LOCATION_PERMISSION, this);
            }
        }
    }

    private void updateBestLocation(Location location){
        if (location != null && (bestLocation == null
                || location.getAccuracy()<2*bestLocation.getAccuracy()
                || location.getTime()-bestLocation.getTime()>TWO_MINUTES)){
            bestLocation = location;
            Stations.actualPosition.setLatitude(location.getLatitude());
            Stations.actualPosition.setLongitude(location.getLongitude());
        }
    }

    public static void requestPermission(final String permission, String justification, final int requestCode, final Activity activity){
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)){
            new AlertDialog.Builder(activity)
                    .setTitle("Solicitar permissão")
                    .setMessage(justification)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
                        }})
                    .show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        enableProviders();
    }


    @Override public void onLocationChanged(Location location){
        updateBestLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int state, Bundle extras){
        enableProviders();
    }
    @Override
    public void onProviderEnabled(String provider) {
        enableProviders();
    }

    @Override
    public void onProviderDisabled(String provider) {
        enableProviders();
    }

    public void enableProviders() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20*1000, 5, this);
            }
            if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1*1000, 10, this);
            }
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, "Sem a sua permissão, não posso mostrar a sua localização" + " aos lugares.", REQUEST_LOCATION_PERMISSION, this);
        }
    }

    public void backToMain(){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

}
