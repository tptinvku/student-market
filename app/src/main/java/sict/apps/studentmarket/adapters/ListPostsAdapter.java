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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.models.Likes;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.models.React;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.ultil.Server;

public class ListPostsAdapter extends RecyclerView.Adapter<ListPostsAdapter.ListProductViewHolder> {
    private static final String TAG = ListPostsAdapter.class.getSimpleName() ;
    private Context context;
    private LayoutInflater mInflater;
    private List<Post> posts = new ArrayList<>();
    private OnItemClickListener mListener;
    private Retrofit retrofit = null;
    private String userId;

    public ListPostsAdapter(Context context, List<Post> posts, String userId) {
        this.context = context;
        this.posts = posts;
        this.userId = userId;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ListProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_post_layout, parent, false);
        return new ListProductViewHolder(view, mListener, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ListProductViewHolder holder, int position) {
        String postId = posts.get(position).get_id();
        String name = posts.get(position).getProduct_name();
        String path = posts.get(position).getProduct_imagePath();
        NumberFormat formatter = new DecimalFormat("#,###");
        String price = formatter.format(posts.get(position).getProduct_price());
        long timeStamp = posts.get(position).getTimestamp();
        Format format = new SimpleDateFormat("EEE, dd/MM/yyyy");
        String time = format.format(timeStamp);
        String address = posts.get(position).getAddress();
        int numLike = posts.get(position).getLikes();
        holder.productName.setText(name);
        holder.productPrice.setText(price);
        holder.timeStamp.setText(time);
        holder.address.setText(address);
        holder.num_like.setText(String.valueOf(numLike));
        Picasso.get()
                .load(path)
                .into(holder.productImage);
        holder.connectApi(postId, userId);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onFavorite(int position);
    }
    public void setOnItemClickListener(ListPostsAdapter.OnItemClickListener listener) {
        mListener = listener;
    }
    public class ListProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private ImageView favorite;
        private TextView productName;
        private TextView productPrice;
        private TextView timeStamp;
        private TextView address;
        private TextView num_like;
        private boolean love = true;
        private ListPostsAdapter adapter;

        public ListProductViewHolder(@NonNull View itemView,  final OnItemClickListener listener, ListPostsAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            productImage = (ImageView) itemView.findViewById(R.id.img_view);
            favorite = (ImageView) itemView.findViewById(R.id.btn_favorite);
            productName = (TextView) itemView.findViewById(R.id.productName);
            productPrice = (TextView) itemView.findViewById(R.id.productPrice);
            timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);
            address = (TextView) itemView.findViewById(R.id.address);
            num_like = (TextView) itemView.findViewById(R.id.num_like);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if(love == true){
                            mListener.onFavorite(position);
                            favorite.setImageResource(R.drawable.ic_favorite_red_32dp);
                            love = false;
                        }else {
                            mListener.onFavorite(position);
                            favorite.setImageResource(R.drawable.ic_favorite_border_red_32dp);
                            love = true;
                        }
                    }

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
        public void connectApi(String postId, String userId){
            if(retrofit==null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(Server.API_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                APIService apiService = retrofit.create(APIService.class);
                Call<List<React>> call = apiService.getReacts(postId);
                call.enqueue(new Callback<List<React>>() {
                    @Override
                    public void onResponse(Call<List<React>> call, Response<List<React>> response) {
                        if(response.isSuccessful()) {
                            if(response.body().size()>0){
                                List<Likes> likes = response.body().get(0).getLikes();
                                for (int i = 0; i < likes.size() ; i++) {
                                    if(userId.equals(likes.get(i).getUserId())){
                                        favorite.setImageResource(R.drawable.ic_favorite_red_32dp);
                                        love = false;
                                        break;
                                    }else{
                                        favorite.setImageResource(R.drawable.ic_favorite_border_red_32dp);
                                        love = true;
                                    }
                                }
                                call.cancel();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<React>> call, Throwable t) {
                        call.cancel();
                    }
                });
                retrofit = null;
            }

        }
    }
}
