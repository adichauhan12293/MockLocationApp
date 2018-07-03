package adichauhan.com.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by adityachauhan on 30/06/18.
 */
public class Step {

    private DataValue distance;
    private DataValue duration;
    @JsonProperty("end_location")
    private GeoCoordinates endLocation;

    @JsonProperty("start_location")
    private GeoCoordinates startLocation;

    private PolyLine polyline;

    public DataValue getDistance() {
        return distance;
    }

    public void setDistance(DataValue distance) {
        this.distance = distance;
    }

    public DataValue getDuration() {
        return duration;
    }

    public void setDuration(DataValue duration) {
        this.duration = duration;
    }

    public GeoCoordinates getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(GeoCoordinates endLocation) {
        this.endLocation = endLocation;
    }

    public GeoCoordinates getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(GeoCoordinates startLocation) {
        this.startLocation = startLocation;
    }

    public PolyLine getPolyline() {
        return polyline;
    }

    public void setPolyline(PolyLine polyline) {
        this.polyline = polyline;
    }
}
