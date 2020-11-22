package useGradleAndMaps;

import java.io.IOException;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.TravelMode;

public class MapsHandlerRequest {
    private static final GeoApiContext context = new GeoApiContext.Builder().apiKey(new ApiKey().getApiKey()).build(); ;
    
//    public MapsHandlerRequest() throws ApiException, InterruptedException, IOException {
//        this.context 
//    }
    
    public static String getDistance(final String origin, final String destination) throws ApiException, InterruptedException, IOException {
        //GeocodingResult[] results = GeocodingApi.geocode(this.context, "44.142986, 12.240852").await();
        DirectionsResult directions = DirectionsApi.getDirections(context, origin, destination).await();
//        System.out.println(directions.routes[0].legs[0]);
//        System.out.println(directions.routes[0].legs[0].startAddress);
        return String.valueOf(directions.routes[0].legs[0].distance.humanReadable);
    }
    
    public static String getTimeTravel(final String origin, final String destination) throws ApiException, InterruptedException, IOException {
        //GeocodingResult[] results = GeocodingApi.geocode(this.context, "44.142986, 12.240852").await();
        DirectionsResult directions = DirectionsApi.getDirections(context, origin, destination).mode(TravelMode.WALKING).await();
        return String.valueOf(directions.routes[0].legs[0].duration.humanReadable);
    }
    
}
