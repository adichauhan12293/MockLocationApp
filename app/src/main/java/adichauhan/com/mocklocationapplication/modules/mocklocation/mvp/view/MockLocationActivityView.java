package adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view;
import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.ArrayList;
import java.util.List;

import adichauhan.com.entities.DirectionsResponse;
import adichauhan.com.entities.Optional;
import adichauhan.com.entities.Route;
import adichauhan.com.mocklocationapplication.R;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.ApiState;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.UserLocationState;
import adichauhan.com.reactivecomponents.provider.MapObservableProvider;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * Created by adityachauhan on 10/8/17.
 *
 */

@SuppressLint("ViewConstructor")
public class MockLocationActivityView extends RelativeLayout {

    private MockLocationActivityViewState currentState;
    private SupportMapFragment mapFragment;

    private Button btnMock;

    private EditText edtDistanceStep;
    private EditText edtSpeedStep;
    private TextView tvSource;
    private TextView tvDestination;


    public MockLocationActivityView(AppCompatActivity activity,
                                    MockLocationActivityViewState initialState) {
        super(activity);
        currentState = initialState;
        inflate(getContext(), R.layout.activity_mock_location, this);
        mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        btnMock = findViewById(R.id.btn_mock);
        edtDistanceStep = findViewById(R.id.edt_distance);
        edtSpeedStep = findViewById(R.id.edt_speed);
        tvSource = findViewById(R.id.tv_source);
        tvDestination = findViewById(R.id.tv_destination);
        btnMock.setText("Get Route");
    }

    public Observable<GoogleMap> observeMapReadyState() {
        return MapObservableProvider.getMapReadyObservable(mapFragment);
    }

    public Observable<String> observeEdtSpeedChanges() {
        return RxTextView.textChanges(edtSpeedStep)
                .map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(@NonNull CharSequence charSequence) {
                        return charSequence.toString();
                    }
                });
    }

    public Observable<String> observeEdtDistanceChanges() {
        return RxTextView.textChanges(edtDistanceStep)
                .map(new Function<CharSequence, String>() {
                    @Override
                    public String apply(@NonNull CharSequence charSequence) {
                        return charSequence.toString();
                    }
                });
    }

    public Observable<Object> observeSourceClick() {
        return RxView.clicks(tvSource);
    }

    public Observable<Object> observeDestinationClick() {
        return RxView.clicks(tvDestination);
    }

    public MockLocationActivityViewState getCurrentState() {
         return currentState;
    }

    public Observable<Object> observeMockBtnClick() {
        return RxView.clicks(btnMock);
    }

    public void renderView() {
        if(currentState.getUserLocationState()
                == UserLocationState.UPDATE_REQUESTED) {
            currentState.setUserLocationState(UserLocationState.UPDATED);
            LatLng userLatLng = new LatLng(currentState.getUserLocation().get().getLatitude(),
                    currentState.getUserLocation().get().getLongitude());
            currentState.getGoogleMap().get().moveCamera(CameraUpdateFactory.newLatLngZoom(
                    userLatLng, 15.0f));
        }
        if(currentState.getRouteData() != null
                && !currentState.getRouteData().isEmpty()) {
            if(!currentState.getRouteMarkers().isEmpty()) {
                for(Marker marker : currentState.getRouteMarkers()) {
                    marker.remove();
                }
                currentState.getRouteMarkers().clear();
            }
            List<Marker> markers = new ArrayList<>();
            for(LatLng latLng : currentState.getRouteData()) {
                Marker currentMarker = currentState.getGoogleMap().get()
                        .addMarker(new MarkerOptions().position(latLng));
                markers.add(currentMarker);
            }
            currentState.setRouteMarkers(markers);
        } else {
            if(!currentState.getRouteMarkers().isEmpty()) {
                for(Marker marker : currentState.getRouteMarkers()) {
                    marker.remove();
                }
                currentState.getRouteMarkers().clear();
            }
        }
        if(currentState.getOriginState() == ApiState.UPDATE_REQUESTED) {
            currentState.setOriginState(ApiState.UPDATED);
            currentState.setDirectionsResponse(Optional.<DirectionsResponse>empty());
            currentState.setApiState(ApiState.UPDATE_REQUESTED);
            if(!currentState.getRouteMarkers().isEmpty()) {
                for(Marker marker : currentState.getRouteMarkers()) {
                    marker.remove();
                }
                currentState.getRouteMarkers().clear();
            }
            if (currentState.getOrigin().isPresent()) {
                Place place = currentState.getOrigin().get();
                tvSource.setText(String.format("Origin :%s", place.getName()));
                if(currentState.getOriginMarker().isPresent())
                    currentState.getOriginMarker().get().remove();
                Marker originMarker = currentState.getGoogleMap().get()
                        .addMarker(new MarkerOptions().position(place.getLatLng()));
                currentState.setOriginMarker(Optional.of(originMarker));
            } else if (currentState.getOriginMarker().isPresent()){
                currentState.getOriginMarker().get().remove();
                currentState.setOriginMarker(Optional.<Marker>empty());
            }
        }
        if(currentState.getDestinationState() == ApiState.UPDATE_REQUESTED) {
            currentState.setDirectionsResponse(Optional.<DirectionsResponse>empty());
            currentState.setApiState(ApiState.UPDATE_REQUESTED);
            currentState.setDestinationState(ApiState.UPDATED);
            if(!currentState.getRouteMarkers().isEmpty()) {
                for(Marker marker : currentState.getRouteMarkers()) {
                    marker.remove();
                }
                currentState.getRouteMarkers().clear();
            }
            if (currentState.getDestination().isPresent()) {
                Place place = currentState.getDestination().get();
                tvDestination.setText(String.format("Destination :%s", place.getName()));
                if(currentState.getDestinationMarker().isPresent())
                    currentState.getDestinationMarker().get().remove();
                Marker destinationMarker = currentState.getGoogleMap().get()
                        .addMarker(new MarkerOptions().position(place.getLatLng()));
                currentState.setDestinationMarker(Optional.of(destinationMarker));
            } else if (currentState.getDestinationMarker().isPresent()){
                currentState.getDestinationMarker().get().remove();
                currentState.setDestinationMarker(Optional.<Marker>empty());
            }
        }
        if(currentState.getApiState() == ApiState.UPDATE_REQUESTED) {
            currentState.setApiState(ApiState.UPDATED);
            if(!currentState.getDirectionsResponse().isPresent()
                    || currentState.getDirectionsResponse().get().getRoutes() == null
                    || currentState.getDirectionsResponse().get().getRoutes().isEmpty()) {
                if(currentState.getPolyline().isPresent()) {
                    currentState.getPolyline().get().remove();
                    currentState.setPolyline(Optional.<Polyline>empty());
                }
                btnMock.setText("Get Route");
                return;
            }
            Route route = currentState.getDirectionsResponse().get().getRoutes().get(0);
            List<LatLng> latLngs = PolyUtil.decode(route.getOverviewPolyline().getPoints());
            PolylineOptions polylineOptions = new PolylineOptions();
            for(LatLng latLng : latLngs) {
                polylineOptions.add(latLng);
            }
            Polyline polyLine = currentState.getGoogleMap().get().addPolyline(polylineOptions);
            currentState.setPolyline(Optional.of(polyLine));
            btnMock.setText("Mock");
            if(!currentState.getRouteMarkers().isEmpty()) {
                for(Marker marker : currentState.getRouteMarkers()) {
                    marker.remove();
                }
                currentState.getRouteMarkers().clear();
            }
        }
    }
}