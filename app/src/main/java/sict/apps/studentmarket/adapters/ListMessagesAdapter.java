package sict.apps.studentmarket.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListMessagesAdapter extends RecyclerView.Adapter<ListMessagesAdapter.ListMessageViewHolder> {
    @NonNull
    @Override
    public ListMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ListMessageViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ListMessageViewHolder extends RecyclerView.ViewHolder {
        public ListMessageViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
