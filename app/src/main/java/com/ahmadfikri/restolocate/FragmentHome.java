package com.ahmadfikri.restolocate;

/*
NIM     : 10119106
Nama    : Ahmad Fikri Maulana
Kelas   : IF-1/S1/VI
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    // Inisialisasi Variabel
    FusedLocationProviderClient client;
    private GoogleMap mMap;

    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();

    //List Tempat
    LatLng sansco = new LatLng(-6.8744103335508635, 107.61857837636722);
    LatLng bagikopi = new LatLng(-6.877573879471015, 107.61681348281154);
    LatLng jurnal = new LatLng(-6.885779994355899, 107.61282205578887);
    LatLng spg = new LatLng(-6.886541572447659, 107.61503219602389);
    LatLng fourcs = new LatLng(-6.888086027741096, 107.61524140833215);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Inisialisasi View
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Inisialisasi Map Fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        //Inisialisasi Lokasi Client
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        //List Array diambil dari list tempat
        arrayList.add(sansco);
        arrayList.add(bagikopi);
        arrayList.add(jurnal);
        arrayList.add(spg);
        arrayList.add(fourcs);

        //Sinkronisasi Map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                //Dimana jika map berhasil dijalankan, dan menampilkan title
                mMap = googleMap;
                mMap.addMarker(new MarkerOptions().position(sansco).title("Sans Co."));
                mMap.addMarker(new MarkerOptions().position(bagikopi).title("Bagi Kopi"));
                mMap.addMarker(new MarkerOptions().position(jurnal).title("Jurnal Risa Coffee Dago"));
                mMap.addMarker(new MarkerOptions().position(spg).title("Warung Nasi SPG"));
                mMap.addMarker(new MarkerOptions().position(fourcs).title("Four C'S Cafe Eatery"));
                for (int i=0;i<arrayList.size();i++){
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(arrayList.get(i)));
                }
            }
        });

        //Cek Kondisi Permission
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //Jikas Permission Sukses, maka memanggil method
            getCurrentLocation();
        }
        else {
            //Jika Permission gagal, maka memanggil method
            requestPermissions(new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 100);
        }

        //Return View
        return view;
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation()
    {
        //Inisialisasi mapFragment
        SupportMapFragment mapFragment=(SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //Inisialisasi locationManager
        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        //Cek Kondisi Location Manager
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            client.getLastLocation().addOnCompleteListener(
                    task -> {

                        //Inisialisasi Lokasi
                        Location location = task.getResult();

                        //Cek Kondisi Lokasi
                        if (location != null) {

                            //Ketika hasil lokasi tidak null maka set latitude dan longitud
                            mapFragment.getMapAsync(googleMap -> {
                                LatLng lokasi = new LatLng(location.getLatitude(),location.getLongitude());
                                MarkerOptions options = new MarkerOptions().position(lokasi).title("Lokasi Anda");
                                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi,17));
                                googleMap.addMarker(options);
                            });
                        }
                        else {
                            LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10000).setFastestInterval(1000).setNumUpdates(1);
                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void
                                onLocationResult(@NonNull LocationResult locationResult)
                                {
                                    mapFragment.getMapAsync(googleMap -> {
                                        LatLng lokasi = new LatLng(location.getLatitude(),location.getLongitude());
                                        MarkerOptions options = new MarkerOptions().position(lokasi).title("Lokasi Sekarang");
                                        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lokasi,17));
                                        googleMap.addMarker(options);
                                    });
                                }
                            };

                            //Update Lokasi
                            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    });
        }
        else {
            startActivity(
                    new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}