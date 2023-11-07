package com.example.findaseat.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.findaseat.R;

import java.util.List;

public class ReservationSeatAdapter extends RecyclerView.Adapter<ReservationSeatAdapter.ReservationViewHolder> {

    private List<Reservation> reservations;


    public ReservationSeatAdapter(List<Reservation> reservations) {
        this.reservations = reservations;
    }




    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.current_reservation_item, parent, false);
        return new ReservationSeatAdapter.ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);

        // Bind the reservation data to the UI elements
        holder.startTimeTextView.setText(reservation.getStartTime());
        holder.endTimeTextView.setText(reservation.getEndTime());
        holder.roomIdTextView.setText(reservation.getRoom());
        holder.dateTextView.setText(reservation.getDate());
    }





    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public class ReservationViewHolder extends RecyclerView.ViewHolder {
        TextView startTimeTextView;
        TextView endTimeTextView;
        TextView roomIdTextView;

        TextView dateTextView;


        public ReservationViewHolder(View itemView) {
            super(itemView);
            startTimeTextView = itemView.findViewById(R.id.startTimeTextView);
            endTimeTextView = itemView.findViewById(R.id.endTimeTextView);
            roomIdTextView = itemView.findViewById(R.id.roomIdTextView);
            dateTextView = itemView.findViewById(R.id.dateIdTextView);
        }
    }


}
