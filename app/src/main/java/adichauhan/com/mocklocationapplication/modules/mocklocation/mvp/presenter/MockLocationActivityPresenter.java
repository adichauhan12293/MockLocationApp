package adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import adichauhan.com.entities.DirectionsResponse;
import adichauhan.com.entities.Optional;
import adichauhan.com.mocklocationapplication.app.utils.PermissionUtils;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.model.MockLocationActivityModel;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.MockLocationActivityView;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.MockLocationActivityViewState;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.ApiState;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.UserLocationState;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by adityachauhan
 */

public class MockLocationActivityPresenter {

    private final MockLocationActivityModel model;
    private final MockLocationActivityView view;
    private final AppCompatActivity activity;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final CompositeDisposable nearbyCompositeDisposables = new CompositeDisposable();
    private Optional<Disposable> previousNearbyApiCallDisposable = Optional.empty();
    private Optional<Disposable> userLocationUpdatesDisposable = Optional.empty();

    private Optional<Disposable> mockLocationDisposable = Optional.empty();

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int SOURCE_PLACE_AUTOCOMPLETE_REQUEST_CODE = 2;
    public static final int DESTINATION_PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;

    @Inject
    public MockLocationActivityPresenter(AppCompatActivity activity,
                                  MockLocationActivityView view,
                                  MockLocationActivityModel model) {
        this.view = view;
        this.model = model;
        this.activity = activity;
    }

    public void onCreate() {
        compositeDisposable.add(observeMapReadyState());
        compositeDisposable.add(observeMockButtonClick());
        compositeDisposable.add(observeEdtDistanceChanges());
        compositeDisposable.add(observeEdtSpeedChanges());
        compositeDisposable.add(observeSourceClick());
        compositeDisposable.add(observeDestinationClick());
    }

    private Disposable observeMapReadyState() {
        return view.observeMapReadyState().subscribe(new Consumer<GoogleMap>() {
            @SuppressLint("MissingPermission")
            @Override
            public void accept(GoogleMap googleMap) {
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                MockLocationActivityViewState state = view.getCurrentState();
                state.setGoogleMap(Optional.of(googleMap));
                view.renderView();
                if (PermissionUtils.checkPermisssion(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    googleMap.setMyLocationEnabled(true);
                    userLocationUpdatesDisposable = Optional.of(observeUserLocation());
                } else {
                    PermissionUtils.getPermissions(activity,LOCATION_PERMISSION_REQUEST_CODE
                            , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION});
                }
            }
        });
    }

    public void onStart() {
        if (view.getCurrentState().getGoogleMap().isPresent()) {
            if (PermissionUtils.checkPermisssion(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                userLocationUpdatesDisposable = Optional.of(observeUserLocation());
            } else {
                PermissionUtils.getPermissions(activity,LOCATION_PERMISSION_REQUEST_CODE
                        , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION});
            }
        }
    }

    public void onStop() {
        if(userLocationUpdatesDisposable.isPresent()) {
            userLocationUpdatesDisposable.get().dispose();
        }
    }

    @SuppressLint("MissingPermission")
    public void locationPermissionGranted() {
        view.getCurrentState().getGoogleMap().get().setMyLocationEnabled(true);
        nearbyCompositeDisposables.add(observeUserLocation());
    }

    public void onDestroy() {
        nearbyCompositeDisposables.clear();
        if (previousNearbyApiCallDisposable.isPresent()
                && !previousNearbyApiCallDisposable.get().isDisposed())
            previousNearbyApiCallDisposable.get().dispose();
        if(mockLocationDisposable.isPresent()
                && mockLocationDisposable.get().isDisposed()) {
            mockLocationDisposable.get().dispose();
            mockLocationDisposable = Optional.empty();
        }
    }

    private Disposable observeUserLocation() {
        MockLocationActivityViewState state = view.getCurrentState();
        state.setUserLocationState(UserLocationState.LOADING);
        view.renderView();
        return model.observeLocationUpdates().subscribe(new Consumer<Optional<Location>>() {
            @Override
            public void accept(Optional<Location> location) {
                if(location.isPresent()) {
                    MockLocationActivityViewState state = view.getCurrentState();
                    state.setUserLocation(location);
                    state.setUserLocationState(UserLocationState.UPDATE_REQUESTED);
                    view.renderView();
                }
            }
        });
    }

    private Disposable observeEdtDistanceChanges() {
        return view.observeEdtDistanceChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String changedText) {
                        if(changedText != null
                                && !changedText.isEmpty()) {
                            view.getCurrentState()
                                    .setDistanceMockInMeters(Integer
                                            .parseInt(changedText));
                            if(mockLocationDisposable.isPresent()
                                    && mockLocationDisposable.get().isDisposed()) {
                                mockLocationDisposable.get().dispose();
                                mockLocationDisposable = Optional.empty();
                            }
                        }
                    }
                });
    }

    private Disposable observeEdtSpeedChanges() {
        return view.observeEdtSpeedChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String changedText) {
                        if(changedText != null
                                && !changedText.isEmpty()) {
                            Double speed = Double
                                    .parseDouble(changedText);
                            if(speed == 0)
                                speed = 1.0d;
                            view.getCurrentState()
                                    .setSpeedInXTimes(speed);
                            if(mockLocationDisposable.isPresent()
                                    && mockLocationDisposable.get().isDisposed()) {
                                mockLocationDisposable.get().dispose();
                                mockLocationDisposable = Optional.empty();
                            }
                        }
                    }
                });
    }

    public void showLocationSnackbar() {
        Snackbar.make(view, "permission denied", 4000)
                .setAction("open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", activity.getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                })
                .show();
    }

    private Disposable observeMockButtonClick() {
        return view.observeMockBtnClick().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                MockLocationActivityViewState state = view.getCurrentState();
                if(mockLocationDisposable.isPresent()
                        && mockLocationDisposable.get().isDisposed()) {
                    mockLocationDisposable.get().dispose();
                    mockLocationDisposable = Optional.empty();
                }
                if(!state.getDirectionsResponse().isPresent()) {
                    observeDirectionCall();
                } else {
                    mockLocationDisposable = Optional.of(model.observeMockLocation(view.getCurrentState())
                            .subscribe(new Consumer<LatLng>() {
                        @Override
                        public void accept(LatLng latLng) throws Exception {
                            MockLocationActivityViewState state = view.getCurrentState();
                            state.getRouteData().add(latLng);
                            view.renderView();
                        }
                    }));
                }
            }
        });
    }

    private Disposable observeSourceClick() {
        return view.observeSourceClick().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(activity);
                    activity.startActivityForResult(intent, SOURCE_PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (Exception ignored) {}
            }
        });
    }

    private Disposable observeDestinationClick() {
        return view.observeDestinationClick().subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) {
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(activity);
                    activity.startActivityForResult(intent, DESTINATION_PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (Exception ignored) {}
            }
        });
    }

    private void observeDirectionCall() {
        MockLocationActivityViewState state = view.getCurrentState();
        if(!state.getOrigin().isPresent() || !state.getDestination().isPresent())
            return;
        model.observeDistanceCall(state.getOrigin().get(),state.getDestination().get())
                .subscribe(new DisposableObserver<Response<DirectionsResponse>>() {
            @Override
            public void onNext(Response<DirectionsResponse> responseResponse) {
                if(responseResponse.isSuccessful()
                        && responseResponse.body() != null
                        && responseResponse.body().getStatus() != null
                        && responseResponse.body().getStatus().equals("OK")) {
                    MockLocationActivityViewState state = view.getCurrentState();
                    state.setDirectionsResponse(Optional.of(responseResponse.body()));
                    state.setApiState(ApiState.UPDATE_REQUESTED);
                    view.renderView();
                }
            }

            @Override
            public void onError(Throwable ignore) {}

            @Override
            public void onComplete() {
               dispose();
            }
        });
    }

    public void onSourceSelected(Place place) {
        MockLocationActivityViewState state = view.getCurrentState();
        state.setOrigin(Optional.of(place));
        state.setOriginState(ApiState.UPDATE_REQUESTED);
        view.renderView();
    }

    public void onDestinationSelected(Place place) {
        MockLocationActivityViewState state = view.getCurrentState();
        state.setDestination(Optional.of(place));
        state.setDestinationState(ApiState.UPDATE_REQUESTED);
        view.renderView();
    }
}