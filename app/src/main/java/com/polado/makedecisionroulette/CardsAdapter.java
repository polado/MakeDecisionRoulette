package com.polado.makedecisionroulette;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by PolaDo on 9/14/2017.
 */

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {
    private Context context;

    private String editCardsReceiver = "home.card.receiver";
    private String deleteCardsReceiver = "home.delete.card.receiver";

    private ArrayList<String> titles;
    private ArrayList<ChoicesAdapter> recyclerAdapter;

    ArrayList<Roulette> systemRoulettes = new ArrayList<>();

    private long longDelay = 800, longDelayBreak = 700, shortDelay = 500, shortDelayBreak = 400;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView, runBtn;
        private TextView textView;
        private TextView optionsBtn;
        private RecyclerView recyclerView;

        private ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.roulette_cv);
            runBtn = (CardView) itemView.findViewById(R.id.card_run_btn);
            textView = (TextView) itemView.findViewById(R.id.roulette_tv);
            optionsBtn = (TextView) itemView.findViewById(R.id.options_btn);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.choices_rv);
        }
    }

    public CardsAdapter(Context context, ArrayList<String> titles, ArrayList<ChoicesAdapter> recyclerAdapter) {
        this.context = context;
        this.titles = titles;
        this.recyclerAdapter = recyclerAdapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        systemRoulettes.add(new Roulette(titles.get(position), recyclerAdapter.get(position).getChoices()));

        holder.textView.setText(titles.get(position));
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false);
        holder.recyclerView.setLayoutManager(layoutManager);
        holder.recyclerView.setAdapter(recyclerAdapter.get(position));

        holder.optionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(context, holder.optionsBtn);
//                popup.inflate(R.menu.card_options_menu);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.card_options_menu, popup.getMenu());

//                MenuItem menuItem = popup.getMenu().findItem(R.id.card_options_edit);
//                final View view = menuItem.getActionView();
//                view.setOnTouchListener(new View.OnTouchListener() {
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        Toast.makeText(context, event.toString(), Toast.LENGTH_SHORT).show();
//                    return true;
//                    }
//                });

//                MenuItem menuItem = popup.getMenu().findItem(R.id.card_options_edit);
//                View view = menuItem.getActionView();
//                if (view != null) {
//                    int[] location = new int[2];
//                    view.getLocationOnScreen(location);
//                    int x = location[0];
//                    int y = location[1];
//                    Log.d("location", "menu item location --> " + x + "," + y);
//                } else {
//
//                    Log.d("location", "menu item location --> null");
//                }

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.card_options_edit:
                                Intent editCardIntent = new Intent(editCardsReceiver);

                                editCardIntent.putExtra("position", position);
                                editCardIntent.putExtra("left", item.getActionView().getLeft());
                                editCardIntent.putExtra("right", item.getActionView().getRight());
                                editCardIntent.putExtra("top", item.getActionView().getTop());
                                editCardIntent.putExtra("bottom", item.getActionView().getBottom());

                                context.sendBroadcast(editCardIntent);
                                break;
                            case R.id.card_options_delete:
                                deleteRoulette(position);
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        holder.runBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int numberOfChoices = holder.recyclerView.getAdapter().getItemCount();
                Log.d("numberOfChoices", String.valueOf(numberOfChoices));
                if (numberOfChoices <= 5) {
                    for (int j = 0; j < 2; j++) {
                        Handler handler = new Handler();
                        final int finalJ = j;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("j", String.valueOf(finalJ));
                                for (int i = 0; i < numberOfChoices; i++) {
                                    RecyclerView.ViewHolder choiceHolder = holder.recyclerView.findViewHolderForAdapterPosition(i);
                                    final TextView choice = (TextView) choiceHolder.itemView.findViewById(R.id.choice_tv);
                                    final int finalI = i;
                                    choice.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            highlight(choice, finalI);
                                        }
                                    });
                                }
                            }
                        }, j * (numberOfChoices * shortDelay));
                    }

                    final int selection = randomSelection(numberOfChoices);
                    Log.d("selection", String.valueOf(selection));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i <= selection; i++) {
                                RecyclerView.ViewHolder choiceHolder = holder.recyclerView.findViewHolderForAdapterPosition(i);
                                final TextView choice = (TextView) choiceHolder.itemView.findViewById(R.id.choice_tv);
                                final int finalI = i;
                                choice.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        highlightSelected(choice, finalI);
                                    }
                                });
                            }
                        }
                    }, 2 * numberOfChoices * shortDelay);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.ViewHolder choiceHolder = holder.recyclerView.findViewHolderForAdapterPosition(selection);
                            final TextView choice = (TextView) choiceHolder.itemView.findViewById(R.id.choice_tv);
                            tellSelected(holder.textView.getText().toString(), choice.getText().toString());
                        }
                    }, (2 * numberOfChoices * shortDelay) + (selection * longDelay));
                } else {
                    for (int i = 0; i < numberOfChoices; i++) {
                        RecyclerView.ViewHolder choiceHolder = holder.recyclerView.findViewHolderForAdapterPosition(i);
                        final TextView choice = (TextView) choiceHolder.itemView.findViewById(R.id.choice_tv);
                        final int finalI = i;
                        choice.post(new Runnable() {
                            @Override
                            public void run() {
                                highlight(choice, finalI);
                            }
                        });
                    }

                    final int selection = randomSelection(numberOfChoices);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i <= selection; i++) {
                                RecyclerView.ViewHolder choiceHolder = holder.recyclerView.findViewHolderForAdapterPosition(i);
                                final TextView choice = (TextView) choiceHolder.itemView.findViewById(R.id.choice_tv);
                                final int finalI = i;
                                choice.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        highlightSelected(choice, finalI);
                                    }
                                });
                            }
                        }
                    }, numberOfChoices * shortDelay);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            RecyclerView.ViewHolder choiceHolder = holder.recyclerView.findViewHolderForAdapterPosition(selection);
                            final TextView choice = (TextView) choiceHolder.itemView.findViewById(R.id.choice_tv);
                            tellSelected(holder.textView.getText().toString(), choice.getText().toString());
                        }
                    }, (numberOfChoices * shortDelay) + (selection * longDelay));
                }
            }
        });
    }

    private void highlight(final TextView t, int i) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                t.setBackgroundColor(context.getResources().getColor(R.color.colorAccentLight));
                t.setTextColor(context.getResources().getColor(R.color.colorTextOnS));
            }
        }, (shortDelay * i));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                t.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
                t.setTextColor(context.getResources().getColor(R.color.colorTextOnP));
            }
        }, shortDelayBreak + (shortDelay * i));
    }

    private void highlightSelected(final TextView t, int i) {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                t.setBackgroundColor(context.getResources().getColor(R.color.colorAccentLight));
                t.setTextColor(context.getResources().getColor(R.color.colorTextOnS));
            }
        }, (longDelay * i));

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                t.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
                t.setTextColor(context.getResources().getColor(R.color.colorTextOnP));
            }
        }, longDelayBreak + (longDelay * i));
    }

    private int randomSelection(int count) {
        Random random = new Random();
        return random.nextInt(count);
    }

    private void tellSelected(String title, String selection) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
        dialog.setTitle("Roulette : " + title)
                .setMessage("The selection is : " + selection);
        final AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    public void deleteRoulette(int position) {
        titles.remove(position);
        recyclerAdapter.remove(position);
        systemRoulettes.remove(position);
        new ReadWriteData(context).writeData(systemRoulettes);
        notifyDataSetChanged();

        Intent deleteCardIntent = new Intent(deleteCardsReceiver);
        deleteCardIntent.putExtra("position", position);
        context.sendBroadcast(deleteCardIntent);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }
}
