package sict.apps.studentmarket.apihelper;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import sict.apps.studentmarket.ultil.Server;

public abstract class ApiConnect {
    private Retrofit retrofit = null;

    protected Retrofit getRetrofit() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.connectTimeout(30, TimeUnit.SECONDS);
        client.readTimeout(30, TimeUnit.SECONDS);
        client.writeTimeout(15, TimeUnit.SECONDS);
        if (retrofit == null)
            return new Retrofit.Builder()
                    .client(client.build())
                    .baseUrl(Server.API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        else return retrofit;
    }
}
