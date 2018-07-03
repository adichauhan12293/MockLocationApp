package adichauhan.com.entities;

import java.util.List;

/**
 * Created by adityachauhan on 30/06/18.
 */
public class DirectionsResponse {

    private String status;
    private List<Route> routes;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
