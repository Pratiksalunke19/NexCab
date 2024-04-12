package com.example.nexcab;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.nexcab.databinding.FragmentPickupLocationBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.List;
import java.util.Objects;

public class PickupLocation extends Fragment implements OnMapReadyCallback {
    FragmentPickupLocationBinding binding;
    Bundle bundle;

    //map
    private GoogleMap mMap;

    SearchView mapSearchView;
    SupportMapFragment supportMapFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPickupLocationBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        androidx.appcompat.widget.SearchView searchView = binding.searchViewPickupLocation;
        binding.searchViewPickupLocation.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = binding.searchViewPickupLocation.getQuery().toString();
                List<Address> addressList = null;

                if(location != null){
                    Geocoder geocoder = new Geocoder(requireContext());
                    try{
                        addressList = geocoder.getFromLocationName(location,1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                assert addressList != null;
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,10));

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!binding.searchViewPickupLocation.getQuery().toString().equals("")){
                    sendPickupLocation();
                }else{
                    Toast.makeText(getContext(), "Please select location!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        supportMapFragment.getMapAsync(this);

    }

    public void sendPickupLocation(){
        // create bundle to pass object to next fragment
        bundle = getArguments();
        String parentFragment = "";

        // get ParentFragment and set it according to instant ride or preebook
        if(bundle == null) {
            bundle = new Bundle();
            bundle.putString("ParentFragment","PickupLocation");
        }
        else{
            // intent came from PickDateTimeFragment
            parentFragment = bundle.getString("ParentFragment");
            bundle.putString("ParentFragment","PickupLocationPreebook");
        }
        //set the bundle values
        bundle.putString("pickupLocation", binding.searchViewPickupLocation.getQuery().toString());

        Fragment fragment = new DropoffLocation();
        // pass data to next fragment
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if(Objects.equals(parentFragment, "")) {
            Log.d("Parent Fragment", "Parent Fragment is null ");
            fragmentTransaction.replace(R.id.location_frameLayout_id, fragment);
        }
        else
            fragmentTransaction.replace(R.id.prebook_container,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        Log.d("onclick", "Fragment Succesfully replaced to DropoffLocation");
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
    }
}