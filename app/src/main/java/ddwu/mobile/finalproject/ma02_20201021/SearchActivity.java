package ddwu.mobile.finalproject.ma02_20201021;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import ddwu.mobile.finalproject.ma02_20201021.model.json.NaverSearchDto;
import ddwu.mobile.finalproject.ma02_20201021.model.json.SearchRoot;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity
        implements SearchAdapter.ListBtnClickListener, SearchAdapter.BlogBtnClickListener {
    public static final String TAG = "20201021_ma";

    private Retrofit retrofit;
    private INaverAPIService naverAPIService;

    private GoogleMap mGoogleMap;
    FusedLocationProviderClient flpClient;
    Location mLastLocation;
    private Marker mCenterMarker;
    Marker marker;

    EditText etQuery;
    ListView lvList;

    String apiUrl;
    String clientId;
    String clientSecret;
    String query;

    ArrayList<Search> dataList;
    SearchAdapter adapter;
    ArrayList<NaverSearchDto> resultList;

    final int ADD_CODE = 100;
    final int REQ_PERMISSION_CODE = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        checkPermission();

        apiUrl = getResources().getString(R.string.api_url);
        clientId = getResources().getString(R.string.client_id);
        clientSecret = getResources().getString(R.string.client_secret);

        etQuery = findViewById(R.id.etQuery);
        lvList = findViewById(R.id.lvList);

        if (retrofit == null) {
            try {
                retrofit = new Retrofit.Builder()
                        .baseUrl(apiUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        naverAPIService = retrofit.create(INaverAPIService.class);

        resultList = new ArrayList();
        dataList = new ArrayList<Search>();
        adapter = new SearchAdapter(this, R.layout.search_adapter_view, dataList, this, this);
        lvList.setAdapter(adapter);

        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Search data = dataList.get(i);

                Intent intent = new Intent(SearchActivity.this, WriteActivity.class);
                intent.putExtra("title", data.getTitle());
                intent.putExtra("address", data.getAddress());
                startActivityForResult(intent, ADD_CODE);
            }
        });

        flpClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        flpClient.requestLocationUpdates(
                getLocationRequest(),
                locCallback,
                Looper.getMainLooper()
        );
        getLastLocation();
    }

    OnMapReadyCallback mapReadyCallback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            mGoogleMap = googleMap;

            LatLng latLng = new LatLng(37.606320, 127.041808);
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title("현재 위치")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            mCenterMarker = mGoogleMap.addMarker(markerOptions);
        }
    };

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    LocationCallback locCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            for (Location loc : locationResult.getLocations()) {
                mLastLocation = loc;
                LatLng currentLoc = new LatLng(loc.getLatitude(), loc.getLongitude());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 17));
                mCenterMarker.setPosition(currentLoc);
            }
        }
    };

    public void onClick (View v) {
        switch(v.getId()) {
            case R.id.searchButton:
                query = etQuery.getText().toString();
                if (!query.contains("동물병원")) {
                    query += "동물병원";
                }
                Call<SearchRoot> apiCall =
                        naverAPIService.getSearchList(clientId, clientSecret, 5, query);
                apiCall.enqueue(apiCallback);
                break;
        }
    }

    @Override
    public void onListBtnClick(String title, String address) {
        Search search = new Search(0, title, address);
        executeGeocoding(search);
    }

    @Override
    public void onBlogBtnClick(String title, String address) {
        Search search = new Search(0, title, address);
        Intent intent = new Intent(SearchActivity.this, BlogActivity.class);
        intent.putExtra("search", search);
        startActivity(intent);
    }

    private void executeGeocoding(Search search) {
        if (Geocoder.isPresent()) {
            new GeoTask().execute(search);
        } else {
            Log.d(TAG, "No Geocoder");
        }
    }

    class GeoTask extends AsyncTask<Search, Void, List<Address>> {
        Geocoder geocoder = new Geocoder(SearchActivity.this, Locale.getDefault());
        Search search;
        @Override
        protected List<Address> doInBackground(Search... searches) {
            List<Address> addresses = null;
            try {
                search = searches[0];
                addresses = geocoder.getFromLocationName(search.getAddress(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            if (addresses != null && addresses.size() > 0) {
                flpClient.removeLocationUpdates(locCallback);

                Address address = addresses.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                MarkerOptions makerOptions = new MarkerOptions()
                        .title(search.getTitle())
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                marker = mGoogleMap.addMarker(makerOptions);

                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            }
        }
    }

    Callback<SearchRoot> apiCallback = new Callback<SearchRoot>() {
        @Override
        public void onResponse(Call<SearchRoot> call, Response<SearchRoot> response) {
            SearchRoot searchRoot = response.body();
            List<NaverSearchDto> list = searchRoot.getItems();

            dataList.clear();
            int count = 1;
            for (NaverSearchDto l : list) {
                Log.d(TAG, l.toString());
                HashMap<String, String> item = new HashMap<String,String>();

                // 검색 시 검색어가 강조되는 <b>, </b> 제거
                String title = l.getTitle().replace("<b>", "");
                title = title.replace("</b>", "");

                Search searchData = new Search(count, title, l.getRoadAddress());
                dataList.add(searchData);
                count++;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onFailure(Call<SearchRoot> call, Throwable t) {
            Log.e(TAG, t.toString());
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    flpClient.removeLocationUpdates(locCallback);
                    finish();
                    break;
                case RESULT_CANCELED:
                    Log.d(TAG, "취소");
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "위치권한 획득 성공");
                } else {
                    Log.d(TAG, "위치권한 획득 실패");
                }
        }
    }


    private void checkPermission() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            // 권한이 있을 경우 수행할 동작
            Log.d(TAG, "Permissions Granted");
        } else {
            // 권한 요청
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQ_PERMISSION_CODE);
        }
    }

    private void getLastLocation() {

        flpClient.getLastLocation().addOnSuccessListener(
                new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            mLastLocation = location;
                        }
                    }
                });

        flpClient.getLastLocation().addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                }
        );

    }

}
