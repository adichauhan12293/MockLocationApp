package adichauhan.com.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by adityachauhan on 30/06/18.
 */

public class Route {

    @JsonProperty("overview_polyline")
    private PolyLine overviewPolyline;

    private List<Leg> legs;

    public PolyLine getOverviewPolyline() {
        return overviewPolyline;
    }

    public void setOverviewPolyline(PolyLine overviewPolyline) {
        this.overviewPolyline = overviewPolyline;
    }

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }
}
