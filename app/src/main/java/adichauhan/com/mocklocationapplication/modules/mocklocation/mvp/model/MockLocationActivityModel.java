package adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.model;

import android.annotation.SuppressLint;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import adichauhan.com.entities.DirectionsResponse;
import adichauhan.com.entities.Leg;
import adichauhan.com.entities.Optional;
import adichauhan.com.entities.Route;
import adichauhan.com.entities.Step;
import adichauhan.com.mocklocationapplication.app.service.GoogleService;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.MockLocationActivityViewState;
import adichauhan.com.reactivecomponents.provider.FusedLocationObservableProvider;
import adichauhan.com.reactivecomponents.provider.LocationSearchObservableProvider;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
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
    private final GeoDataClient geoDataClient;

    @Inject
    public MockLocationActivityModel(FragmentActivity activity,
                              GoogleService googleService, FusedLocationProviderClient locationProviderClient,
                              GeoDataClient geoDataClient) {
        this.geoDataClient = geoDataClient;
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

    public Observable<Response<DirectionsResponse>> observeDistanceCall() {
        return googleService
                .getDirections("12.983507, 77.596822",
                        "13.041849, 77.617731")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @SuppressLint("MissingPermission")
    public Observable<Location> observeMockLocation(final MockLocationActivityViewState state) {
        final DirectionsResponse directionsResponse = state.getDirectionsResponse().get();
        final int distaceStep = state.getDistanceMockInMeters();
        final double speedStep = state.getSpeedInXTimes();
        if(directionsResponse.getRoutes() != null
                && !directionsResponse.getRoutes().isEmpty()
                && directionsResponse.getRoutes().get(0).getLegs() != null
                && !directionsResponse.getRoutes().get(0).getLegs().isEmpty()) {
            locationProviderClient.setMockMode(true);
            return Observable.create(new ObservableOnSubscribe<Location>() {
                @SuppressLint("MissingPermission")
                @Override
                public void subscribe(ObservableEmitter<Location> emitter) throws Exception {
                    Leg leg = directionsResponse.getRoutes().get(0).getLegs().get(0);
                    List<LatLng> latLngsToMock = new ArrayList<>();
                    double distanceCovered = 0;
                    int index = 0;
                    LatLng prevoiusLatLng = new LatLng(leg.getStartLocation().getLat(),leg.getStartLocation().getLng());
                    int previousLatLngIndex = 0;
                    double distanceRemainingInCurrentStep = 0;
                    //Log.d("distancestep",String.valueOf(distaceStep));
                    //Log.d("speedstep",String.valueOf(speedStep));
                    while(distanceCovered
                            < leg.getDistance().getValue() && index < leg.getSteps().size()) {
                        //Log.d("distancecovered",String.valueOf(distanceCovered));
                        Log.d("index",String.valueOf(index));
                        Step currentStep = leg.getSteps().get(index);
                        double totalDistanceBetween = SphericalUtil.computeDistanceBetween(prevoiusLatLng,
                                new LatLng(currentStep.getEndLocation().getLat(),
                                        currentStep.getEndLocation().getLng()));
                        if(totalDistanceBetween>(distaceStep<distanceRemainingInCurrentStep
                                ?distaceStep:distaceStep-distanceRemainingInCurrentStep)) {
                            List<LatLng> currentStepLatLngs = PolyUtil.decode(currentStep.getPolyline().getPoints());
                            LatLng mockLatLng = null;
                            int indexPrevious = currentStepLatLngs.indexOf(prevoiusLatLng);
                            if(indexPrevious < 0) {
                                indexPrevious = 0;
                            }
                            while(indexPrevious < currentStepLatLngs.size()) {
                                LatLng latLng = currentStepLatLngs.get(previousLatLngIndex);
                                double distance = SphericalUtil.computeDistanceBetween(prevoiusLatLng, latLng);
                                Log.d("distance", ""+distance);
                                if (SphericalUtil.computeDistanceBetween(prevoiusLatLng, latLng)
                                        > (distaceStep<distanceRemainingInCurrentStep
                                        ? distaceStep:distaceStep-distanceRemainingInCurrentStep)) {
                                    mockLatLng = latLng;
                                    break;
                                }
                                previousLatLngIndex++;
                            }
                            if(mockLatLng == null) {
                                continue;
                            }
                            latLngsToMock.add(mockLatLng);
                            distanceRemainingInCurrentStep = SphericalUtil
                                    .computeDistanceBetween(mockLatLng, new LatLng(currentStep.getEndLocation().getLat(),
                                            currentStep.getEndLocation().getLng()));
                            prevoiusLatLng = mockLatLng;
                            previousLatLngIndex = index;
                            distanceCovered = distanceCovered + ;
                            //Log.d("distanceRemainingInCurr",String.valueOf(distanceRemainingInCurrentStep));
                            Log.d("newlatlng",mockLatLng.latitude+" "+mockLatLng.longitude);
                        } else {
                            index++;
                        }
                    }
                    /*for(LatLng latLng : latLngsToMock) {

                    }*/
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
        return null;
    }

    public void mockLocation(Location mockLocation) {
        @SuppressLint("MissingPermission")
        Task<Void> task = locationProviderClient.setMockLocation(mockLocation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("asdasda","asddasdasdas");
                    }
                });
    }
}