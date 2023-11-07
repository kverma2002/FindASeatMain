package com.example.findaseat.Utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findaseat.BlankFragment;
import com.example.findaseat.R;
import com.example.findaseat.SeatFragment;

import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatCardViewHolder> {

    private List<Seat> seatList; // Replace with your Seat data model.

    private FragmentManager fragmentManager;
    Context context;

    User user;

    Building building;

    public SeatAdapter(List<Seat> seatList, FragmentManager fragmentManager, Context context, User user, Building building) {
        this.seatList = seatList;
        this.fragmentManager = fragmentManager;
        this.context = context;
        this.user = user;
        this.building = building;
    }

    @NonNull
    @Override
    public SeatCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_seat, parent, false);
        return new SeatCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatCardViewHolder holder, int position) {
        Seat seat = seatList.get(position);
        holder.roomNameTextView.setText(String.valueOf(seat.getRoomName()));
        holder.seatNumberTextView.setText("Seat: " + String.valueOf(seat.getSeatNumber()));
        holder.descriptionTextView.setText(String.valueOf(seat.getDescription()));
        System.out.println(seat.getSeatNumber());
        System.out.println(seat.getDescription());
        System.out.println(seat.getRoomName());

        holder.itemView.setOnClickListener(view -> {
            // Handle card click here.
            // You can navigate to the SeatDetailsFragment when a card is clicked.
            System.out.println(user.getLoggedIn());
            if (user.getLoggedIn()) {
                navigateToSeatDetailsFragment(seat);
            } else {
                Toast.makeText(context, "Log In First", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void navigateToSeatDetailsFragment(Seat seat) {
        SeatFragment sf = new SeatFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("seat", seat);
        bundle.putSerializable("building", building);
        if (building == null) {
            Log.d("CustomAdapter", "AHHHHHH");
        }
        sf.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, sf);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    public static class SeatCardViewHolder extends RecyclerView.ViewHolder {
        TextView roomNameTextView;
        TextView seatNumberTextView;

        TextView descriptionTextView;

        public SeatCardViewHolder(@NonNull View itemView) {
            super(itemView);
            roomNameTextView = itemView.findViewById(R.id.roomNameTextView);
            seatNumberTextView = itemView.findViewById(R.id.seatNumberTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
