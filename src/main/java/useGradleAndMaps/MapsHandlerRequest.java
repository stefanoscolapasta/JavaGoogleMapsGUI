package useGradleAndMaps;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.NotFoundException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

public class MapsHandlerRequest{
    
    private static GeoApiContext context;
 
//    public MapsHandlerRequest() throws ApiException, InterruptedException, IOException {
//        this.context 
//    }
    
    public MapsHandlerRequest() {
        try {
            MapsHandlerRequest.context = new GeoApiContext.Builder().apiKey(new ApiKey().getApiKey()).build();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public String getDistance(final String origin, final String destination) throws NotFoundException, ApiException, InterruptedException, IOException, NoSuchElementException {
        //GeocodingResult[] results = GeocodingApi.geocode(this.context, "44.142986, 12.240852").await();
        if(origin.isBlank() || destination.isBlank()) {
            throw new NoSuchElementException();
        }
        DirectionsResult directions = DirectionsApi.getDirections(context, origin, destination).await();
//        System.out.println(directions.routes[0].legs[0]);
//        System.out.println(directions.routes[0].legs[0].startAddress);
        return String.valueOf(directions.routes[0].legs[0].distance.humanReadable);
    }
    
    public String getTimeTravel(final String origin, final String destination) throws ApiException, InterruptedException, IOException {
        if(origin.isBlank() || destination.isBlank()) {
            throw new NoSuchElementException();
        }
        //GeocodingResult[] results = GeocodingApi.geocode(this.context, "44.142986, 12.240852").await();
        DirectionsResult directions = DirectionsApi.getDirections(context, origin, destination).mode(TravelMode.WALKING).await();
        return String.valueOf(directions.routes[0].legs[0].duration.humanReadable);
    }
    
}
