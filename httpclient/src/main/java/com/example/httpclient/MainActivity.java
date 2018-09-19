package com.example.httpclient;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String TAG = this.getClass().getSimpleName();

    private HttpServeAPI httpServeApi;

    private EditText editId, editName, editGetId;

    private SeekBar seekId;

    private Button buttonGet, buttonPost;

    List<Item> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Creating api ...");
        createAPI();

        editId = (EditText) findViewById(R.id.edit_id);
        editName = (EditText) findViewById(R.id.edit_name);
        editGetId = (EditText) findViewById(R.id.edit_get_id);

        buttonGet = (Button) findViewById(R.id.button_get);
        buttonGet.setOnClickListener(buttonGetOnClickListener);

        buttonPost = (Button) findViewById(R.id.button_post);
        buttonPost.setOnClickListener(buttonPostOnClickListener);

        seekId = (SeekBar) findViewById(R.id.seek_id);
        seekId.setEnabled(false);
        seekId.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (itemList.size() > 0) {
                    editGetId.setText((Integer.toString(progress)));
                    editId.setText(Integer.toString(itemList.get(progress).id));
                    editName.setText(itemList.get(progress).name);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Nothing to do here
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Nothing to do here
            }
        });
    }

    View.OnClickListener buttonGetOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (editGetId.getText().toString().isEmpty()) {
                httpServeApi.getItem().enqueue(new Callback<List<Item>>() {
                    @Override
                    public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {

                        if (response.isSuccessful()) {
                            itemList = response.body();
                            for (Item item : itemList) {
                                Log.d(TAG, "ITEM: " + item.id + " - " + item.name);
                            }

                            seekId.setMax(itemList.size() - 1);
                            seekId.setEnabled(true);
                        } else {
                            Log.w(TAG, "Failure: " + response.code() + " Message: " + response.message());
                            showToast("GET items fail! " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Item>> call, Throwable t) {
                        clearItemList();
                        Log.e(TAG, t.getCause() + " >> " + t.getMessage());
                        showToast("GET items fail! " + t.getMessage());
                    }
                });
            } else {
                try {
                    int id = Integer.parseInt(editGetId.getText().toString());
                    httpServeApi.getItem(id).enqueue(new Callback<Item>() {
                        @Override
                        public void onResponse(Call<Item> call, Response<Item> response) {
                            if (response.isSuccessful()) {
                                clearItemList();

                                Item item = response.body();
                                editId.setText(Integer.toString(item.id));
                                editName.setText(item.name);
                                Log.d(TAG, "ITEM: " + item.id + " - " + item.name);
                            } else {
                                Log.w(TAG, "Failure: " + response.code() + " Message: " + response.message());
                                showToast("Get Item with id not found! " + response.message());
                                clearItemList();
                            }
                        }

                        @Override
                        public void onFailure(Call<Item> call, Throwable t) {
                            Log.e(TAG, t.getCause() + " >> " + t.getMessage());
                            showToast("Get Item with id not found! " + t.getMessage());
                            clearItemList();
                        }
                    });
                } catch (NumberFormatException nex) {
                    httpServeApi.findItem("haris", "1").enqueue(new Callback<List<Item>>() {
                        @Override
                        public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                            if (response.isSuccessful()) {
                                itemList = response.body();
                                for (Item item : itemList) {
                                    Log.d(TAG, "ITEM FOUND: " + item.id + " - " + item.name);
                                }

                                seekId.setMax(itemList.size());
                                seekId.setEnabled(true);
                            } else {
                                Log.w(TAG, "Failure: " + response.code() + " Message: " + response.message());
                                showToast("FIND item fail! " + response.message());
                                clearItemList();
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Item>> call, Throwable t) {
                            Log.e(TAG, t.getCause() + " >> " + t.getMessage());
                            showToast("FIND item fail! " + t.getMessage());
                            clearItemList();
                        }
                    });
                }
            }
        }
    };
    View.OnClickListener buttonPostOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            int id = -1;
            try {
                id = Integer.parseInt(editId.getText().toString());
            } catch (NumberFormatException nex) {
                showToast("ID is not specified!");
                return;
            }

            String name = "";
            if ((name = editName.getText().toString()).isEmpty()) {
                showToast("NAME is not specified!");
                return;
            }

            httpServeApi.postItem(new Item(id, name)).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "Post item successful!");
                        showToast("POST item success!");
                    } else {
                        Log.w(TAG, "Failure: " + response.code() + " Message: " + response.message());
                        showToast("POST item fail! " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e(TAG, t.getCause() + " >> " + t.getMessage());
                    showToast("POST item fail! " + t.getMessage());
                    clearItemList();
                }
            });
        }
    };

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    private void clearItemList() {
        editId.setText("");
        editName.setText("");
        itemList.clear();
        seekId.setEnabled(false);
        seekId.setProgress(0);
    }

    private void createAPI() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").setLenient()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpServeAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        httpServeApi = retrofit.create(HttpServeAPI.class);
    }

}
