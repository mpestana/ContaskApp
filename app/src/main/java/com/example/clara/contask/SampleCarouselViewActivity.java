package com.example.clara.contask;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.synnapps.carouselview.ViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SampleCarouselViewActivity extends AppCompatActivity {

    private BroadcastReceiver broadcastReceiver;

    CarouselView carouselView;
    CarouselView customCarouselView;

    TextView carouselLabel;
    TextView customCarouselLabel;
    TextView text_wait;

    Button pauseButton;

    int[] sampleImages = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_3, R.drawable.image_4, R.drawable.image_5};
    String[] sampleTitles = {"For an application having multiple activities , cant we implement this callback class in the Main Application class so it would know that which activity is currently being created/resumed/stopped etc ? \n", "Grapes \n", "Strawberry \n", "Cherry \n", "Apricot \n"};
    List<Integer> taskIds = new ArrayList<Integer>();
    List<String> taskQuestion = new ArrayList<String>();
    List<Integer> taskAnswer = new ArrayList<Integer>();
    List<Integer> taskContext = new ArrayList<Integer>();

    public ImageView mDialog;
    public Integer genderSelected = 0;

    public static final Integer FEMALE = 0;
    public static final Integer MALE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_scrolling_tasks);

        //carouselView = (CarouselView) findViewById(R.id.carouselView);
        // carouselLabel = (TextView) findViewById(R.id.carouselLabel);
        customCarouselView = (CarouselView) findViewById(R.id.customCarouselView);
        customCarouselLabel = (TextView) findViewById(R.id.customCarouselLabel);
        text_wait = (TextView) findViewById(R.id.waiting);

        mDialog = (ImageView)findViewById(R.id.your_image);
        final String[] gender = {""};
        GraphRequest requestMe = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback(){
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            gender[0] = object.getString("gender");
                            Log.i("MainActivity", gender[0]);

                            if(gender[0].equals("male")){
                                mDialog.setImageResource(R.drawable.man);
                                genderSelected= MALE;
                            }

                            else if (gender[0].equals("female")){
                                mDialog.setImageResource(R.drawable.woman);
                                genderSelected = FEMALE;
                            }
                            else{
                                mDialog.setImageResource(R.drawable.woman); //se pessoa nao coloca nada no face -> mulher
                                genderSelected = FEMALE;
                            }

                        } catch (JSONException e) {
                           mDialog.setImageResource(R.drawable.man); // se ocorre qualquer problema na requisicao -> homem
                            genderSelected = MALE;
                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender, birthday");
        requestMe.setParameters(parameters);
        requestMe.executeAsync();

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Integer id = Integer.parseInt(intent.getStringExtra(ServiceUtil.SERVICE_MESSAGE));
                if(id==0){
                    taskQuestion.clear();
                    taskAnswer.clear();
                    taskContext.clear();
                    taskIds.clear();
                }else{
                    Log.i("TaskTest","Tarefa " + id + " recebida");
                }
                if(!taskIds.contains(id)){
                    Integer contexto_selecionado;
                    Integer answer;
                    String context_test = intent.getStringExtra("context");
                    String question = intent.getStringExtra("question");
                    String context_answer = intent.getStringExtra("answer");
                    if(context_answer!=null && context_test != null){
                        contexto_selecionado = Integer.parseInt(context_test);
                        answer = Integer.parseInt(context_answer);
                        taskQuestion.add(id + " - " + question);
                        taskAnswer.add(answer);
                        taskContext.add(contexto_selecionado);
                        //text_wait.setText(contexto_selecionado.toString());
                        if(taskIds.isEmpty()){ //primeira vez que encontra
                            taskIds.add(id); //ve se ta vazia e add, e faz os comandos especificos pra qd ta vazia
                            customCarouselView.setViewListener(viewListener);
                            customCarouselView.setPageCount(taskIds.size());
                            customCarouselView.setSlideInterval(4000);
                            customCarouselView.reSetSlideInterval(0);
                            text_wait.setVisibility(View.INVISIBLE);
                        }else{
                            taskIds.add(id); //add e seta numero novo de pagina
                            customCarouselView.setPageCount(taskIds.size());
                        }
                    }
                }
            }
        };

//        pauseButton = (Button) findViewById(R.id.pauseButton);
//        pauseButton.setOnClickListener(pauseOnClickListener);
        //carouselView.setPageCount(sampleImages.length);
        //customCarouselView.setPageCount(taskIds.size());
        //customCarouselView.setSlideInterval(4000);
        //carouselView.setImageListener(imageListener);
        //customCarouselView.setViewListener(viewListener);
        //customCarouselView.reSetSlideInterval(0);
    }

    private void addScreen() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver),
                new IntentFilter(ServiceUtil.SERVICE_RESULT));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    /* To set simple images
    ImageListener imageListener = new ImageListener() {
         @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImages[position]);
        }
    };*/

    // To set custom views
    ViewListener viewListener = new ViewListener() {
        @Override
        public View setViewForPosition(final int position) {

            View customView = getLayoutInflater().inflate(R.layout.task_view, null);

            TextView labelTextView = (TextView) customView.findViewById(R.id.taskText);
            //ImageView fruitImageView = (ImageView) customView.findViewById(R.id.fruitImageView);

            if(taskIds.get(position)!=null) {
                //fruitImageView.setImageResource(sampleImages[position]);
                labelTextView.setText(taskQuestion.get(position));
//              carouselView.setIndicatorGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);

                if (taskAnswer.get(position).equals(2)) {
                    final TextView textSB = (TextView) customView.findViewById(R.id.initialvaluetextID);
                    final SeekBar seekBar = (SeekBar) customView.findViewById(R.id.seekBarID);
                    final RelativeLayout rl = (RelativeLayout) customView.findViewById(R.id.relativeLayoutNivel);
                    rl.setVisibility(View.VISIBLE);
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            textSB.setText(String.valueOf(progress));
                        }
                    });
                }
                if(taskAnswer.get(position).equals(1)){
                    RadioGroup rb = (RadioGroup) customView.findViewById(R.id.radioSex);
                    rb.setVisibility(View.VISIBLE);
                }

                Button sendAnswer = (Button) customView.findViewById(R.id.send_task);

                sendAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id_avatar=0;
                       if(genderSelected==MALE){
                            id_avatar=changeM(taskContext.get(position));
                       }
                        if(genderSelected==FEMALE){
                            id_avatar=changeF(taskContext.get(position));
                        }
                        mDialog.setImageResource(id_avatar);
                    }
                });


            }
            return customView;
        }
    };

    View.OnClickListener pauseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //carouselView.pauseCarousel();
            customCarouselView.reSetSlideInterval(0);

        }
    };

    public int changeF(int context){
        int img_id=0;
        if(context==ServiceUtil.CONTEXTO_IM)
            img_id=R.drawable.class_woman;
        if(context==ServiceUtil.CONTEXTO_BIBLIOTECA)
            img_id=R.drawable.bib_woman;
        if(context==ServiceUtil.CONTEXTO_CHUVA)
            img_id=R.drawable.chuva_woman;
        if(context==ServiceUtil.CONTEXTO_DIA)
            img_id=R.drawable.day_woman;
        if(context==ServiceUtil.CONTEXTO_TEMP_ALTA)
            img_id=R.drawable.hot_woman;
        if(context==ServiceUtil.CONTEXTO_NOITE)
            img_id=R.drawable.noite_woman;
        return  img_id;
    }

    public int changeM(int context){
        int img_id=0;
        if(context==ServiceUtil.CONTEXTO_IM)
            img_id=R.drawable.class_man;
        if(context==ServiceUtil.CONTEXTO_BIBLIOTECA)
            img_id=R.drawable.bib_man;
        if(context==ServiceUtil.CONTEXTO_CHUVA)
            img_id=R.drawable.chuva_man;
        if(context==ServiceUtil.CONTEXTO_DIA)
            img_id=R.drawable.day_man;
        if(context==ServiceUtil.CONTEXTO_TEMP_ALTA)
            img_id=R.drawable.hot_man;
        if(context==ServiceUtil.CONTEXTO_NOITE)
            img_id=R.drawable.noite_man;
        return  img_id;
    }

 }