package sict.apps.studentmarket.ui.order;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import sict.apps.studentmarket.R;
import sict.apps.studentmarket.adapters.ListOrdersAdapter;
import sict.apps.studentmarket.models.Cart;
import sict.apps.studentmarket.models.Post;
import sict.apps.studentmarket.ultil.MySqlite;

public class OrderUI extends Fragment implements ListOrdersAdapter.OnItemClickListener {
    private static final String TAG = OrderUI.class.getSimpleName();
    private RecyclerView orderRecyclerView;
    private TextView totalPrice;
    private ListOrdersAdapter adapter;
    private MySqlite database;
    private List<Cart> lsOrders = new ArrayList<>();
    private NumberFormat formatter = new DecimalFormat("#,###");
    private Cursor cs;
    private String token = "";
    private String userId = "";
    private String username= "";
    private long phone;
    private boolean gender;
    private String email = "";
    public static OrderUI newInstance() {
        return new OrderUI();
    }
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.order_layout, container, false);
        database = new MySqlite(getContext());
        loadSqlite();
        mapping(rootView);
        getLocalCart();
        setView();
        catchEvent();
        return rootView;
    }

    private void initRecyclerView() {
        adapter = new ListOrdersAdapter(getContext(), lsOrders, database);
        adapter.notifyDataSetChanged();
        orderRecyclerView.setAdapter(adapter);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void getLocalCart(){
        cs = database.get(String.format("SELECT * FROM cart WHERE userId='%s'", userId));
        while (cs.moveToNext()){
            int id = cs.getInt(0);
            String pId = cs.getString(2);
            String cId = cs.getString(3);
            String name = cs.getString(4);
            String path = cs.getString(5);
            int qty = cs.getInt(6);
            double _price = cs.getDouble(7);
            double _total_price = cs.getDouble(8);
            Log.d(TAG, path);
            Post product = new Post(pId, cId, path, name, _price);
            Cart cartItems = new Cart(product, qty, _total_price);
            lsOrders.add(cartItems);
            Log.d("Database cart", id+ "\n"+ pId + "\n" + cId + "\n" + name + "\n" + path + "\n" + qty + "\n" +formatter.format(_price)  + "\n" + formatter.format(_total_price));
        }
    }
    private void loadSqlite(){
        database = new MySqlite(getContext());
        cs = database.get("SELECT * FROM authorization");
        if (cs.moveToNext()) {
            token = cs.getString(0);
            userId = cs.getString(1);
            username = cs.getColumnName(2);
            phone = cs.getInt(3);
            gender = cs.getInt(4) > 0 ? true: false;
            email = cs.getString(5);
        }else{
            userId = "GUEST";
        }
        cs.close();
        database.close();
    }
    private void mapping(View v) {
        orderRecyclerView = (RecyclerView) v.findViewById(R.id.order_recyclerview);
        totalPrice = (TextView) v.findViewById(R.id.total_price);
    }
    private void catchEvent(){
        adapter.setOnItemClickListener(this);
    }
    private void setView(){
        initRecyclerView();
        String total_price = formatter.format(getTotalPrice());
        totalPrice.setText(total_price);
    }

    private double getTotalPrice(){
        double result = 0;
        for(int i = 0; i < lsOrders.size(); i++){
            result += lsOrders.get(i).getPrice();
        }
        return result;
    }
    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onDeleteItem(int position) {
        String id = lsOrders.get(position).getItem().get_id();
        database.post(String.format("DELETE FROM cart WHERE userId= '%s' AND productId='%s'",userId, id));
        lsOrders.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyDataSetChanged();

        String total_price = formatter.format(getTotalPrice());
        totalPrice.setText(total_price);
        if(lsOrders.size()<=0){
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_orders_container, OrderEmptyUI.newInstance())
                    .commit();
        }
    }
}
