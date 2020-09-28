package sict.apps.studentmarket.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.ultil.Server;

public class ListImagesAdapter extends RecyclerView.Adapter<ListImagesAdapter.ListImagesViewHolder> {
    private static final String TAG = ListImagesAdapter.class.getSimpleName();
    private Context context;
    private List<String> paths;
    private LayoutInflater mInflater;
    private OnClickImageShow mListener;
    public ListImagesAdapter(Context context, List<String> paths) {
        this.context = context;
        this.paths = paths;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListImagesAdapter.ListImagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_image, parent, false);
        return new ListImagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListImagesAdapter.ListImagesViewHolder holder, int position) {
        String path = Server.API_URL+paths.get(position);
        Log.d(TAG, "onBindViewHolder: "+path);
        Picasso.get()
                .load(path)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }

    public interface OnClickImageShow{
        void showImage(int position);
    }
    public void setOnClickShowListener(ListImagesAdapter.OnClickImageShow listener){
        mListener = listener;
    }
    public class ListImagesViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ListImagesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mListener.showImage(position);
                }
            });
        }
    }
}
