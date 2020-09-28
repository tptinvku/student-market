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
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.Result;
import sict.apps.studentmarket.ultil.Server;

public class ListResultsFoundAdapter extends RecyclerView.Adapter<ListResultsFoundAdapter.ListResultFoundViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private OnItemClickListener mListener;
    private List<Result> lsResult;
    public ListResultsFoundAdapter(Context context, List<Result> lsResult) {
        this.context = context;
        this.lsResult = lsResult;
        mInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public ListResultFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_search_result, parent, false);
        return new ListResultFoundViewHolder(view, this, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListResultFoundViewHolder holder, int position) {
        String postId = lsResult.get(position).get_id();
        String name = lsResult.get(position).getProduct_name();
        String path = Server.API_URL + lsResult.get(position).getProduct_imagePath();
        NumberFormat formatter = new DecimalFormat("#,###");
        String price = formatter.format(lsResult.get(position).getProduct_price());
        holder.productName.setText(name);
        holder.productPrice.setText(price);
        Picasso.get()
                .load(path)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return lsResult.size();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(ListResultsFoundAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public class ListResultFoundViewHolder extends RecyclerView.ViewHolder {
        private TextView productName;
        private TextView productPrice;
        private ImageView productImage;
        private ListResultsFoundAdapter adapter;
        public ListResultFoundViewHolder(@NonNull View itemView, ListResultsFoundAdapter adapter, OnItemClickListener listener) {
            super(itemView);
            this.adapter = adapter;
            productImage = (ImageView) itemView.findViewById(R.id.img_view);
            productName = (TextView) itemView.findViewById(R.id.productName);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
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
