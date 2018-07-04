package adichauhan.com.mocklocationapplication.modules.mocklocation.mvp.view;


import android.location.Location;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

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
    private Optional<Place> origin;
    private Optional<Place> destination;
    private ApiState originState;
    private ApiState destinationState;
    private Optional<DirectionsResponse> directionsResponse;
    private Integer distanceMockInMeters;
    private Double speedInXTimes;
    private List<LatLng> routeData;
    private Optional<Marker> originMarker;
    private Optional<Marker> destinationMarker;
    private Optional<Polyline> polyline;
    private List<Marker> routeMarkers;

    @Inject
    MockLocationActivityViewState() {
        googleMap = Optional.empty();
        userLocationState = UserLocationState.UPDATED;
        userLocation = Optional.empty();
        apiState = ApiState.UPDATED;
        originState = ApiState.UPDATED;
        destinationState = ApiState.UPDATED;
        directionsResponse = Optional.empty();
        distanceMockInMeters = 50;
        speedInXTimes = 1d;
        routeData = new ArrayList<>();
        origin = Optional.empty();
        destination = Optional.empty();
        originMarker = Optional.empty();
        destinationMarker = Optional.empty();
        polyline = Optional.empty();
        routeMarkers = new ArrayList<>();
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

    public List<LatLng> getRouteData() {
        return routeData;
    }

    public Optional<Place> getOrigin() {
        return origin;
    }

    public void setOrigin(Optional<Place> origin) {
        this.origin = origin;
    }

    public Optional<Place> getDestination() {
        return destination;
    }

    public void setDestination(Optional<Place> destination) {
        this.destination = destination;
    }

    public ApiState getOriginState() {
        return originState;
    }

    public void setOriginState(ApiState originState) {
        this.originState = originState;
    }

    public ApiState getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(ApiState destinationState) {
        this.destinationState = destinationState;
    }

    public Optional<Marker> getDestinationMarker() {
        return destinationMarker;
    }

    public void setDestinationMarker(Optional<Marker> destinationMarker) {
        this.destinationMarker = destinationMarker;
    }

    public Optional<Marker> getOriginMarker() {
        return originMarker;
    }

    public void setOriginMarker(Optional<Marker> originMarker) {
        this.originMarker = originMarker;
    }

    public Optional<Polyline> getPolyline() {
        return polyline;
    }

    public void setPolyline(Optional<Polyline> polyline) {
        this.polyline = polyline;
    }

    public List<Marker> getRouteMarkers() {
        return routeMarkers;
    }

    public void setRouteMarkers(List<Marker> routeMarkers) {
        this.routeMarkers = routeMarkers;
    }
}
