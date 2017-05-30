package com.example.shibin.myapplication.implementation;

import android.content.Context;

import com.example.shibin.myapplication.model.getdirection.GetDirectionResponse;
import com.example.shibin.myapplication.network.ApiManager;
import com.example.shibin.myapplication.network.ApiResponseUiData;
import com.example.shibin.myapplication.network.ApiService;
import com.example.shibin.myapplication.network.RetrofitResponseHandler;
import com.example.shibin.myapplication.utils.Constants;
import com.example.shibin.myapplication.utils.OttoClient;
import com.squareup.otto.Bus;
import com.google.android.gms.maps.model.LatLng;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author shibin
 * @version 1.0
 * @date 29/05/17
 *
 * Implementation class for MapActivity.
 * All the web services implementation and the business logic of the
 * MapActivty can be written over here.
 * Callbacks to the activity is achived using otto bus subscription mechanism
 *
 * This architecture will gives the flexibility to couple and decouple the business logic layer and ui layer
 * with minimal effort and complexity
 *
 */

public class MapActivityImplementation {

    private Bus mBus;
    private Context mContext;

    public MapActivityImplementation(Context context) {
        this.mContext = context;
    }

    public void getDirectionsfor(LatLng startLocation, LatLng destinLocation) {
        mBus = OttoClient.getInstance().getOttoBus();
        mBus.post(new ApiResponseUiData(Constants.API_GOOGLE_GET_DIRECTIONS, Constants.API_ON_REQ_INITIATED));
        ApiService apiService = ApiManager.getApiManager(mContext).getApiService();

        String orign = startLocation.latitude + "," + startLocation.longitude;
        String destination = destinLocation.latitude + "," + destinLocation.longitude;
        String sensor = "false";
        String alternatives = "true";

        apiService.getDirections(orign, destination, sensor, alternatives)
             .subscribeOn(Schedulers.newThread())
             .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GetDirectionResponse>() {
                    @Override
                    public void onCompleted() {
                        mBus.post(new ApiResponseUiData(Constants.API_GOOGLE_GET_DIRECTIONS, Constants.API_ON_REQ_COMPLETED));
                    }

                    @Override
                    public void onError(final Throwable e) {
                        ApiResponseUiData apiResponseUiData = new ApiResponseUiData(Constants
                                .API_GOOGLE_GET_DIRECTIONS, Constants.API_ON_REQ_FAILURE);
                        apiResponseUiData.addException(RetrofitResponseHandler.onRetrofitResponseFailue(e));
                        mBus.post(apiResponseUiData);
                    }

                    @Override
                    public void onNext(final GetDirectionResponse getDirectionResponse) {
                        ApiResponseUiData apiResponseUiData = new ApiResponseUiData(Constants.API_GOOGLE_GET_DIRECTIONS,
                                Constants.API_ON_REQ_SUCCESS);
                        apiResponseUiData.addResponseParam(getDirectionResponse);
                        mBus.post(apiResponseUiData);
                    }
                });
    }

}
