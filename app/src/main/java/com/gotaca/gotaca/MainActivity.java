package com.gotaca.gotaca;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.Manifest.permission.CAMERA;

@RequiresApi(api = Build.VERSION_CODES.O)

public class MainActivity extends AppCompatActivity implements LocationListener, RewardedVideoAdListener {

    private LocationManager manager;
    private Location bestLocation;
    static final int PREFERENCES_RESULT = 0;
    private ViewStationFragment stationFragment;
    private FirebaseAuth mAuth;
    static final int REQUEST_IMAGE_CAPTURE = 0;

    private RewardedVideoAd mRewardedVideoAd;
    FirebaseFirestore db;
    RecyclerView mRecyclerView;
    ArrayList<Station> stationArrayList;
    RecyclerViewAdapter adapterStations;
    String lastStreetChosed, currentChosenPreference, fuel;
    String FILENAME = "Chosed street";
    String firstTimeKey, uIDUser, firstChooseStreet;
    String gasPrice, etanolPrice;
    boolean firstTimeDetector, firstTimeIn;
    String[] corredores;
    Calendar calendar;

    String teste;
    String azul;
    int chosedStreetPosition, fuelPreference;
    int fuelChoosed;
    long lastVersionCode;

    public static final String TACA_POINT = "Pontuação Tacas";
    public static final String CUMULATIVE_POINT = "Pontuação Acumulativa";
    public static final String LAST_ACCESS = "Último acesso";
    public static final String TACA_HISTORY = "Extrato de Tacas";
    long tacasAmount, cumulativePoints;

    @Override
    protected void onStart(){
        super.onStart();
        verifyFirstTimeAccess();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stationArrayList = new ArrayList<>();

        setUpFirebase();
        setUpRecyclerView();
        loadDataFromFirebase();
        setUpAdMob();
        loadAdMob();
        averagePrice();

        //messageToken(); Habilitar para enviar notification test.

        //stationFragment = (ViewStationFragment)getSupportFragmentManager().findFragmentById(R.id.station_view_fragment);

        verifyUpdate();

        manager = (LocationManager)getSystemService(LOCATION_SERVICE);
        lastLocation();


        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        enableGPS();

        firstTimeDayGame();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getSharedPreferences(firstChooseStreet,MODE_PRIVATE).getBoolean(firstChooseStreet,true)){
                    dialogChooseStreet();
                } else {
                    chooseStreet();
                }
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fabprice);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Verificar conexão.
                ConnectivityManager cM = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cM.getActiveNetworkInfo();
                boolean isOnline = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                //Informar como enviar o preço ou se conectar.
                if (isOnline){
                    if (checkSelfPermission(CAMERA) != PackageManager.PERMISSION_GRANTED){
                        requestPermissions(new String[]{CAMERA}, REQUEST_IMAGE_CAPTURE);
                    } else {
                        dialogInfo();
                    }
                } else {
                    callNetworkConnection();
                }
                //startActivity(new Intent(getApplicationContext(), SendPrice.class));
            }
        });
    }

    public void averagePrice(){
        DocumentReference avPriceDoc = db.collection("Average Price").document("DocAveragePrice");
        avPriceDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    gasPrice = (String) document.get("Gasolina Comum");
                    etanolPrice = (String) document.get("Etanol");
                    setAveragePrices(gasPrice, etanolPrice);
                }
            }
        });
    }

    public void setAveragePrices(String gasPrice, String etanolPrice){
        TextView txAvPrV = (TextView)findViewById(R.id.scrollingTextTest);
        txAvPrV.setSelected(true); //Line added to enable the scrolling text in the last android versions.
        String txAvPr = "Valores médios       Gasolina Comum: R$ "+gasPrice+"       Etanol: R$ "+etanolPrice;
        txAvPrV.setText(txAvPr);
    }

    public void messageToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Error", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = token;
                        Log.d("TokenFirebase: ", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void updateLastLocation(){
        uIDUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        GeoPoint lastLocation = Stations.actualPosition;
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("Last Location", lastLocation);
        db.collection("Users").document(uIDUser).collection("Data").document("Profile").update(dataMap);
    }

    private void verifyUpdate(){
        db.collection("Actual Version").document("yJFSjXbaUfFz6MdRla7L").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                lastVersionCode = (long) document.get("actualVersionCode");
                int versionCode = BuildConfig.VERSION_CODE;
                if (lastVersionCode != versionCode){
                    requestAppUpdate();
                }
            }
        });
    }

    private void requestAppUpdate(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Existe uma nova atualização")
                .setMessage("Mantenha o GoTaca atualizado para aproveitar as novidades.")
                .setPositiveButton("Atualizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.gotaca.gotaca")));
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogChooseStreet(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Onde quer abastecer?")
                .setMessage("Escolha o corredor e o sentido")
                .setPositiveButton("Escolher", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getSharedPreferences(firstChooseStreet,MODE_PRIVATE).edit().putBoolean(firstChooseStreet, false).apply();
                        chooseStreet();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void dialogInfo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enviar preço")
                .setMessage("Pronto, nós já te localizamos.\nAgora é só tirar a foto do preço.")
                .setPositiveButton("Tirar a foto", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), SendPrice.class));
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void callNetworkConnection(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sem conexão")
                .setMessage("Você precisa se conectar à internet para enviar um preço.")
                .setPositiveButton("Conectar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent onNetwork = new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS);
                        startActivity(onNetwork);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setUpAdMob(){
        MobileAds.initialize(this, "ca-app-pub-8041447300796099~5853426281");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
    }

    private void loadAdMob(){
        mRewardedVideoAd.loadAd("ca-app-pub-8041447300796099/1364227374",
                new AdRequest.Builder().build());
    }

    private void chooseStreet (){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Escolha o corredor e o sentido")
                    .setItems(R.array.chooseStreet, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String[] yourArray = getResources().getStringArray(R.array.chooseStreet);
                            currentChosenPreference = yourArray[which];
                            saveInCache(which);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
    }

    private void saveInCache(int corredorSelecionado){
        Intent intent = new Intent(this, ChooseStreetActivity.class);
        intent.putExtra("corredor selecionado", corredorSelecionado);
        startActivity(intent);
    }


    private void verifyFirstTimeAccess(){
        firstTimeDetector = getPreferences(MODE_PRIVATE).getBoolean(firstTimeKey, true);
        if (firstTimeDetector){
            callTutorial();
            getPreferences(MODE_PRIVATE).edit().putBoolean(firstTimeKey, false).apply();
        }
    }

    private void firstTimeDayGame(){
        calendar = Calendar.getInstance();
        uIDUser = FirebaseAuth.getInstance().getCurrentUser().getUid(); //Avaliar código para evitar null.

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final String actualDate = simpleDateFormat.format(calendar.getTime());

        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Gamification");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    Date lastAccess = document.getTimestamp(LAST_ACCESS).toDate();
                    String lastAccessS = simpleDateFormat.format(lastAccess);
                    if (!Objects.equals(lastAccessS, actualDate)) {
                        tacasAmount = (long) document.get(TACA_POINT) + 10;
                        cumulativePoints = (long) document.get(CUMULATIVE_POINT) + 10;
                        sendPoints(calendar.getTime(),tacasAmount, cumulativePoints);
                        updateLastLocation();
                    }
                } else {
                    tacasAmount = 10;
                    cumulativePoints = 10;
                    sendPoints(calendar.getTime(),tacasAmount, cumulativePoints);
                }
            }
        });
    }

    protected void sendPoints(final Date time, final long tacas, final long cumulative) {
        DocumentReference docRef = db.collection("Users").document(uIDUser).collection("Data").document("Gamification");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put(TACA_POINT, tacas);
                    dataMap.put(CUMULATIVE_POINT, cumulative);
                    dataMap.put(LAST_ACCESS, time);
                    db.collection("Users").document(uIDUser).collection("Data").document("Gamification").update(dataMap);
                } else {
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put(TACA_POINT, tacas);
                    dataMap.put(CUMULATIVE_POINT, cumulative);
                    dataMap.put(LAST_ACCESS, time);
                    db.collection("Users").document(uIDUser).collection("Data").document("Gamification").set(dataMap);
                }
            }
        });
        Toast.makeText(this, "Você acabou de ganhar 10 Tacas pelo seu primeiro acesso do dia.", Toast.LENGTH_LONG).show();
        sendToHistory(time);
    }

    public void sendToHistory(final Date time){

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        final String actualDate = simpleDateFormat.format(time);
        String timeInMillis = String.valueOf(calendar.getTimeInMillis());

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put(TACA_POINT, 10);
        dataMap.put("Reason", "Tacas pelo primeiro acesso do dia");
        dataMap.put("Date", actualDate);
        dataMap.put("timeInMillis", timeInMillis);
        db.collection("Users").document(uIDUser).collection("Data").document("Gamification")
                .collection("Tacas History").document(timeInMillis).set(dataMap);
    }


    private void enableGPS() {
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("GPS Desabilitado")
                        .setMessage("Precisamos que habilite o GPS para encaminhar a foto com o endereço do posto.")
                        .setPositiveButton("Habilitar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                enableGPSSettings();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
    }

    private void enableGPSSettings(){
            Intent onGPS = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(onGPS);
    }

    protected FirebaseFirestore setUpFirebase(){
        //db = FirebaseFirestore.getInstance();
        return db = FirebaseFirestore.getInstance();
    }

    protected void setUpRecyclerView(){
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    protected void loadDataFromFirebase() {


        if (stationArrayList.size() > 0)
            stationArrayList.clear();

            try {
                FileInputStream fis = openFileInput(FILENAME);
                chosedStreetPosition = fis.read();
                fis.close();
            } catch (Exception e) {
                chosedStreetPosition = 0;
            }

        try {
            FileInputStream fis = openFileInput("Chosed fuel");
            fuelChoosed = fis.read();
            fis.close();
        } catch (Exception e) {
            fuelChoosed = 0;
        }

        if (fuelChoosed == 0){
            fuel = "gasolina";
        } if (fuelChoosed == 1){
            fuel = "etanol";
        } if (fuelChoosed == 2){
            fuel = "gasolina";
        }

        String[] yourArray = getResources().getStringArray(R.array.chooseStreet);

            if (chosedStreetPosition != 0) {
                lastStreetChosed = yourArray[chosedStreetPosition];
            } else {
                lastStreetChosed = "Cristiano Machado - Centro"; }

        db.collection(lastStreetChosed)
                .orderBy(fuel, Query.Direction.ASCENDING) //Se der erro, tirar o Query.Direction.ASCENDING e deixar apenas fuel.
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot querySnapshot : task.getResult()) {
                            Station station = new Station(querySnapshot.getId(),
                                    querySnapshot.getGeoPoint("coord"),
                                    querySnapshot.getString("name"),
                                    querySnapshot.getString("adress"),
                                    querySnapshot.getString("gasolina"),
                                    querySnapshot.getString("updateDate"),
                                    querySnapshot.getString("whoUpdated"),
                                    querySnapshot.getString("etanol")/*,
                                        querySnapshot.getString("distributor")*/);

                            stationArrayList.add(station);
                            adapterStations = new RecyclerViewAdapter(getBaseContext(), MainActivity.this, stationArrayList);
                                adapterStations.setOnClickListener(new View.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void onClick(View view) {
                                        //MainActivity mainActivity = new MainActivity();
                                        //mainActivity.showStation(mRecyclerView.getChildAdapterPosition(view));
                                        showStation(mRecyclerView.getChildAdapterPosition(view)); //Encontrar outro get que retorne o id do documento.
                                    }
                                });
                            mRecyclerView.setAdapter(adapterStations);
                        }
                    }
                });
    }

    public void showStation(int id){
        if (stationFragment != null){
            //stationFragment.updateView(id);
        } else {
            String nameStation = stationArrayList.get(id).getName();
            String adressStation = stationArrayList.get(id).getAdress();
            String gasStation = stationArrayList.get(id).getGasolinac();
            String etanolStation = stationArrayList.get(id).getEtanol();
            String updateDate = stationArrayList.get(id).getUpdateDate();
            String whoUpdated = stationArrayList.get(id).getWhoUpdated();
            GeoPoint position = stationArrayList.get(id).getPosition();
            com.google.firebase.firestore.GeoPoint coord = stationArrayList.get(id).getCoord();
            Double latitude = coord.getLatitude();
            Double longitude = coord.getLongitude();

            Intent intent = new Intent(this, ViewStationActivity.class);

            intent.putExtra("nameStation", nameStation);
            intent.putExtra("adressStation", adressStation);
            intent.putExtra("gasStation", gasStation);
            intent.putExtra("etanolStation", etanolStation);
            intent.putExtra("updateDateStation", updateDate);
            intent.putExtra("whoUpdated", whoUpdated);
            intent.putExtra("latitude", latitude);
            intent.putExtra("longitude", longitude);

            startActivityForResult(intent,0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.Premiação){
            startActivity(new Intent(getApplicationContext(), Lottery.class));
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.profileTeste){
            callProfile();
            return true;
        }

        if (id == R.id.tutorialMenu){
            callTutorial();
        }

        if (id == R.id.logout){
            callLogout();
            return true;
        }

        if (id == R.id.termsRights){
            callTerms();
            return true;
        }

        if (id == R.id.orderQuery){
            callOrderDialog();
            return true;
        }

        if (id == R.id.adMob){
            callVideoAdMob();
            return true;
        }

        if (id == R.id.userlist){
            callUserList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void callUserList(){
        startActivity(new Intent(getApplicationContext(), UserLIst.class));
    }

    private void callProfile(){
        startActivity(new Intent(getApplicationContext(), HomeUser.class));
    }

    private void callTutorial(){
        startActivity(new Intent(getApplicationContext(), Tutorial1.class));
    }

    private void callLogout() {
        mAuth.signOut();
        finish();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    private void callTerms(){
        startActivity(new Intent(getApplicationContext(), TermsRights.class));
    }

    private void callVideoAdMob(){
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {}
    @Override
    public void onRewardedVideoAdOpened() {}
    @Override
    public void onRewardedVideoStarted() {}
    @Override
    public void onRewardedVideoAdClosed() {}
    @Override
    public void onRewarded(RewardItem rewardItem) {
        adMobGaming();
    }
    @Override
    public void onRewardedVideoAdLeftApplication() {}
    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {}
    @Override
    public void onRewardedVideoCompleted() {}

    private void adMobGaming(){
        startActivity(new Intent(getApplicationContext(), AdMobGaming.class));
    }

    private void callOrderDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Qual o seu combustível preferido?")
                .setMessage("A ordem dos preços será definida com base na sua escolha.")
                .setPositiveButton("Gasolina", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fuelPreference = 2;
                        saveFuelInCache(fuelPreference);
                    }
                })
                .setNegativeButton("Etanol", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fuelPreference = 1;
                        saveFuelInCache(fuelPreference);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveFuelInCache(int fuel){
            Intent intent = new Intent(this, FuelCache.class);
            intent.putExtra("combustível", fuel);
            startActivity(intent);
    }

    void lastLocation(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                updateBestLocation(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }
            if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                updateBestLocation(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }
        }
        else {
            requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION,"Sem a sua permissão não posso mostrar a sua localização"+
                    " aos lugares.", REQUEST_LOCATION_PERMISSION, this);
        }
    }

    private static final long DOS_MINUTOS = 2*60*1000;
    private void updateBestLocation(Location location){
        if (location != null && (bestLocation == null
                || location.getAccuracy()<2*bestLocation.getAccuracy()
                || location.getTime()-bestLocation.getTime()>DOS_MINUTOS)){
            Log.d(Stations.TAG, "Nova melhor localização");
            bestLocation = location;
            Stations.actualPosition.setLatitude(location.getLatitude());
            Stations.actualPosition.setLongitude(location.getLongitude());
        }
    }

    private static final int REQUEST_LOCATION_PERMISSION = 0;
    public static void requestPermission(final String permiso, String justificacion,
                                         final int requestCode, final Activity actividad) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(actividad,
                permiso)){
            new AlertDialog.Builder(actividad)
                    .setTitle("Solicitação de permissão.")
                    .setMessage(justificacion)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ActivityCompat.requestPermissions(actividad,
                                    new String[]{permiso}, requestCode);
                        }})
                    .show();
        } else {
            ActivityCompat.requestPermissions(actividad,
                    new String[]{permiso}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == REQUEST_LOCATION_PERMISSION){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                lastLocation();
                enableProviders();
            }
        }
    }

    public void enableProviders() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 20*1000, 5, this);
            }
            if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10*1000, 10, this);
            }
        } else {
            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, "Sem a sua permissão, não posso mostrar a sua localização" + " aos lugares.", REQUEST_LOCATION_PERMISSION, this);
        }
    }

    private static final long TWO_MINUTS = 2*60*1000;
    private void enableBestLocation(Location location){
        if (location != null && (bestLocation == null
                || location.getAccuracy()<2*bestLocation.getAccuracy()
                || location.getTime()-bestLocation.getTime()>DOS_MINUTOS)){
            Log.d(Stations.TAG, "Nova melhor localização");
            bestLocation = location;
            Stations.actualPosition.setLatitude(location.getLatitude());
            Stations.actualPosition.setLongitude(location.getLongitude());
        }
    }

    @Override
    public void onLocationChanged(Location location){
        Log.d(Stations.TAG,"Nova localização: "+location);
        updateBestLocation(location);
    }

    @Override
    public void onStatusChanged(String proveedor, int estado, Bundle extras){
        Log.d(Stations.TAG, "Mudar estado: "+proveedor);
        enableProviders();
    }

    @Override
    public void onProviderEnabled(String proveedor) {
        Log.d(Stations.TAG, "Se desabilita: "+proveedor);
        enableProviders();
    }

    @Override
    public void onProviderDisabled(String proveedor) {
        Log.d(Stations.TAG, "Se desabilita: "+proveedor);
        enableProviders();
    }

    /*public void chooseStreet(View view) {
        Intent i = new Intent(this, ChooseStreetActivity.class);
        startActivityForResult(i,1);
    }*/
}