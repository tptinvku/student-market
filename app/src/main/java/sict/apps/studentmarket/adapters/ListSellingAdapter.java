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
import sict.apps.studentmarket.models.Post;

public class ListSellingAdapter extends RecyclerView.Adapter<ListSellingAdapter.ListOrdersViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;
    private List<Post> lsPosts = new ArrayList<>();
    public ListSellingAdapter(Context context, List<Post> lsPosts) {
        this.context = context;
        this.lsPosts = lsPosts;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListOrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_post_selling, parent, false);
        return new ListOrdersViewHolder(view, this, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListOrdersViewHolder holder, int position) {
        String id = lsPosts.get(position).get_id();
        String name = lsPosts.get(position).getProduct_name();
        NumberFormat formatter = new DecimalFormat("#,###");
        String price = formatter.format(lsPosts.get(position).getProduct_price());
        String path = lsPosts.get(position).getProduct_imagePath();
        holder.productName.setText(name);
        holder.productPrice.setText(price);
        Picasso.get()
                .load(path)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return lsPosts.size();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteItem(int position);
        void onEditItem(int position);
    }
    public void setOnItemClickListener(ListSellingAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public class ListOrdersViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productPrice;
        private ImageView productImage;
        private ImageView btn_del;
        private ImageView btn_edi;
        private ListSellingAdapter adapter;
        public ListOrdersViewHolder(@NonNull View itemView, ListSellingAdapter adapter, OnItemClickListener listener) {
            super(itemView);
            this.adapter = adapter;
            productImage = (ImageView) itemView.findViewById(R.id.img_view);
            productName = (TextView) itemView.findViewById(R.id.productName);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
            btn_edi = (ImageView) itemView.findViewById(R.id.btn_edit);
            btn_del = (ImageView) itemView.findViewById(R.id.btn_delete);
            btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mListener.onDeleteItem(position);
                }
            });
            btn_edi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mListener.onEditItem(position);
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
