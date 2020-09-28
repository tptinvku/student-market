package sict.apps.studentmarket.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Cart;
import sict.apps.studentmarket.ultil.MySqlite;

public class ListOrdersAdapter extends RecyclerView.Adapter<ListOrdersAdapter.ListOrdersViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;
    private List<Cart> lsOrders = new ArrayList<>();
    private MySqlite database;
    public ListOrdersAdapter(Context context, List<Cart> lsOrders, MySqlite db) {
        this.context = context;
        this.database = db;
        this.lsOrders = lsOrders;
        mInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public ListOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_order, parent, false);
        return new ListOrdersViewHolder(view, this, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListOrdersViewHolder holder, int position) {
        String id = lsOrders.get(position).getItem().get_id();
        String name = lsOrders.get(position).getItem().getProduct_name();
        NumberFormat formatter = new DecimalFormat("#,###");
        String price = formatter.format(lsOrders.get(position).getItem().getProduct_price());
        String path = lsOrders.get(position).getItem().getProduct_imagePath();
        holder.productName.setText(name);
        holder.productPrice.setText(price);
        Picasso.get()
                .load(path)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return lsOrders.size();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteItem(int position);
    }
    public void setOnItemClickListener(ListOrdersAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public class ListOrdersViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productPrice;
        private ImageView productImage;
        private ImageView button;
        private ListOrdersAdapter adapter;
        public ListOrdersViewHolder(@NonNull View itemView, ListOrdersAdapter adapter, OnItemClickListener listener) {
            super(itemView);
            this.adapter = adapter;
            productImage = (ImageView) itemView.findViewById(R.id.img_view);
            productName = (TextView) itemView.findViewById(R.id.productName);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
            button = (ImageView) itemView.findViewById(R.id.btn_delete);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mListener.onDeleteItem(position);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
