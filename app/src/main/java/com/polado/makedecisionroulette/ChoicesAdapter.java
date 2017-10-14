package com.polado.makedecisionroulette;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by PolaDo on 9/14/2017.
 */

public class ChoicesAdapter extends RecyclerView.Adapter<ChoicesAdapter.ViewHolder> {
    private Context context;
    private ArrayList<String> choices;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;
        private TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.choice_cv);
            textView = (TextView) itemView.findViewById(R.id.choice_tv);
        }
    }

    public ChoicesAdapter(Context context,ArrayList<String> choices) {
        this.context = context;
        this.choices = choices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choice, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(choices.get(position));
    }

    public ArrayList<String> getChoices() {
        return choices;
    }

    @Override
    public int getItemCount() {
        return choices.size();
    }
}
