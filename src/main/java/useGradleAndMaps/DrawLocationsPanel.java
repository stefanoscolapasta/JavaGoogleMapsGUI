package useGradleAndMaps;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
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
    private List<PlacesSearchResult> results = new ArrayList<>();
    private Point myCoordinates;
    private static final int RADIUS = 10;
    private static final int PROPORTIONAL_MULTIPLIER = 10_000_000 / 90 / 4;
    private Point CentralPoint;
    
    public void setResults(Pair <List<PlacesSearchResult>, LatLng> results) {
        this.results = results.first;
        this.myCoordinates = this.transformedCoordinates(results.second); 
    }
    
    public void paint(final Graphics g) {
        super.paintComponent(g);
        this.CentralPoint = new Point(this.getSize().width / 2, this.getSize().height / 2);
        Graphics2D g2d = (Graphics2D) g;
        
        
        for(PlacesSearchResult res : this.results) {
            
            System.out.println("LOCATION NEAR ---> name: " + res.name + " " + res.geometry.location + " ");
            LatLng whereToPlaceLocationOnPanel = calculateVectorDifference(myCoordinates, res.geometry.location);
            
            Point actualLocationPositionRelativeToScreen = new Point(
                    (int)(this.CentralPoint.x + whereToPlaceLocationOnPanel.lat),
                    (int)(this.CentralPoint.y + whereToPlaceLocationOnPanel.lng)
                    );
            
            g2d.fillOval(
                    actualLocationPositionRelativeToScreen.x,
                    actualLocationPositionRelativeToScreen.y,
                    RADIUS, RADIUS);
            
            g2d.drawString("YOU ARE HERE", CentralPoint.x, CentralPoint.y);
            g2d.drawString(
                    res.name,
                    actualLocationPositionRelativeToScreen.x, 
                    actualLocationPositionRelativeToScreen.y);
            g2d.drawLine(
                    CentralPoint.x,
                    CentralPoint.y, 
                    actualLocationPositionRelativeToScreen.x + (RADIUS / 2), 
                    actualLocationPositionRelativeToScreen.y + (RADIUS / 2));
        }
    }
    
    private LatLng calculateVectorDifference(final Point pivotLocation, final LatLng C2) {
        return new LatLng(
                (int)(transformedCoordinates(C2).y - pivotLocation.y), //La latitudine sono le Y la long le X 
                (int)(pivotLocation.x - transformedCoordinates(C2).x));
    }
    
    /**
     * 
     * @param C1 is the coordinate pair to transform to a number useful to be put on screen whith coordinates
     * @return LatLng a new set of coordinates
     */
    
    private Point transformedCoordinates(final LatLng C1) {
        return new Point((int)(C1.lat * PROPORTIONAL_MULTIPLIER),(int)(C1.lng * PROPORTIONAL_MULTIPLIER));
    }  
}