package com.polado.makedecisionroulette;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by PolaDo on 9/17/2017.
 */

public class ReadWriteData {
    private Context context;
    private String separator = "@#";

    public ReadWriteData (Context context) {
        this.context = context;
    }

    public void writeData(ArrayList<Roulette> roulette) {
        String fileName = "data";

        if (roulette.size()==0)
            return;

        String data = "";
        for (int i = 0; i< roulette.size(); i++) {
            data+= roulette.get(i).getTitle();
            for (int j = 0; j< roulette.get(i).getChoices().size(); j++) {
                data+=","+ roulette.get(i).getChoices().get(j);
            }
            data+=separator;
        }

        Log.i("write data", data);

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public ArrayList<Roulette> readData() {
        ArrayList<Roulette> roulette = new ArrayList<>();

        String fileName = "data";
        String ret = "-1";

        ArrayList<String> list = new ArrayList<>();

        try {
            InputStream inputStream = context.openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                    list.add(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("home activity", "File not found: " + e.toString());
            ret = "-1";
        } catch (IOException e) {
            Log.e("home activity", "Can not read file: " + e.toString());
            ret = "-1";
        }

        Log.i("read data", ret+", "+list);

        if (ret.equals("-1") || ret.equals("empty"))
            return null;

        String[] arr = ret.split(separator);
        for (String index: arr) {
            String[] rouletteStrings = index.split(",");

            String title = rouletteStrings[0];

            ArrayList<String> choices = new ArrayList<>();
            choices.addAll(Arrays.asList(rouletteStrings).subList(1, rouletteStrings.length));

            roulette.add(new Roulette(title, choices));
        }

        return roulette;
    }
}
