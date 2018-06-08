package com.example.clara.contask;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.regex.Pattern;

import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.Instance;
import weka.classifiers.lazy.IBk;

/**
 * Created by clara on 16/02/2017.
 */

public class ServiceUtil extends Service implements LocationListener {

    public Weather mWeather;

    ArrayList<String> idList = null;
    private LocationManager locationManager;
    public GoogleApiClient client;
    private LocationRequest locationRequest;

    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 50000;
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 5;

    private LocalBroadcastManager localBroadcastManager;
    protected static final String SERVICE_RESULT = "com.service.result";
    protected static final String SERVICE_MESSAGE = "com.service.message";

    public static final Integer CONTEXTO_LUGAR = 1;
    public static final Integer CONTEXTO_DIA = 2;
    public static final Integer CONTEXTO_NOITE = 3;
    public static final Integer CONTEXTO_TEMP_ALTA = 4;
    public static final Integer CONTEXTO_CHUVA = 5;
    public static final Integer CONTEXTO_RU = 6;
    public static final Integer CONTEXTO_ESTACIONAMENTO = 7;
    public static final Integer CONTEXTO_PAF1 = 8;
    public static final Integer CONTEXTO_IM = 9;
    public static final Integer CONTEXTO_BIOLOGIA = 10;
    public static final Integer CONTEXTO_BIOLOGIA_RESTAURANTE = 11;
    public static final Integer CONTEXTO_PORTARIA = 12;
    public static final Integer CONTEXTO_BIBLIOTECA = 13;
    public static final Integer CONTEXTO_UNKNOWN = 14;

    private Integer contexto_selecionado = 0;

    private Location lastLocation = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        localBroadcastManager = LocalBroadcastManager.getInstance(this);

        /********** Google Awareness API Data **********/
        client = new GoogleApiClient.Builder(this)
                .addApi(Awareness.API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {

                        createLocationRequest();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                    }
                })
                .build();
        client.connect();

        /******** Relation **********/
        GraphRequest requestFriends = GraphRequest.newMyFriendsRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONArrayCallback() {
                    @Override
                    public void onCompleted(JSONArray objects, GraphResponse response) {
                        String ids = String.valueOf(objects);
                        idList = new ArrayList<String>();
                        if (objects != null) {
                            try {
                                for (int i = 0; i < objects.length(); i++) {
                                    idList.add(objects.getString(i));
                                }
                            } catch (JSONException e) {
                                idList = null;
                                e.printStackTrace();
                            }
                        }
                        Log.i("MainActivity", "Friends Ids: " + ids);
                    }
                });
        Bundle friendsParameters = new Bundle();
        friendsParameters.putString("fields", "id");
        requestFriends.setParameters(friendsParameters);
        requestFriends.executeAsync();

        /**** Weather and Location ****/
        getSnapshot();
        return Service.START_NOT_STICKY;


    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    protected void startLocationUpdates() {

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        LocationServices.FusedLocationApi
                    .requestLocationUpdates(client, locationRequest, this);
    }

    private void sendResult(String id, String question, String answer, String context) {
        Intent intent = new Intent(SERVICE_RESULT);
        if(id != null){
            intent.putExtra(SERVICE_MESSAGE, id);
        }
        if (question != null && answer != null && context!=null) {
            intent.putExtra("question", question);
            intent.putExtra("answer", answer);
            intent.putExtra("context", context);
        }
        localBroadcastManager.sendBroadcast(intent);
    }


    @Override
    public void onLocationChanged(Location location) {
        //Controlador de Reset de Tarefas após 10m pecorridos
        if(lastLocation!=null){
            if(location.distanceTo(lastLocation)>100){
                sendResult("0", null, null, null);//envia reset
                lastLocation = location;
                return;
            }
        }
        else {
            lastLocation = location;
        }
        // criação de um arquivo de dados para testes na UFBA ondina
        FilesManager mFilesManager = new FilesManager();
        ArrayList<String> list = mFilesManager.ReadsNotification(this);

        Log.i("MainActivity", "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());

        for (int i = 0; i < list.size(); i++) {
            //STRINGS DOS ARQUIVOS
            String lat2 = list.get(i).substring(4, list.get(i).indexOf(","));
            String lng2 = list.get(i).substring(list.get(i).indexOf(", ") + 2, list.get(i).indexOf("/"));

            //Calculos da Localização
            Location location2 = new Location("");
            location2.setLatitude(Double.parseDouble(lat2));
            location2.setLongitude(Double.parseDouble(lng2));

            float distance = location.distanceTo(location2);
            Log.i("MainActivity", "Distance [" + i + "]: " + distance);

            if (distance <= 600) { //se o usuário chega perto do contexto de local
                //    Intent intent = new Intent(this, ShowNotsOnMap.class);
                //    String message = mTextView2.getText().toString();
                //    intent.putExtra(LATLNGGPSNET_MESSAGE, message);
                //    startActivity(intent);
                contexto_selecionado = CONTEXTO_LUGAR;
                //verifica contexto de tempo
                String hIni = list.get(i).substring(list.get(i).indexOf("hIni=") + 5, list.get(i).indexOf("/hF"));
                String hFim =
                        list.get(i).substring(list.get(i).indexOf("hFim=") + 5, list.get(i).indexOf("//r"));
                if (!hIni.equals("") & !hFim.equals("")) {
                    //Calculo do tempo
                    Calendar c = Calendar.getInstance();
                    int hora = c.get(Calendar.HOUR_OF_DAY); //0-23
                    int minuto = c.get(Calendar.MINUTE);//0-60
                    String minutoStr = "" + minuto;
                    if (minuto < 10 && minuto >= 0) minutoStr = 0 + minutoStr;
                    String horaStr = hora + minutoStr;
                    Integer horaAtual = Integer.parseInt(horaStr);
                    Integer horaInicio = Integer.parseInt(hIni);
                    Integer horaFim = Integer.parseInt(hFim);

                     if ((horaAtual > horaInicio) & (horaAtual < horaFim)) {
                        Log.i("MainActivity", "Contexto de tempo atendido");
                        if(horaInicio>=0500 && horaFim < 1800){
                            contexto_selecionado = CONTEXTO_DIA;
                        }else{
                            contexto_selecionado = CONTEXTO_NOITE;
                        }
                    } else {
                        Log.e("MainActivity", "Tarefa nao recebida - Contexto de tempo não foi atendido");
                        continue;
                    }
                }
                String friendFilter = list.get(i).substring(list.get(i).indexOf("rela=") + 5, list.get(i).indexOf("//c"));
                if (!friendFilter.equals("")) {
                    if (idList == null) {
                        Log.e("MainActivity", "Tarefa nao recebida - Contexto de relacao 'FacebookFriends' não pôde ser detectado");
                        continue;
                    }
                    if (idList.contains(friendFilter)) {
                        Log.e("MainActivity", "Contexto de relacao atendido");
                    } else {
                        Log.e("MainActivity", "Tarefa nao recebida - Contexto de relacao não foi atendido");
                        continue;
                    }
                }

                String temCar = list.get(i).substring(list.get(i).indexOf("car=") + 4, list.get(i).indexOf("///d"));
                if (!temCar.equals("")) {
                    //individualidade
                    Boolean testCar = true;//pega do cadastro
                    Boolean car = Boolean.parseBoolean(temCar);
                    if (testCar.equals(car)) {
                        Log.e("MainActivity", "Contexto de individualidade(carro) atendido");
                    } else {
                        Log.e("MainActivity", "Tarefa nao recebida - Contexto de individualidade(carro) não foi atendido");
                        continue;
                    }
                }

                String depart = list.get(i).substring(list.get(i).indexOf("dep=") + 4, list.get(i).lastIndexOf("///t"));
                if (!depart.equals("")) {
                    //individualidade
                    int testDep = 1;//pega do cadastro
                    int dep = Integer.parseInt(depart);
                    if (testDep == dep) {
                        Log.e("MainActivity", "Contexto de individualidade(dep) atendido");
                    } else {
                        Log.e("MainActivity", "Tarefa nao recebida - Contexto de individualidade(dep) não foi atendido");
                        continue;
                    }
                }

               String temp = list.get(i).substring(list.get(i).indexOf("temp=") + 5, list.get(i).indexOf("////c"));
                    if (!temp.equals("")) {

                        if (mWeather == null) {//erro
                            Log.e("MainActivity", "Tarefa nao recebida - Contexto de Clima não pôde ser detectado");
                            continue;
                        }

                        float tempAtual = mWeather.getTemperature(2);
                        String temps[] = temp.split(Pattern.quote(","));

                        if (tempAtual >= Integer.parseInt(temps[0]) && tempAtual <= Integer.parseInt(temps[1])) {
                            Log.e("MainActivity", "Tarefa recebida - Contexto de temperatura atendido");

                            if(Integer.parseInt(temps[0])>=30){
                                contexto_selecionado=CONTEXTO_TEMP_ALTA;
                            }

                        } else {
                            Log.e("MainActivity", "Tarefa nao recebida - Contexto de temperatura nao atendido");
                            continue;
                        }
                    }
                    String cond = list.get(i).substring(list.get(i).indexOf("cond=") + 5, list.get(i).indexOf("////h"));
                    if (!cond.equals("")) {
                        if (mWeather == null) {//erro
                            Log.e("MainActivity", "Tarefa nao recebida - Contexto de Clima não pôde ser detectado");
                            continue;
                        }
                        //Calculo da condicao climatica
                        int[] condAtual = mWeather.getConditions();
                        if (Arrays.asList(condAtual).contains(Integer.parseInt(cond))) {
                            Log.e("MainActivity", "Tarefa recebida - Contexto de condicao atendido");
                            if(Integer.parseInt(cond)==Weather.CONDITION_RAINY){
                                contexto_selecionado = CONTEXTO_CHUVA;
                            }
                        } else {
                            Log.e("MainActivity", "Tarefa nao recebida - Contexto de condicao nao atendido");
                            continue;
                        }
                    }
                    String humid = list.get(i).substring(list.get(i).indexOf("humid=") + 6, list.get(i).indexOf("*"));
                    if (!humid.equals("")) {
                        if (mWeather == null) {//erro
                            Log.e("MainActivity", "Tarefa nao recebida - Contexto de Clima não pôde ser detectado");
                            continue;
                        }
                        //Calculo da Humidade
                        int humidAtual = mWeather.getHumidity();
                        String humids[] = humid.split(Pattern.quote(","));
                        if (humidAtual >= Integer.parseInt(humids[0]) && humidAtual <= Integer.parseInt(humids[1])) {
                            Log.e("MainActivity", "Tarefa recebida - Contexto de humidade atendido");
                        } else {
                            Log.e("MainActivity", "Tarefa nao recebida - Contexto de humidade nao atendido");
                            continue;
                        }
                    }

                if(contexto_selecionado == CONTEXTO_LUGAR){
                    double classe = 0;

                    try {
                        classe = knn(location.getLatitude(), location.getLongitude());
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Integer rotulo = retornaRotulo(classe);
                    contexto_selecionado = rotulo;
                }

                //chegar aqui a tarefa é recebida
                String id = list.get(i).substring(list.get(i).indexOf("(") + 1, list.get(i).indexOf(")"));
                String question = list.get(i).substring(list.get(i).indexOf(")") + 1, list.get(i).indexOf("#"));
                String answer = list.get(i).substring(list.get(i).indexOf("#") + 1, list.get(i).lastIndexOf("#"));
                if (id != null && id != "")
                    sendResult(id, question, answer, contexto_selecionado.toString());
            } else {
                Log.e("MainActivity", "tarefa não recebida - Contexto de lugar não foi atendido");
                continue;
            }

        }
    }

    private void getSnapshot() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            sendResult("ask_permission", null, null,null);
        }
        Awareness.SnapshotApi.getWeather(client)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {

                        if (!weatherResult.getStatus().isSuccess()) {
                            Log.e("MainActivity", "Could not get weather.");
                            return;
                        }
                        Weather weather = weatherResult.getWeather();
                        weather.getConditions();
                        mWeather = weather;
                        Log.i("MainActivity", "Weather: " + weather.toString());
                        //mLogFragment.getLogView().println("Weather: " + weather);
                        //main.putExtra("cond", mWeather.getConditions());
                        //main.putExtra("temp", mWeather.getTemperature(1));
                        //main.putExtra("humid", mWeather.getHumidity());
                    }
                });
    }


    public double knn(double lat, double lng) throws Exception {

        BufferedReader datafile = new BufferedReader(new InputStreamReader(getAssets().open("databaseUFBA.arff")));
        //readDataFile(Environment.getDataDirectory()+"databaseUFBA.arff");

        Instances data = new Instances(datafile);
        data.setClassIndex(data.numAttributes() - 1);

        BufferedReader datasetfile = new BufferedReader(
                new InputStreamReader(getAssets().open("databaseCabecalho.arff")));
        //readDataFile(Environment.getDataDirectory()+"databaseCabecalho.arff");
        Instances dataUnlabeled = new Instances(datasetfile);

        Instance i1 = new DenseInstance(3);
        i1.setValue(0, lat);
        i1.setValue(1, lng);
        i1.setValue(2, 0);
        dataUnlabeled.add(i1);

        dataUnlabeled.setClassIndex(dataUnlabeled.numAttributes() - 1);

        Classifier ibk = new IBk();
        ibk.buildClassifier(data);

        double class1 = ibk.classifyInstance(dataUnlabeled.firstInstance());

        System.out.println("rotulo: "+ class1 );

        /*********** 2 ********/

//        LinearNNSearch knn = new LinearNNSearch(data);
        //do other stuff

//        Instances nearestInstances= knn.kNearestNeighbours(i1, 3);

        return class1;
    }

    public int retornaRotulo(double classe){

        if(classe == 0.0){
            return CONTEXTO_IM;
        }
        if(classe == 1.0){
            return CONTEXTO_BIBLIOTECA;
        }
       /* if(classe == 2.0){
            return CONTEXTO_PAF1;
        }
        if(classe == 3.0){
            return CONTEXTO_BIOLOGIA;
        }
        if(classe == 4.0){
            return CONTEXTO_BIOLOGIA_RESTAURANTE;
        }
        if(classe == 5.0){
            return CONTEXTO_BIBLIOTECA;
        }
        if(classe == 6.0){
            return CONTEXTO_PORTARIA;
        }
        if(classe == 7.0){
            return CONTEXTO_ESTACIONAMENTO;
        }*/
        return CONTEXTO_UNKNOWN;
    }


}
/*        Awareness.SnapshotApi.getLocation(client)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                            Log.e("MainActivity", "Could not get location.");
                            return;
                        }
                        Location location = locationResult.getLocation();
                        Log.i("MainActivity", "Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                    }
                });*/
