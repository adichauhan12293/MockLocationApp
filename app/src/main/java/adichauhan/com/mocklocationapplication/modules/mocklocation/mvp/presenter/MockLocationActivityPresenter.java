package adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.presenter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;

import java.util.Objects;

import javax.inject.Inject;

import adichauhan.com.entities.DirectionsResponse;
import adichauhan.com.entities.Optional;
import adichauhan.com.mocklocationapplication.app.utils.PermissionUtils;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.model.MockLocationActivityModel;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.MockLocationActivityView;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.MockLocationActivityViewState;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.ApiState;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.UserLocationState;
import io.reactivex.Scheduler;
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
    private Disposable mockLocationDisposable;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

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
                observeDirectionCall();
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
    }

    private Disposable observeUserLocation() {
        MockLocationActivityViewState state = view.getCurrentState();
        state.setUserLocationState(UserLocationState.LOADING);
        view.renderView();
        return model.observeLocationUpdates().subscribe(new Consumer<Optional<Location>>() {
            @Override
            public void accept(Optional<Location> location) {
                if(location.isPresent()) {
                    Log.d("adsasdasd",""+location.get().getLatitude()+" "+location.get().getLongitude());
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
                .subscribe(new Consumer<TextViewAfterTextChangeEvent>() {
                    @Override
                    public void accept(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                        if(textViewAfterTextChangeEvent.editable() != null
                                && !textViewAfterTextChangeEvent.editable().toString().isEmpty()) {
                            view.getCurrentState()
                                    .setDistanceMockInMeters(Integer
                                            .parseInt(textViewAfterTextChangeEvent.editable().toString()));
                        }
                    }
                });
    }

    private Disposable observeEdtSpeedChanges() {
        return view.observeEdtSpeedChanges()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TextViewAfterTextChangeEvent>() {
                    @Override
                    public void accept(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                        if(textViewAfterTextChangeEvent.editable() != null
                                && !textViewAfterTextChangeEvent.editable().toString().isEmpty()) {
                            view.getCurrentState()
                                    .setSpeedInXTimes(Double
                                            .parseDouble(textViewAfterTextChangeEvent.editable().toString()));
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
                if(!view.getCurrentState().getDirectionsResponse().isPresent())
                    return;
                mockLocationDisposable = model.observeMockLocation(view.getCurrentState()).subscribe(new Consumer<Location>() {
                    @Override
                    public void accept(Location location) {
                        model.mockLocation(location);
                    }
                });
            }
        });
    }

    private void observeDirectionCall() {
        model.observeDistanceCall().subscribe(new DisposableObserver<Response<DirectionsResponse>>() {
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
            public void onError(Throwable e) {
                Log.d("google API failed",e.toString());
            }

            @Override
            public void onComplete() {
               dispose();
            }
        });
    }
}