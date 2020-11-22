package useGradleAndMaps;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;

import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;

public class DrawLocationsPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final List<PlacesSearchResult> results;
    private final LatLng myCoordinates;
    private static final int WORLD_RADIOUS_IN_MT = 6378137;
    private static Point myPositionOnScreen;
    
    public DrawLocationsPanel(Pair <List<PlacesSearchResult>, LatLng> results) {
        this.results = results.first;
        this.myCoordinates = results.second;     
        DrawLocationsPanel.myPositionOnScreen = this.calculatePointFromCoordinates(this.myCoordinates);
    }
    
    public void paint(final Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        for(PlacesSearchResult res : results) {
            double distance = calculateDistanceFromMe(res.geometry.location, myCoordinates);
            System.out.println(distance);
            Point placeLocation = calculatePointFromCoordinates(res.geometry.location);
            int x = placeLocation.x % 944;
            int y = placeLocation.y % 412;
            g2d.drawRect(x, y, 40, 20);
            g2d.drawString("YOUR POSITION", myPositionOnScreen.x, myPositionOnScreen.y);
            g2d.drawString(res.name, x, y);
            g2d.drawLine(myPositionOnScreen.x, myPositionOnScreen.y, placeLocation.x, placeLocation.y);
        }
    }
    
    private double calculateDistanceFromMe(final LatLng C1, final LatLng C2) {
            final double dLat = rad(C2.lat - C1.lat);
            final double dLong = rad(C2.lng - C1.lng);
            
            final double a =    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                Math.cos(rad(C1.lat)) * Math.cos(rad(C2.lat)) *
                                Math.sin(dLong / 2) * Math.sin(dLong / 2);
            
            final double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            final double d = WORLD_RADIOUS_IN_MT * c;
            return d; // returns the distance in meter
    }
    
    private Point calculatePointFromCoordinates(final LatLng C) {
        System.out.println("Width " + this.getSize().height);
        return new Point(       Math.abs((int) (WORLD_RADIOUS_IN_MT * Math.cos(C.lat) * Math.cos(C.lng)) % 944),
                                Math.abs((int) (WORLD_RADIOUS_IN_MT * Math.cos(C.lat) * Math.sin(C.lng)) % 412));
    }
    
    private double rad(double x) {
        return x * Math.PI / 180;
    }
    
}
