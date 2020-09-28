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

import java.util.ArrayList;
import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Category;

public class ListCategoriesAdapter extends RecyclerView.Adapter<ListCategoriesAdapter.ListCategoriesViewHolder> {
    private Context context;
    private List<Category> lsCategories = new ArrayList<Category>();
    private OnItemClickListener mListener;
    private LayoutInflater mInflater;
    public ListCategoriesAdapter(Context context, List<Category> lsCategories) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        this.lsCategories = lsCategories;
    }

    @NonNull
    @Override
    public ListCategoriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_category, parent, false);
        return new ListCategoriesViewHolder(rootView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ListCategoriesViewHolder holder, int position) {
        String path = lsCategories.get(position).getCategory_imagePath();
        String name = lsCategories.get(position).getCategory_name();
        holder.name.setText(name);
        Picasso.get()
                .load(path)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return lsCategories.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public class ListCategoriesViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView imageView;
        public ListCategoriesViewHolder(@NonNull View itemView,
                                        final OnItemClickListener listener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.categoryName);
            imageView = (ImageView) itemView.findViewById(R.id.img_view);
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
