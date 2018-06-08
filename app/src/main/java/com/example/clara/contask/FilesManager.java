package com.example.clara.contask;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;
import android.view.Gravity;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by clara on 12/12/2016.
 */
public class FilesManager {
    public final static String LOG_TAG = "com.example.clara.LOG_FilesManager";

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getNotificationStorageDir(Context context, String dirname) {
        // Get the directory for the app's private notifications directory.
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS), dirname);
        if (!dir.mkdirs()) {
            //Log.e(LOG_TAG, "Directory not created or already exists");
        }
        return dir;
    }

    public File getNotificationStorageFile(Context context, File dir, String filename) {
        File file = null;
        try {
            file = new File(dir, filename);
            if (!file.createNewFile()){
                //Log.e(LOG_TAG, "File not created or already exists");
            }
        } catch (IOException e) {
            AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
            TextView myMsg = new TextView(context);
            myMsg.setText("Error creating file notification");
            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            popupBuilder.setView(myMsg);
            popupBuilder.show();
            //e.printStackTrace();
        }
        return file;
    }

    private File processDir(Context context){
        File dir = null;

        if (isExternalStorageWritable()){
            dir = getNotificationStorageDir(context, "UOANot");
        }
        else{
            AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
            TextView myMsg = new TextView(context);
            myMsg.setText("SD Card isn't available");
            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            popupBuilder.setView(myMsg);
            popupBuilder.show();
        }
        return dir;
    }

    public boolean WritesNotification(Context context, String LatLong, String title, String Desc, String Categ) {
        File dir = processDir(context);
        if (dir == null) return false;
        File file = getNotificationStorageFile(context, dir, "notifications.txt");

        try {
            FileWriter fileWriter = new FileWriter(file, true);

            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(LatLong+"***"+title+"***"+Desc+"&&&"+Categ+"&&&");
            printWriter.flush();

        } catch (IOException e) {
            AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
            TextView myMsg = new TextView(context);
            myMsg.setText("Error saving notification");
            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            popupBuilder.setView(myMsg);
            popupBuilder.show();
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    // serve apenas para criar um arquivo de notificações para testes no campos da UFBA ondina
    public boolean createPreFile(Context context){
        File dir = processDir(context);
        if (dir == null) return false;
        File file = new File(dir, "notifications.txt");

        if(file.exists()){
            return false;
        }
        else{
            file = getNotificationStorageFile(context, dir, "notifications.txt");

            try {
                FileWriter fileWriter = new FileWriter(file, true);
                PrintWriter printWriter = new PrintWriter(fileWriter);
                //printWriter.println("loc=-12.86472, -38.29068/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(0)Teste em LAuro de Freitas?#1#");
                printWriter.println("loc=-13.00179, -38.50721/hIni=1843/hFim=2300//rela=//car=///dep=///temp=////cond=////humid=*1*(1)[Contexto Noite] Classifique a iluminação deste local#2#");
                printWriter.println("loc=-13.00141, -38.50806/hIni=0500/hFim=1700//rela=//car=///dep=///temp=////cond=////humid=*1*(2)[Contexto Dia] Classifique o grau de limpeza deste local?#2#");
                printWriter.println("loc=-13.00275, -38.50688/hIni=/hFim=//rela=//car=///dep=///temp=30,50////cond=////humid=*1*(3)[Contexto Temperatura Alta] Classifique o nível de calor nesse local. #2#");
                printWriter.println("loc=-13.00187, -38.50447/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(4)[Contexto Chuva] Existe um bueiro entupido nesse local?#1#");
                printWriter.println("loc=-13.00137, -38.50732/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(5)[Contexto Lugar] A sala 15 do IM está ocupada?#1#");
                printWriter.println("loc=-13.00188, -38.50904/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(6)[Contexto Lugar] A biblioteca está aberta?#1#");
                printWriter.println("loc=-12.86500, -38.29043/hIni=/hFim=//rela=100000520291484//car=///dep=///temp=////cond=////humid=*1*(135)Classifique a alimentação desse local#2#");
                printWriter.println("loc=-12.86500, -38.29043/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(777)Classify the cleaness level of this place: 5-very clean to 0-very dirty.#2#");
                printWriter.println("loc=-12.86500, -38.29043/hIni=1200/hFim=2000//rela=//car=///dep=///temp=////cond=////humid=*1*(422)Do you feel safe in this place?#1#");


                //-12.865006, -38.290431
                /******classificação**/
                printWriter.println("loc=-13.002307, -38.508318/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(7)Classifique a limpeza desse local#2#");
                printWriter.println("loc=-13.001368, -38.508484/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(8)Classifique a limpeza desse local#2#");
                printWriter.println("loc=-13.004258, -38.509133/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(9)Classifique a limpeza desse local#2#");
                printWriter.println("loc=-13.001808, -38.509093/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(10)Classifique a limpeza desse local#2#");
                printWriter.println("loc=-13.001808, -38.509093/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(11)Classifique o barulho desse local#2#");
                printWriter.println("loc=-13.001808, -38.509093/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(12)Classifique os recursos desse local#2#");
                printWriter.println("loc=-13.002307, -38.508318/hIni=/hFim=//rela=100000520291484//car=///dep=///temp=////cond=////humid=*1*(13)Classifique a alimentação desse local#2#");
                printWriter.println("loc=-13.001368, -38.508484/hIni=/hFim=//rela=100000520291484//car=///dep=///temp=////cond=////humid=*1*(14)Classifique a alimentação desse local#2#");
                printWriter.println("loc=-13.004258, -38.509133/hIni=/hFim=//rela=100000520291484//car=///dep=///temp=////cond=////humid=*1*(15)Classifique a alimentação desse local#2#");
                printWriter.println("loc=-13.001808, -38.509093/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(16)Classifique a lotação desse local#2#");
                printWriter.println("loc=-13.002146, -38.506858/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(17)Classifique a lotação desse local#2#");
                printWriter.println("loc=-13.004598, -38.509471/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(18)Classifique a lotação desse local#2#");
                printWriter.println("loc=-13.000840, -38.507572/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(19)Classifique a lotação desse local#2#");
                printWriter.println("loc=-13.002146, -38.506858/hIni=/hFim=//rela=//car=TRUE///dep=///temp=////cond=////humid=*1*(20)Classifique o tempo que levou para encontrar uma vaga#2#");
                printWriter.println("loc=-13.004598, -38.509471/hIni=/hFim=//rela=//car=TRUE///dep=///temp=////cond=////humid=*1*(21)Classifique o tempo que levou para encontrar uma vaga#2#");
                printWriter.println("loc=-13.000840, -38.507572/hIni=/hFim=//rela=//car=TRUE///dep=///temp=////cond=////humid=*1*(22)Classifique o tempo que levou para encontrar uma vaga#2#");
                printWriter.println("loc=-13.001984, -38.508521/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(23)Classifique o alagamento nesse local#2#");
                printWriter.println("loc=-13.004598, -38.509471/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(24)Classifique o alagamento nesse local#2#");
                printWriter.println("loc=-13.002146, -38.506858/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(25)Classifique o alagamento nesse local#2#");
                printWriter.println("loc=-13.000840, -38.507572/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(26)Classifique o alagamento nesse local#2#");
                printWriter.println("loc=-13.002176, -38.507613/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(27)Classifique o alagamento nesse local#2#");
                printWriter.println("loc=-13.006051, -38.510077/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(28)Classifique o alagamento nesse local#2#");
                printWriter.println("loc=-13.001243, -38.507393/hIni=1200/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(29)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.000592, -38.507742/hIni=1200/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(30)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.001368, -38.508484/hIni=1200/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(31)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.002307, -38.508318/hIni=1200/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(32)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.001808, -38.509093/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(33)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.001984, -38.508521/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(34)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.004598, -38.509471/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(35)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.002146, -38.506858/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(36)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.000840, -38.507572/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(37)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.002176, -38.507613/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(38)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.006051, -38.510077/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(39)Classifique a claridade nesse local#2#");
                printWriter.println("loc=-13.002307, -38.508318/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(40)Classifique o tempo que levou na fila do RU#2#");
                printWriter.println("loc=-13.001368, -38.508484/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(41)Classifique o tempo que levou na fila da xerox/papelaria#2#");
                /******** pergunta **********/
                printWriter.println("loc=-13.002307, -38.508318/hIni=1200/hFim=2000//rela=//car=///dep=///temp=////cond=////humid=*1*(42)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.001808, -38.509093/hIni=1200/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(43)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.001984, -38.508521/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(44)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.004598, -38.509471/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(45)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.002146, -38.506858/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(46)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.000840, -38.507572/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(47)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.002176, -38.507613/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(48)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.006051, -38.510077/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(49)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.001243, -38.507393/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(50)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.000592, -38.507742/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(51)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.001368, -38.508484/hIni=1800/hFim=2200//rela=//car=///dep=///temp=////cond=////humid=*1*(52)Você se sente seguro nesse local?#1#");
                printWriter.println("loc=-13.001984, -38.508521/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(53)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.004598, -38.509471/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(54)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.002146, -38.506858/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(55)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.000840, -38.507572/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(56)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.002176, -38.507613/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(57)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.006051, -38.510077/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(58)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.001243, -38.507393/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(59)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.000592, -38.507742/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(60)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.001368, -38.508484/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(61)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.002307, -38.508318/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(62)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.001808, -38.509093/hIni=/hFim=//rela=//car=///dep=///temp=////cond=6////humid=*1*(63)Faltou energia nesse local hoje?#1#");
                printWriter.println("loc=-13.001243, -38.507393/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(64)Existe algum equipamento quebrado nesse local?#1#");
                printWriter.println("loc=-13.000592, -38.507742/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(65)Existe algum equipamento quebrado nesse local?#1#");
                printWriter.println("loc=-13.001368, -38.508484/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(66)Existe algum equipamento quebrado nesse local?#1#");
                printWriter.println("loc=-13.002307, -38.508318/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(67)Existe algum equipamento quebrado nesse local?#1#");
                printWriter.println("loc=-13.001808, -38.509093/hIni=/hFim=//rela=//car=///dep=///temp=////cond=////humid=*1*(68)Existe algum equipamento quebrado nesse local?#1#");
                printWriter.println("loc=-13.002146, -38.506858/hIni=/hFim=//rela=//car=TRUE///dep=///temp=////cond=////humid=*1*(69)Você passou por buracos na pista?#1#");
                printWriter.println("loc=-13.004598, -38.509471/hIni=/hFim=//rela=//car=TRUE///dep=///temp=////cond=////humid=*1*(70)Você passou por buracos na pista?#1#");
                printWriter.println("loc=-13.000840, -38.507572/hIni=/hFim=//rela=//car=TRUE///dep=///temp=////cond=////humid=*1*(71)Você passou por buracos na pista?#1#");
                printWriter.println("loc=-13.001243, -38.507393/hIni=/hFim=//rela=//car=///dep=///temp=28,38////cond=////humid=*1*(72)A temperatura neste local está agradável?#1#");
                printWriter.println("loc=-13.000592, -38.507742/hIni=/hFim=//rela=//car=///dep=///temp=28,38////cond=////humid=*1*(73)A temperatura neste local está agradável?#1#");
                printWriter.println("loc=-13.001368, -38.508484/hIni=/hFim=//rela=//car=///dep=///temp=28,38////cond=////humid=*1*(74)A temperatura neste local está agradável?#1#");
                printWriter.println("loc=-13.002307, -38.508318/hIni=/hFim=//rela=//car=///dep=///temp=28,38////cond=////humid=*1*(75)A temperatura neste local está agradável?#1#");
                printWriter.println("loc=-13.001808, -38.509093/hIni=/hFim=//rela=//car=///dep=///temp=28,38////cond=////humid=*1*(76)A temperatura neste local está agradável?#1#");

                printWriter.flush();

            } catch (IOException e) {
                AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
                TextView myMsg = new TextView(context);
                myMsg.setText("Error saving notification");
                myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
                popupBuilder.setView(myMsg);
                popupBuilder.show();
                //e.printStackTrace();
                return false;
            }
            return true;
        }
    }

    public boolean WritesNotification(Context context, ArrayList<String> list){
        File dir = processDir(context);
        if (dir == null) return false;
        File file = getNotificationStorageFile(context, dir, "notifications.txt");

        try {
            FileWriter fileWriter = new FileWriter(file, false);

            PrintWriter printWriter = new PrintWriter(fileWriter);
            for(int i=0; i<list.size(); i++){
                printWriter.println(list.get(i));
            }
            printWriter.flush();

        } catch (IOException e) {
            AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
            TextView myMsg = new TextView(context);
            myMsg.setText("Error saving notification");
            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            popupBuilder.setView(myMsg);
            popupBuilder.show();
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    public ArrayList<String> ReadsNotification(Context context) {
        File dir = processDir(context);
        File file = getNotificationStorageFile(context, dir, "notifications.txt");
        ArrayList<String> list = new ArrayList<String>();

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String linha = "";
            while ( ( linha = bufferedReader.readLine() ) != null) {
                //System.out.println(linha);
                list.add(linha);
            }
            fileReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            AlertDialog.Builder popupBuilder = new AlertDialog.Builder(context);
            TextView myMsg = new TextView(context);
            myMsg.setText("Error reading notification");
            myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
            popupBuilder.setView(myMsg);
            popupBuilder.show();
            //e.printStackTrace();
        }
        return list;
    }
}

