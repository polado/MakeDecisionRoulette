package com.polado.makedecisionroulette;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.scalified.fab.ActionButton;

import java.util.ArrayList;

import io.codetail.animation.ViewAnimationUtils;

public class Home extends AppCompatActivity {

    RecyclerView recyclerView;
//    ActionButton floatingActionButton;
    FloatingActionButton floatingActionButton;
    CardView addRouletteCard;
    LinearLayout linearLayout;
    EditText rouletteTitleEditText;

    Animation scaleDown, scaleUp;

    //    long duration = (long) Settings.Global.getFloat(getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE, 1.0f);
    boolean hidden = true;

    //    ArrayList<EditText> editTexts = new ArrayList<>();
    ArrayList<LinearLayout> linearLayouts = new ArrayList<>();

    CardsAdapter cardsAdapter;
    ArrayList<String> titles;
    ArrayList<ChoicesAdapter> choicesAdapter;

    ArrayList<Roulette> systemRoulettes = new ArrayList<>();

    String editCardsReceiver = "home.card.receiver";
    private String deleteCardsReceiver = "home.delete.card.receiver";

    View parentView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        scaleDown = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_down);
        scaleUp = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_up);
        scaleDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                floatingActionButton.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        scaleUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                floatingActionButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

//        floatingActionButton = (ActionButton) findViewById(R.id.fab);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        rouletteTitleEditText = (EditText) findViewById(R.id.roulette_name_et);
        recyclerView = (RecyclerView) findViewById(R.id.cards_rv);

        systemRoulettes = new ArrayList<>();
        titles = new ArrayList<>();

        choicesAdapter = new ArrayList<>();

        cardsAdapter = new CardsAdapter(this, titles, choicesAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(cardsAdapter);

        addRouletteCard = (CardView) findViewById(R.id.add_roulette_cv);
        addRouletteCard.setVisibility(View.INVISIBLE);

        linearLayout = (LinearLayout) findViewById(R.id.add_choice_ll);

        CardView addChoice = (CardView) findViewById(R.id.add_choice_btn);
        addChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                EditText editText = new EditText(Home.this);
//                editText.setHint("Enter Choice");
//                editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                        , ViewGroup.LayoutParams.WRAP_CONTENT));
//                linearLayout.addView(editText);
//                editText.requestFocus();
//                editTexts.add(editText);

                LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_choice_et, null);
                linearLayout.addView(layout);
                linearLayouts.add(layout);

                final TextView countTV = (TextView) layout.getChildAt(0);
                countTV.setText(String.valueOf(linearLayouts.size()));

                layout.getChildAt(1).requestFocus();

                ImageButton b = (ImageButton) layout.getChildAt(2);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        linearLayouts.remove(Integer.parseInt(countTV.getText().toString()) - 1);
                        linearLayout.removeViewAt(Integer.parseInt(countTV.getText().toString()) - 1);
                        resetCount();
                    }
                });
            }
        });

        CardView submit = (CardView) findViewById(R.id.submit_choices_btn);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (LinearLayout l : linearLayouts) {
                    EditText e = (EditText) l.getChildAt(1);
                    if (e.getText().toString().equals("")) {
                        Toast.makeText(Home.this, "Fill all of the choices", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

//                for (EditText e : editTexts) {
//                    if (e.getText().toString().equals("")) {
//                        Toast.makeText(Home.this, "Fill all of the choices", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }

                if (rouletteTitleEditText.getText().toString().equals("") || linearLayouts.size() < 2) {
                    Toast.makeText(Home.this, "Must write roulette name and have at least 2 choices", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (parentView.getId() == R.id.fab) {
                    reverseAnimation(floatingActionButton);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addRoulette();
                        }
                    }, 800);
                } else {
                    reverseAnimation(parentView);
                    final int position = recyclerView.getChildAdapterPosition(parentView);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateRoulette(position);
                        }
                    }, 800);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (hidden)
            super.onBackPressed();
        else {
            reverseAnimation(parentView);
            if (parentView.getId() != R.id.fab) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linearLayout.removeAllViews();
                        linearLayouts.clear();
                        rouletteTitleEditText.setText("");
                    }
                }, 800);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_bar_clear_all:
                deleteAll();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(editCardBroadcastReceiver);
        unregisterReceiver(deleteCardBroadcastReceiver);

//        new ReadWriteData(this).writeData(systemRoulettes);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(editCardBroadcastReceiver, new IntentFilter(editCardsReceiver));
        registerReceiver(deleteCardBroadcastReceiver, new IntentFilter(deleteCardsReceiver));

        if (systemRoulettes.size() == 0) {
            systemRoulettes = new ReadWriteData(this).readData();
            if (systemRoulettes != null)
                update();
            else
                systemRoulettes = new ArrayList<>();
        }
    }

    public void update() {
        for (Roulette r : systemRoulettes) {
            titles.add(r.getTitle());
            choicesAdapter.add(new ChoicesAdapter(this, r.getChoices()));
        }
        cardsAdapter.notifyDataSetChanged();
    }

    public void addRoulette() {
        resetCount();

        linearLayout.removeAllViews();

        ArrayList<String> choices = new ArrayList<>();

        for (int i = 0; i < choices.size(); i++) {
            choicesAdapter.add(new ChoicesAdapter(this, choices));
        }

        String title = rouletteTitleEditText.getText().toString().trim();
        rouletteTitleEditText.setText("");
        titles.add(title);

        for (LinearLayout l : linearLayouts) {
            EditText e = (EditText) l.getChildAt(1);
            choices.add(e.getText().toString().trim());
        }
        linearLayouts.clear();

//        for (EditText e : editTexts) {
//            choices.add(e.getText().toString().trim());
//        }
//        editTexts.clear();

        choicesAdapter.add(new ChoicesAdapter(this, choices));

        cardsAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(choicesAdapter.size());

        Roulette roulette = new Roulette(title, choices);
        systemRoulettes.add(roulette);

        new ReadWriteData(this).writeData(systemRoulettes);
    }

    public void updateRoulette(int position) {
        resetCount();

        linearLayout.removeAllViews();

        ArrayList<String> choices = new ArrayList<>();

        for (int i = 0; i < choices.size(); i++) {
            choicesAdapter.set(position, new ChoicesAdapter(this, choices));
        }

        String title = rouletteTitleEditText.getText().toString().trim();
        rouletteTitleEditText.setText("");
        titles.set(position, title);

        for (LinearLayout l : linearLayouts) {
            EditText e = (EditText) l.getChildAt(1);
            choices.add(e.getText().toString().trim());
        }
        linearLayouts.clear();

//        for (EditText e : editTexts) {
//            choices.add(e.getText().toString().trim());
//        }
//        editTexts.clear();

        choicesAdapter.set(position, new ChoicesAdapter(this, choices));

        cardsAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(position);

        Roulette roulette = new Roulette(title, choices);
        systemRoulettes.set(position, roulette);

        new ReadWriteData(this).writeData(systemRoulettes);
    }

    public void startAnimation(View v, Long delay) {
//        floatingActionButton.hide();
        floatingActionButton.startAnimation(scaleDown);

        int cx = (v.getLeft() + v.getRight()) / 2;
        int cy = (v.getTop() + v.getBottom()) / 2;

        float radius = Math.max(addRouletteCard.getWidth(), addRouletteCard.getHeight()) * 2.0f;

        Animator animator = ViewAnimationUtils.createCircularReveal(addRouletteCard, cx, cy, 0, radius)
                .setDuration(800);
        animator.setStartDelay(delay);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                addRouletteCard.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                recyclerView.setVisibility(View.INVISIBLE);
            }
        });
        animator.start();

        hidden = false;
    }

    public void reverseAnimation(View v) {
        recyclerView.setVisibility(View.VISIBLE);

        int cx = (v.getLeft() + v.getRight()) / 2;
        int cy = (v.getTop() + v.getBottom()) / 2;

        float radius = Math.max(addRouletteCard.getWidth(), addRouletteCard.getHeight()) * 2.0f;

        Animator animator_reverse = ViewAnimationUtils.createCircularReveal(addRouletteCard, cx, cy, radius, 0)
                .setDuration(800);

        animator_reverse.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                addRouletteCard.setVisibility(View.INVISIBLE);
//                floatingActionButton.show();
                floatingActionButton.startAnimation(scaleUp);
                hidden = true;
                parentView = null;
            }
        });
        animator_reverse.start();

    }

    public void add(View v) {
        if (hidden) {
            parentView = floatingActionButton;
            startAnimation(v, (long) 400);
            rouletteTitleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
//                            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });
            rouletteTitleEditText.requestFocus();
        } else {
            reverseAnimation(v);
        }
    }

    private BroadcastReceiver deleteCardBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position", -1);

            systemRoulettes.remove(position);
            new ReadWriteData(context).writeData(systemRoulettes);
        }
    };

    private BroadcastReceiver editCardBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int position = intent.getIntExtra("position", -1);

            int left = intent.getIntExtra("left", -1);
            int right = intent.getIntExtra("right", -1);
            int top = intent.getIntExtra("top", -1);
            int bottom = intent.getIntExtra("bottom", -1);

            Log.i("editCardReceiver", position + " " + left + " " + right);

            View v = recyclerView.getChildAt(position);

            parentView = v;

            startAnimation(v, (long) 0);

            RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                TextView rouletteTitle = (TextView) holder.itemView.findViewById(R.id.roulette_tv);
                rouletteTitleEditText.setText(rouletteTitle.getText().toString());

                RecyclerView recyclerView = (RecyclerView) holder.itemView.findViewById(R.id.choices_rv);
                for (int i = 0; i < recyclerView.getAdapter().getItemCount(); i++) {
                    RecyclerView.ViewHolder choiceHolder = recyclerView.findViewHolderForAdapterPosition(i);
                    TextView choiceTV = (TextView) choiceHolder.itemView.findViewById(R.id.choice_tv);

//                    EditText editText = new EditText(Home.this);
//                    editText.setText(choiceTV.getText().toString());
//                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
//                    editText.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                            , ViewGroup.LayoutParams.WRAP_CONTENT));
//                    linearLayout.addView(editText);
//                    editTexts.add(editText);

                    LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.add_choice_et, null);
                    EditText e = (EditText) layout.getChildAt(1);
                    e.setText(choiceTV.getText().toString());
                    linearLayout.addView(layout);
                    linearLayouts.add(layout);

                    final TextView countTV = (TextView) layout.getChildAt(0);
                    countTV.setText(String.valueOf(linearLayouts.size()));

                    ImageButton b = (ImageButton) layout.getChildAt(2);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            linearLayouts.remove(Integer.parseInt(countTV.getText().toString()) - 1);
                            linearLayout.removeViewAt(Integer.parseInt(countTV.getText().toString()) - 1);
                            resetCount();
                        }
                    });
                }
            }
        }
    };

    void deleteAll() {
        if (parentView == null) {

            final AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this, R.style.CustomAlertDialog);
            dialog.setTitle("Delete All")
                    .setMessage("Are you sure you want to delete all the Roulettes?\n" +
                            "This action can't be undone.")
                    .setPositiveButton("I'm Sure !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            systemRoulettes.clear();
                            titles.clear();
                            choicesAdapter.clear();
                            cardsAdapter.notifyDataSetChanged();
                            deleteFile("data");

                            Snackbar.make(getCurrentFocus(), "It's Done", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Not Sure, Cancel !", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Snackbar.make(getCurrentFocus(), "Canceled, Be sure next time !!", Snackbar.LENGTH_LONG).show();
                        }
                    });
            final AlertDialog alertDialog = dialog.create();
            alertDialog.show();
        } else if (parentView.getId() == R.id.fab)
            Toast.makeText(this, "Finish adding your Roulette first !", Toast.LENGTH_SHORT).show();

        else
            Toast.makeText(this, "Finish editing your Roulette first !", Toast.LENGTH_SHORT).show();
    }

    void resetCount() {
        for (int i = 0; i < linearLayouts.size(); i++) {
            LinearLayout l = linearLayouts.get(i);

            TextView t = (TextView) l.getChildAt(0);
            t.setText(i + 1 + "");

//            l.getChildAt(1).requestFocus();
        }
    }
}
