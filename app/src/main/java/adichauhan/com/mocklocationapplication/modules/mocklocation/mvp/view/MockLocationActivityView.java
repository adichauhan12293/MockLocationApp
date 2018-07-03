package adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view;
import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import adichauhan.com.entities.DirectionsResponse;
import adichauhan.com.entities.Route;
import adichauhan.com.mocklocationapplication.R;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.ApiState;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.UserLocationState;
import adichauhan.com.reactivecomponents.provider.MapObservableProvider;
import io.reactivex.Observable;

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


    public MockLocationActivityView(AppCompatActivity activity,
                                    MockLocationActivityViewState initialState) {
        super(activity);
        currentState = initialState;
        inflate(getContext(), R.layout.activity_mock_location, this);
        mapFragment = (SupportMapFragment) activity.getSupportFragmentManager()
                .findFragmentById(R.id.map);
        btnMock = (Button) findViewById(R.id.btn_mock);
        edtDistanceStep = (EditText) findViewById(R.id.edt_distance);
        edtSpeedStep = (EditText) findViewById(R.id.edt_speed);
    }

    public Observable<GoogleMap> observeMapReadyState() {
        return MapObservableProvider.getMapReadyObservable(mapFragment);
    }

    public Observable<TextViewAfterTextChangeEvent> observeEdtSpeedChanges() {
        return RxTextView.afterTextChangeEvents(edtSpeedStep)
                .debounce(1000, TimeUnit.SECONDS);
    }

    public Observable<TextViewAfterTextChangeEvent> observeEdtDistanceChanges() {
        return RxTextView.afterTextChangeEvents(edtSpeedStep)
                .debounce(1000, TimeUnit.SECONDS);
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
            currentState.getGoogleMap().get().addMarker(new MarkerOptions().position(userLatLng));
        }
        if(currentState.getOrigin().isPresent()) {
            currentState.getGoogleMap().get()
                    .addMarker(new MarkerOptions().position(currentState.getOrigin().get()));
        }
        if(currentState.getDestination().isPresent()) {
            currentState.getGoogleMap().get()
                    .addMarker(new MarkerOptions().position(currentState.getDestination().get()));
        }
        if(currentState.getApiState() == ApiState.UPDATE_REQUESTED) {
            currentState.setApiState(ApiState.UPDATED);
            if(!currentState.getDirectionsResponse().isPresent()
                    || currentState.getDirectionsResponse().get().getRoutes() == null
                    || currentState.getDirectionsResponse().get().getRoutes().isEmpty())
                return;
            Route route = currentState.getDirectionsResponse().get().getRoutes().get(0);
            List<LatLng> latLngs = PolyUtil.decode(route.getOverviewPolyline().getPoints());
            PolylineOptions polylineOptions = new PolylineOptions();
            for(LatLng latLng : latLngs) {
                polylineOptions.add(latLng);
            }
            currentState.getGoogleMap().get().addPolyline(polylineOptions);
            //route.getOverviewPolyline();
        }
    }
}