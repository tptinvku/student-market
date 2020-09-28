package sict.apps.studentmarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Content;
import sict.apps.studentmarket.models.Notification;

public class ListNotificationAdapter extends RecyclerView.Adapter<ListNotificationAdapter.NotificationViewHolder> {
    private LayoutInflater mInflater;
    private List<Content> lsNotification;
    private Context context;
    public ListNotificationAdapter(Context context, List<Content> lsNotification) {
        this.lsNotification = lsNotification;
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        String title = lsNotification.get(position).getContent();
        holder.notification_name.setText(title);
    }

    @Override
    public int getItemCount() {
        return lsNotification.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView notification_name;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_name = (TextView) itemView.findViewById(R.id.notification_name);
        }
    }
}
