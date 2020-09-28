package sict.apps.studentmarket.ui.notification;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import sict.apps.studentmarket.R;
import sict.apps.studentmarket.adapters.ListNotificationAdapter;
import sict.apps.studentmarket.apihelper.ApiConnect;
import sict.apps.studentmarket.models.Content;
import sict.apps.studentmarket.models.Notification;
import sict.apps.studentmarket.models.User;
import sict.apps.studentmarket.services.APIService;
import sict.apps.studentmarket.vm.MainShareViewModel;

public class NotificationUI extends Fragment {
    private static final String TAG = Notification.class.getSimpleName();
    private RecyclerView recyclerView;
    private ListNotificationAdapter adapter;
    private List<Content> lsNotification = new ArrayList<>();
    private User user;
    private MainShareViewModel viewModel;
    public static NotificationUI newInstance() {

        return  new NotificationUI();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notification_layout, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapping(view);
        initRecyclerView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(MainShareViewModel.class);
        viewModel.getObjUser().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.d(TAG, "onChanged: "+s);
                Gson gson = new Gson();
                Type type = new TypeToken<User>(){}.getType();
                user = gson.fromJson(s, type);
                if(user==null){
                    user = new User("", "GUEST", "", 0, true, "");
                }
                ApiLoad apiListener = new ApiLoad();
                apiListener.getNotifications(user.getToken(), user.get_id());
            }
        });
    }

    public void mapping(View v){
        recyclerView = (RecyclerView) v.findViewById(R.id.notification_recyclerview);
    }
    public void initRecyclerView(){
        adapter = new ListNotificationAdapter(getContext(), lsNotification);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    class ApiLoad extends ApiConnect {
        private final String TAG = ApiLoad.class.getSimpleName();
        private Retrofit retrofit = getRetrofit();
        private APIService apiService = retrofit.create(APIService.class);

        public void getNotifications(String token, String userId){
            Call<List<Notification>> call = apiService.getNotification("Bearer "+token, userId);
            call.enqueue(new Callback<List<Notification>>() {
                @Override
                public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                    if(response.isSuccessful()){
                        if(response.body().size()>0) {
                            for (int i = 0; i < response.body().get(0).getContents().size(); i++) {
                                String content = response.body().get(0).getContents().get(i).getContent();
                                Content ct = new Content(content);
                                lsNotification.add(0, ct);
                            }
                            adapter.notifyDataSetChanged();
                        }
                        return;
                    }
                    call.cancel();
                }

                @Override
                public void onFailure(Call<List<Notification>> call, Throwable t) {

                }
            });
        }

    }
}
