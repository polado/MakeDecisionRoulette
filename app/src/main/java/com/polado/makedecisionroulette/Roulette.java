package com.polado.makedecisionroulette;

import java.util.ArrayList;

/**
 * Created by PolaDo on 9/17/2017.
 */

public class Roulette {
    private String title;
    private ArrayList<String> choices;

    public Roulette(String title, ArrayList<String> choices) {
        this.title = title;
        this.choices = choices;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    public void setChoices(ArrayList<String> choices) {
        this.choices = choices;
    }

}
