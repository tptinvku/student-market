package sict.apps.studentmarket.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import sict.apps.studentmarket.R;

public class ListCapturesAdapter  extends RecyclerView.Adapter<ListCapturesAdapter.ListCapturesViewHolder> {
    private Context context;
    private OnItemClickListener mListener;
    private LayoutInflater mInflater;
    private List<String> lsPath = new ArrayList<>();

    public ListCapturesAdapter(Context context, List<String> lsPath) {
        this.context = context;
        this.lsPath = lsPath;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListCapturesAdapter.ListCapturesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = mInflater.inflate(R.layout.item_image_post, parent, false);
        return new ListCapturesViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListCapturesAdapter.ListCapturesViewHolder holder, int position) {
        Bitmap imageBitmap = BitmapFactory.decodeFile(lsPath.get(position));
        holder.imageView.setImageBitmap(imageBitmap);
    }

    @Override
    public int getItemCount() {
        return lsPath.size();
    }
    public interface OnItemClickListener {
        void onDeleteItem(int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
    public class ListCapturesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private ImageView btn_remove;
        public ListCapturesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_view);
            btn_remove =(ImageView) itemView.findViewById(R.id.btn_remove);
            btn_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mListener.onDeleteItem(position);
                }
            });
        }

    }
}
