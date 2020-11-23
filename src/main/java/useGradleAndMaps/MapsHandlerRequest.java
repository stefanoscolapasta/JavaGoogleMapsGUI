package useGradleAndMaps;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.NotFoundException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;
import com.google.maps.model.TravelMode;

public class MapsHandlerRequest{
    
    private static GeoApiContext context;
    private static int FROM_M_TOKM = 1000;
    
    public MapsHandlerRequest() throws ApiException, InterruptedException, IOException {
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
                .mode(TravelMode.WALKING).await();
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
                .radius(radius*MapsHandlerRequest.FROM_M_TOKM)
                .keyword(query)
                .await();
        List<PlacesSearchResult> results = Arrays.asList(req.results);
        return new Pair<>(results, locationLatLang);
    }
    
}
