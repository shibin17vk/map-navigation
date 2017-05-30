package com.example.shibin.myapplication.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shibin.myapplication.R;
import com.example.shibin.myapplication.adapters.RouteInfoRecyclerAdapter;
import com.example.shibin.myapplication.implementation.MapActivityImplementation;
import com.example.shibin.myapplication.model.getdirection.GetDirectionResponse;
import com.example.shibin.myapplication.model.getdirection.Leg;
import com.example.shibin.myapplication.model.getdirection.Route;
import com.example.shibin.myapplication.model.getdirection.Step;
import com.example.shibin.myapplication.network.ApiResponseUiData;
import com.example.shibin.myapplication.utils.AnimationUtility;
import com.example.shibin.myapplication.utils.AppMethods;
import com.example.shibin.myapplication.utils.Constants;
import com.example.shibin.myapplication.utils.OttoClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 */

public class MapActivity extends AppBaseActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        GoogleMap.OnPolylineClickListener, LocationListener {

    @BindView(R.id.tvLabelStartLocstion)
    protected TextView tvLabelStartLocstion;

    @BindView(R.id.tvLabelDestination)
    protected TextView tvLabelDestination;

    @BindView(R.id.uiPanel)
    protected AppBarLayout mAppBarLayout;

    @BindView(R.id.bottom_sheet)
    protected View bottomSheet;

    @BindView(R.id.routeinfo)
    protected RecyclerView mRoutinfoRecyler;

    @BindView(R.id.tvDistance)
    protected TextView tvDistance;

    /**
     * Collection to store the resultant Routes. Key will be route id /polygon line id and
     * Value will be List of routes
     */
    private LinkedHashMap<String, Route> mResultRoutes              =   null;
    private LinkedHashMap<String, String> mPolylines                =   null;
    private String mCSelectedRouteId                                =   null;
    private LocationManager mLocationManager                        =   null;

    private RouteInfoRecyclerAdapter mRouteInfoRecyclerAdapter      =   null;
    private BottomSheetBehavior mBottomSheetBehavi                  =   null;
    private MapActivityImplementation mMapActivityImplementation    =   null;

    private GoogleMap map                    =       null;
    private SupportMapFragment mapFragment   =       null;
    private Place mPlaceStart                =       null;
    private Place mPlaceDestination          =       null;
    private static float mapZoomFactor       =       15f;
    private Bus mBus                         =       null;
    private ProgressDialog mProgressDialog   =       null;

    private String[] permisionsRequired     =   {
            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);
        mMapActivityImplementation = new MapActivityImplementation(this);
        mProgressDialog = new ProgressDialog(MapActivity.this);
        mBus = OttoClient.getInstance().getOttoBus();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mBottomSheetBehavi = BottomSheetBehavior.from(bottomSheet);
        mRouteInfoRecyclerAdapter = new RouteInfoRecyclerAdapter(this);
        mRoutinfoRecyler.setLayoutManager(new LinearLayoutManager(this));
        mRoutinfoRecyler.setAdapter(mRouteInfoRecyclerAdapter);
        mResultRoutes = new LinkedHashMap<>();
        mPolylines = new LinkedHashMap<>();
        bottomSheet.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkForPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            int permissionCheck = checkSelfPermission(permisionsRequired[0]);
            permissionCheck = checkSelfPermission(permisionsRequired[0]);
            if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(permisionsRequired, Constants.REQ_PERMISSION_ACCESS_LOCATION);
            } else {
                enableCurrentLocation();
            }
        }

    }

    private void enableLocationListener() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,10, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
        updateViewControllerUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState, final PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void enableCurrentLocation() {
        map.setMyLocationEnabled(true);
        enableLocationListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnPolylineClickListener(this);
        checkForPermissions();
    }

    @OnClick({R.id.tvLabelStartLocstion, R.id.tvLabelDestination })
    protected void onViewCLick(View view) {
        switch (view.getId()) {

            case R.id.tvLabelDestination:
                launchPlaceSelectorWidget(Constants.ACT_REQ_GET_DEST_PLACE);
                break;

            case R.id.tvLabelStartLocstion:
                launchPlaceSelectorWidget(Constants.ACT_REQ_GET_START_PLACE);
                break;

        }
    }

    private void launchPlaceSelectorWidget(int reqCode) {
        try {
            Intent intent = null;
            intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
            startActivityForResult(intent, reqCode);
        }
        catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        }
        catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     *  method to udate the ui elements based on the user inputs
     *  handling text label changes, map marker etc
     */
    private void updateViewControllerUI() {
        if(mPlaceStart != null) {
            tvLabelStartLocstion.setText(mPlaceStart.getName());
            addMarker(mPlaceStart.getLatLng(), BitmapDescriptorFactory.HUE_BLUE);
        }
        if(mPlaceDestination != null) {
            tvLabelDestination.setText(mPlaceDestination.getName());
            addMarker(mPlaceDestination.getLatLng(),BitmapDescriptorFactory.HUE_RED);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(mPlaceDestination.getLatLng(), mapZoomFactor));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(mPlaceDestination.getLatLng(),mapZoomFactor));
        }
    }

    /**
     * webservice call to fetch the directions
     */
    private void initiateGetDirectionApiService() {
        if(mPlaceStart != null && mPlaceDestination != null) {
            mMapActivityImplementation.getDirectionsfor(mPlaceStart.getLatLng(), mPlaceDestination.getLatLng());
        }
    }

    private void addMarker(LatLng latLng, float markerIcon) {
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.defaultMarker(markerIcon));
        map.addMarker(options);
    }

    /**
     * preparing the route list from the webservice response and caching locally in map
     * @param routes
     */
    private void prepareRoutes(List<Route> routes) {
        mResultRoutes.clear();
        Collections.reverse(routes);
        for(int routeIndex = 0 ; routeIndex < routes.size(); routeIndex ++) {
            mResultRoutes.put(String.valueOf(routeIndex),routes.get(routeIndex));
            mCSelectedRouteId = String.valueOf(routeIndex);
        }
        drawRoutes(mCSelectedRouteId);
    }

    /**
     * method to list out the route over the map.
     * key will the index position of the route array from the server and will be the corresponding Routes
     *
     * @param selectedRouteId
     * the selected route will be high lighted with blue color and non selected will be with
     * gray color.
     */
    private void drawRoutes(String selectedRouteId) {
        Route selectedRoute = null;
        int selectedRoutIndex = 0;
        map.clear();
        mPolylines.clear();
        addMarker(mPlaceStart.getLatLng(), BitmapDescriptorFactory.HUE_BLUE);
        addMarker(mPlaceDestination.getLatLng(), BitmapDescriptorFactory.HUE_BLUE);
        for(int index = 0; index < mResultRoutes.size() ; index ++) {
            String routeId = (String) AppMethods.getKey(mResultRoutes, index);
            Route route = mResultRoutes.get(routeId);
            if(routeId == selectedRouteId) {
                selectedRoute = route;
                selectedRoutIndex = index;
                continue;
            }
            mPolylines.put(String.valueOf(index), drawPolygonDirectionForRoute(route, false));
        }
        mPolylines.put(String.valueOf(selectedRoutIndex), drawPolygonDirectionForRoute(selectedRoute, true));
        getRoutInfo(selectedRoute);
    }

    /**
     * Method to draw a perticular route over the map
     * @param route
     * @param isSelected
     * @return
     */
    private String drawPolygonDirectionForRoute(Route route, boolean isSelected) {
        PolylineOptions lineOption = new PolylineOptions();
        List<Leg> legs = route.getLegs();
        for(Leg leg : legs) {
            List<Step> steps = leg.getSteps();
            for(Step step : steps) {
                String polyline = step.getPolyline().getPoints();
                List<LatLng> latLongList = AppMethods.decodePoly(polyline);
                lineOption.addAll(latLongList);
            }
        }
        int directionLineColor = isSelected ? getResources().getColor(R.color.route_directio_on_selected) :
                getResources().getColor(R.color.route_directio_alternative) ;
        lineOption.color(directionLineColor);
        lineOption.width(20);
        Polyline polyline = map.addPolyline(lineOption);
        polyline.setClickable(true);
        return polyline.getId();
    }

    /**
     * method to populate the route info in bottom sheet
     * @param routes
     */
    private void getRoutInfo(Route routes) {
         List<Step> steps = new ArrayList<>();
        for(Leg leg : routes.getLegs()) {
            steps.addAll(leg.getSteps());
        }
        mRouteInfoRecyclerAdapter.setData((ArrayList<Step>) steps);
        bottomSheet.setVisibility(View.VISIBLE);
        String routeInfoHeader = mRouteInfoRecyclerAdapter.getTravelDistance() + " KM, " ;
        routeInfoHeader += mRouteInfoRecyclerAdapter.getTravelTime() + " Minutes " ;
        tvDistance.setText(routeInfoHeader);


    }

    /**
     * method to handle the GetDirections API response
     * @param apiResponseUiData
     */
    private void onGetDirection(ApiResponseUiData apiResponseUiData) {
        switch (apiResponseUiData.getStatus()) {

            case Constants.API_ON_REQ_INITIATED:
                mProgressDialog.show();
                break;
            case Constants.API_ON_REQ_COMPLETED:
                mProgressDialog.dismiss();
                break;
            case Constants.API_ON_REQ_SUCCESS:
                GetDirectionResponse getDirectionResponse = (GetDirectionResponse)apiResponseUiData.getResponseParam().get(0);
                List<Route> routes = getDirectionResponse.getRoutes();
                prepareRoutes(routes);
                break;
            case Constants.API_ON_REQ_FAILURE:
                String error = apiResponseUiData.getAppException().getExceptionMessage();
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    /**
     * otto bus method to receive the callbacks from the implementation class @class {@link MapActivityImplementation}
     * {@link ApiResponseUiData}
     * having api id, api request status (onSend/ onComplete/ onSuccess/ onFailure), web service response and
     * exception (if there)
     *
     * @param apiResponseUiData
     */
    @Subscribe
    public void onApiStatusUpdate(ApiResponseUiData apiResponseUiData) {
        switch (apiResponseUiData.getApiId()) {
            case Constants.API_GOOGLE_GET_DIRECTIONS:
                onGetDirection(apiResponseUiData);
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == Constants.ACT_REQ_GET_DEST_PLACE) {
            mPlaceDestination = PlaceAutocomplete.getPlace(this, data);
        }
        else if(resultCode == RESULT_OK && requestCode == Constants.ACT_REQ_GET_START_PLACE) {
            mPlaceStart = PlaceAutocomplete.getPlace(this, data);
        }
        updateViewControllerUI();
        initiateGetDirectionApiService();
    }

    @Override
    public void onMapClick(final LatLng latLng) {
        int visibilty = mAppBarLayout.getVisibility();
        if(visibilty == View.VISIBLE) {
            AnimationUtility.collapseViewPanel(mAppBarLayout);
        } else if(visibilty == View.GONE) {
            AnimationUtility.expandViewPanel(mAppBarLayout);
        }
    }

    @Override
    public void onPolylineClick(final Polyline polyline) {
        Iterator it = mPolylines.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(pair.getValue().equals(polyline.getId())) {
                drawRoutes((String) pair.getKey());
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.REQ_PERMISSION_ACCESS_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableCurrentLocation();
                }
                break;
        }
    }

    /**
     * navigating to the current location as default location
     * @param location
     */
    @Override
    public void onLocationChanged(final Location location) {
        LatLng latLong = new LatLng(location.getLatitude(), location.getLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, mapZoomFactor));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong,mapZoomFactor));
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(final String s, final int i, final Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(final String s) {

    }

    @Override
    public void onProviderDisabled(final String s) {

    }
}
