package useGradleAndMaps;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.ImageResult;
import com.google.maps.NearbySearchRequest;
import com.google.maps.StaticMapsApi;
import com.google.maps.StaticMapsRequest.StaticMapType;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.NotFoundException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.Size;
import com.google.maps.model.TravelMode;

public class MapsHandlerRequest{
    
    private static GeoApiContext context;
    private static int FROM_M_TO_KM = 1000;
    private static int SIZE_W_REQUEST = 1920;
    private static int SIZE_H_REQUEST = 1080;
    private final int zoom = 15;
    private final Size imageSize = new Size(SIZE_W_REQUEST, SIZE_H_REQUEST); 
    
    public MapsHandlerRequest() throws ApiException, InterruptedException {
        try {
            MapsHandlerRequest.context = new GeoApiContext.Builder().apiKey(new ApiKey().getApiKey()).build();
        } catch (IOException e) {
            e.printStackTrace();
        }    
    }
    
    public String getDistance(final String origin, final String destination) throws NotFoundException, ApiException, InterruptedException, IOException, NoSuchElementException {

        if(origin.isBlank() || destination.isBlank()) {
            throw new NoSuchElementException();
        }
        DirectionsResult directions = DirectionsApi.getDirections(context, origin, destination).await();
        return String.valueOf(directions.routes[0].legs[0].distance.humanReadable);
    }
    
    public String getTimeTravel(final String origin, final String destination) throws ApiException, InterruptedException, IOException {
        if(origin.isBlank() || destination.isBlank()) {
            throw new NoSuchElementException();
        }
        DirectionsResult directions = DirectionsApi
                .getDirections(context, origin, destination)
                .mode(TravelMode.WALKING)
                .await();
        return String.valueOf(directions.routes[0].legs[0].duration.humanReadable);
    }
    
    public Pair<List<PlacesSearchResult>, LatLng> getTimeTravel(final String origin, final String query, int radius) throws NoSuchElementException, ApiException, InterruptedException, IOException {
        if(origin.isBlank() || query.isBlank() || radius > 50 ||  radius < 1) {
            throw new NoSuchElementException();
        }
        GeocodingResult[] coord = GeocodingApi.geocode(context, origin).await();
        LatLng locationLatLang = coord[0].geometry.location;
        System.out.println("YOUR LOCATION--->" + locationLatLang);
        PlacesSearchResponse req = new NearbySearchRequest(context)
                .location(locationLatLang)
                .radius(radius*MapsHandlerRequest.FROM_M_TO_KM)
                .keyword(query)
                .await();
        List<PlacesSearchResult> results = Arrays.asList(req.results);
        return new Pair<>(results, locationLatLang);
    }
    
    public DirectionsApiRequest getPath(final LatLng origin, final LatLng destination) {
        return new DirectionsApiRequest(context).origin(origin).destination(destination); 
    }
    
    public ImageResult getGeoImageAtCoordinates(final LatLng locationLatLang) throws ApiException, InterruptedException, IOException {
        return StaticMapsApi
                .newRequest(context, this.imageSize)
                .center(locationLatLang)
                .zoom(this.zoom)
                .scale(1)
                .maptype(StaticMapType.satellite)
                .await();    
    }
    
    public int getImageScaleValue() {
        return this.zoom;
    }
    
}
