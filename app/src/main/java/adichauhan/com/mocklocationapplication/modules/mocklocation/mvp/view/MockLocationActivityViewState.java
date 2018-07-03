package adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view;


import android.location.Location;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import adichauhan.com.entities.DirectionsResponse;
import adichauhan.com.entities.Optional;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.ApiState;
import adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view.enums.UserLocationState;

/**
 * Created by adityachauhan on 10/11/17.
 *
 */

public class MockLocationActivityViewState {
    public Optional<DirectionsResponse> getDirectionsResponse() {
        return directionsResponse;
    }

    public void setDirectionsResponse(Optional<DirectionsResponse> directionsResponse) {
        this.directionsResponse = directionsResponse;
    }

    private Optional<GoogleMap> googleMap;
    private UserLocationState userLocationState;
    private Optional<Location> userLocation;
    private ApiState apiState;
    private Optional<LatLng> origin;
    private Optional<LatLng> destination;
    private Optional<DirectionsResponse> directionsResponse;
    private Integer distanceMockInMeters;
    private Double speedInXTimes;

    @Inject
    MockLocationActivityViewState() {
        googleMap = Optional.empty();
        userLocationState = UserLocationState.UPDATED;
        userLocation = Optional.empty();
        apiState = ApiState.UPDATED;
        origin = Optional.of(new LatLng(12.983507, 77.596822));
        destination = Optional.of(new LatLng(13.041849, 77.617731));
        directionsResponse = Optional.empty();
        distanceMockInMeters = 50;
        speedInXTimes = 1d;
    }

    public Optional<GoogleMap> getGoogleMap() {
        return googleMap;
    }

    public void setGoogleMap(Optional<GoogleMap> googleMap) {
        this.googleMap = googleMap;
    }

    public UserLocationState getUserLocationState() {
        return userLocationState;
    }

    public void setUserLocationState(UserLocationState userLocationState) {
        this.userLocationState = userLocationState;
    }

    public Optional<Location> getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Optional<Location> userLocation) {
        this.userLocation = userLocation;
    }

    public ApiState getApiState() {
        return apiState;
    }

    public void setApiState(ApiState apiState) {
        this.apiState = apiState;
    }

    public Optional<LatLng> getOrigin() {
        return origin;
    }

    public void setOrigin(Optional<LatLng> origin) {
        this.origin = origin;
    }

    public Optional<LatLng> getDestination() {
        return destination;
    }

    public void setDestination(Optional<LatLng> destination) {
        this.destination = destination;
    }

    public Integer getDistanceMockInMeters() {
        return distanceMockInMeters;
    }

    public void setDistanceMockInMeters(Integer distanceMockInMeters) {
        this.distanceMockInMeters = distanceMockInMeters;
    }

    public Double getSpeedInXTimes() {
        return speedInXTimes;
    }

    public void setSpeedInXTimes(Double speedInXTimes) {
        this.speedInXTimes = speedInXTimes;
    }
}
