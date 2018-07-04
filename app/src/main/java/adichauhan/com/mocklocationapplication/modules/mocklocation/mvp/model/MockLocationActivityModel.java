package adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.model;

import android.annotation.SuppressLint;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import adichauhan.com.entities.DirectionsResponse;
import adichauhan.com.entities.Leg;
import adichauhan.com.entities.Optional;
import adichauhan.com.entities.Step;
import adichauhan.com.mocklocationapplication.app.service.GoogleService;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.MockLocationActivityViewState;
import adichauhan.com.reactivecomponents.provider.FusedLocationObservableProvider;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by adityachauhan on 10/8/17.
 *
 */

public class MockLocationActivityModel  {

    private final GoogleService googleService;
    private final FusedLocationProviderClient locationProviderClient;
    private final FragmentActivity activity;

    @Inject
    public MockLocationActivityModel(FragmentActivity activity,
                              GoogleService googleService, FusedLocationProviderClient locationProviderClient) {
        this.googleService = googleService;
        this.activity = activity;
        this.locationProviderClient = locationProviderClient;
    }

    public Observable<Optional<Location>> observeLocationUpdates() {
        return FusedLocationObservableProvider
                .getUserLocationUpdatesObservable(activity,
                        locationProviderClient)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<DirectionsResponse>> observeDistanceCall(Place source, Place destination) {
        return googleService
                .getDirections(String.valueOf(source.getLatLng().latitude)
                                +","+String.valueOf(source.getLatLng().longitude),
                        String.valueOf(destination.getLatLng().latitude)
                                +","+String.valueOf(destination.getLatLng().longitude))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressLint("MissingPermission")
    public Observable<LatLng> observeMockLocation(final MockLocationActivityViewState state) {
        final DirectionsResponse directionsResponse = state.getDirectionsResponse().get();
        final int distanceStep = state.getDistanceMockInMeters();
        final double speedStep = state.getSpeedInXTimes();
        if(directionsResponse.getRoutes() != null
                && !directionsResponse.getRoutes().isEmpty()
                && directionsResponse.getRoutes().get(0).getLegs() != null
                && !directionsResponse.getRoutes().get(0).getLegs().isEmpty()) {
            locationProviderClient.setMockMode(true);
            return Observable.create(new ObservableOnSubscribe<LatLng>() {
                @SuppressLint("MissingPermission")
                @Override
                public void subscribe(ObservableEmitter<LatLng> emitter) throws Exception {
                    Leg leg = directionsResponse.getRoutes().get(0).getLegs().get(0);
                    List<LatLng> latLngsToMock;
                    latLngsToMock = getLatLngsToMock(leg.getSteps(),distanceStep);
                    for(LatLng latLng : latLngsToMock) {
                        Log.d("latlngs",""+latLng.longitude+","+latLng.latitude);
                        Integer speed = leg.getDistance().getValue()/leg.getDuration().getValue();
                        Double timeSleep = (speed*1000*distanceStep)/speedStep;
                        Log.d("sleeptime",String.valueOf(timeSleep));
                        Thread.sleep(Math.round(timeSleep));
                        emitter.onNext(latLng);
                    }
                    emitter.onComplete();
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return null;
    }

    private List<LatLng> getLatLngsToMock(List<Step> steps, Integer distanceStep) {
        List<LatLng> latLngs = new ArrayList<>();
        LatLng previousLatLng = null;
        for(Step step : steps) {
            for(LatLng latLng : PolyUtil.decode(step.getPolyline().getPoints())) {
                if(previousLatLng == null) {
                    previousLatLng = latLng;
                    continue;
                }
                Double distanceBetween = SphericalUtil.computeDistanceBetween(previousLatLng,latLng);
                if(distanceBetween > (distanceStep + 100 /*buffer*/)) {
                    double repeatition = (distanceBetween/distanceStep)%distanceStep;
                    for(int i = 0; i <= repeatition ; i++) {
                        double newLat = previousLatLng.latitude +
                                (latLng.latitude - previousLatLng.latitude) * (distanceStep / distanceBetween);
                        double newLng = previousLatLng.longitude +
                                (latLng.longitude - previousLatLng.longitude)* (distanceStep / distanceBetween);
                        LatLng newLatLng = new LatLng(newLat, newLng);
                        latLngs.add(newLatLng);
                        previousLatLng = newLatLng;
                    }
                } else if(distanceBetween>=distanceStep) {
                    latLngs.add(latLng);
                    previousLatLng = latLng;
                }
            }
        }
        return latLngs;
    }
}