package adichauhan.com.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by adityachauhan on 30/06/18.
 */
public class Leg {

    private DataValue distance;
    private DataValue duration;

    @JsonProperty("end_location")
    private GeoCoordinates endLocation;

    @JsonProperty("start_location")
    private GeoCoordinates startLocation;

    private List<Step> steps;

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

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
