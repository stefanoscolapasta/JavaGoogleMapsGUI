package useGradleAndMaps;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.NearbySearchRequest;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResponse;
import com.google.maps.model.PlacesSearchResult;

public class testNearby {
    public static void main(String[] args) throws ApiException, InterruptedException {
        try {
            GeoApiContext context = new GeoApiContext.Builder().apiKey(new ApiKey().getApiKey()).build();
            GeocodingResult[] coord = GeocodingApi.geocode(context, "Via Fornaci 7, Cesena, Italia").await();
            LatLng locationLatLang = coord[0].geometry.location;
            PlacesSearchResponse req = new NearbySearchRequest(context).location(locationLatLang).radius(5).keyword("Gelateria").await();
            List<PlacesSearchResult> results = Arrays.asList(req.results);
            results.stream().filter(place -> place.rating > 1).forEach(i -> System.out.println(i.name));
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
}
