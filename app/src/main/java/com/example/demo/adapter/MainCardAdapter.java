package com.example.demo.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.R;
import com.example.demo.utils.CardInfo;
import com.example.demo.utils.CardType;

import java.util.List;


public class MainCardAdapter extends RecyclerView.Adapter<MainCardAdapter.ViewHolder> {

    private List<CardInfo> cardList;

    public MainCardAdapter(List<CardInfo> cardList) {
        this.cardList = cardList;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CardInfo card = cardList.get(position);
        holder.cardImage.setImageResource(card.getImageId());
        holder.cardName.setText(card.getName());
        holder.cardDesc.setText(card.getDesc());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View cardView;
        ImageView cardImage;
        TextView cardName;
        TextView cardDesc;


        public ViewHolder(View view) {
            super(view);
            cardView = view;
            cardImage = (ImageView) view.findViewById(R.id.card_image);
            cardName = (TextView) view.findViewById(R.id.card_name);
            cardDesc = (TextView) view.findViewById(R.id.card_desc);
            Log.i("Card", "card_name=" + cardName);
        }
    }

    public interface ItemClickListener {
        public void onClick(CardType cardType);
    }
}
