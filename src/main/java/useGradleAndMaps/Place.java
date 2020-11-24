package useGradleAndMaps;

import java.awt.Graphics2D;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;

public class Place {
    
    static int DEFAULT_SIZE = 10;
    static int HOVER_SIZE = 20;
    
    private Double x;
    private Double y;
    private int size = DEFAULT_SIZE;
    private PlacesSearchResult place;
    
    private LatLng coordinates;
    private Place basic;
    
    
    Place(PlacesSearchResult place, LatLng coordinates, Double x, Double y){
        this.place = place;
        this.coordinates = coordinates;
        this.x = x;
        this.y = y;
    }
    
    public LatLng getPosition() {
        return this.coordinates;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setSize(int size) {
        this.size = size;
    }
    
    
    public Double getX() {
        return this.x;
    }
    public Double getY() {
        return this.y;
    }
    public int getSize() {
        return this.size;
    }
    
    public PlacesSearchResult getPlace() {
        return this.place;
    }
    
    
    
    void draw(Graphics2D g) {
        
    }
    
}
