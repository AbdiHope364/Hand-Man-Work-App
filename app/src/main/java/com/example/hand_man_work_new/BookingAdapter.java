package com.example.hand_man_work_new;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hand_man_work_new.databinding.ItemBookingBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    private List<Booking> bookingList;

    public BookingAdapter(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingBinding binding = ItemBookingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        holder.binding.tvCustomerName.setText(booking.getCustomerName());
        holder.binding.tvStatus.setText("Status: " + booking.getStatus());

        holder.binding.btnAccept.setOnClickListener(v -> updateStatus(booking.getBookingId(), "accepted"));
        holder.binding.btnReject.setOnClickListener(v -> updateStatus(booking.getBookingId(), "rejected"));
    }

    private void updateStatus(String id, String status) {
        FirebaseFirestore.getInstance().collection("bookings").document(id).update("status", status);
    }

    @Override
    public int getItemCount() { return bookingList.size(); }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        ItemBookingBinding binding;
        BookingViewHolder(ItemBookingBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}
