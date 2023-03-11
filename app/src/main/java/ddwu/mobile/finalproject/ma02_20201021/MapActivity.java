package ddwu.mobile.finalproject.ma02_20201021;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.List;
import java.util.Locale;

public class MapActivity extends AppCompatActivity {
    public static final String TAG = "20201021_ma";

    private GoogleMap mGoogleMap;
    FusedLocationProviderClient flpClient;
    Location mLastLocation;
    private Marker mCenterMarker;
    Marker marker;

    final int REQ_PERMISSION_CODE = 500;

    EditText etSearchWord;
    String address;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        checkPermission();

        flpClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback);

        startLocation();
        getLastLocation();

        etSearchWord = findViewById(R.id.etSearchWord);

        address = getIntent().getStringExtra("address");
        if (address != null && !address.equals("")) {
            executeGeocoding(address);
        }
    }

    public void startLocation() {
        flpClient.requestLocationUpdates(
                getLocationRequest(),
                locCallback,
                Looper.getMainLooper()
        );
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


            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    startLocation();
                    return false;
                }
            });
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
        switch (v.getId()) {
            case R.id.btn:
                String searchWord = etSearchWord.getText().toString();
                executeGeocoding(searchWord);

                break;
        }
    }

    private void executeGeocoding(String searchWord) {
        if (Geocoder.isPresent()) {
            new GeoTask().execute(searchWord);
        } else {
            Log.d(TAG, "No Geocoder");
        }
    }

    class GeoTask extends AsyncTask<String, Void, List<Address>> {
        Geocoder geocoder = new Geocoder(MapActivity.this, Locale.getDefault());

        @Override
        protected List<Address> doInBackground(String... searchWord) {
            List<Address> addresses = null;
            Log.d(TAG, searchWord[0]);
            try {
                addresses = geocoder.getFromLocationName(searchWord[0], 1);
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
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

                marker = mGoogleMap.addMarker(makerOptions);

                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

            } else {
                Toast.makeText(MapActivity.this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
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
