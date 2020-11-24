package useGradleAndMaps;

import org.gavaghan.geodesy.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


import com.google.maps.ImageResult;
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
    private static final int PROPORTIONAL_MULTIPLIER = 1_000_000 / 90;
    private Point CentralPoint;
    private final static Color DEFAULT_NODE_COLOR = Color.RED;
    private final static Color DEFAULT_LINE_COLOR = Color.ORANGE;
    private final static Color DEFAULT_STRING_COLOR = Color.WHITE;
    private ImageIcon backgroundImage = new ImageIcon();
    
    
    
    private LatLng myPosition;
    private final double widthInMeter = (40_000/Math.pow(2,  15)) * 2 * 1_000;
    private final double heightInMeter = (40_000/Math.pow(2,  15)) * 2 * 1_000;
    
    public void setResults(Pair <List<PlacesSearchResult>, LatLng> results, ImageResult geoImageRes) {
        this.results = results.first;
        this.myCoordinates = this.transformedCoordinates(results.second); 
        this.backgroundImage = new ImageIcon(geoImageRes.imageData);
        this.myPosition = results.second;
    }
    
    public void paint(final Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        Dimension scaledImageDimension = this.getScaledDimension(
                new Dimension(
                        this.backgroundImage.getIconWidth(),
                        this.backgroundImage.getIconHeight()),
                this.getSize()
                );
        
        
        final double realWidthInMeterPerPixel = this.widthInMeter / (double)scaledImageDimension.width;
        final double realHeightInMeterPerPixel = this.heightInMeter / (double)scaledImageDimension.height;
        
        System.out.println("realWidthInMeterPerPixel = " + realWidthInMeterPerPixel);
        System.out.println("realHeightInMeterPerPixel = " + realHeightInMeterPerPixel);
        
        this.CentralPoint = new Point(scaledImageDimension.width / 2, scaledImageDimension.height / 2);
        
        g2d.drawImage(
                this.backgroundImage.getImage(),
                0,
                0,
                scaledImageDimension.width,
                scaledImageDimension.height,
                null
                );

        
        
        for(PlacesSearchResult res : this.results) {
            
            
            final double distanceInMeter = this.calculateDistanceInMeter(this.myPosition, res.geometry.location);            
            final double distanceFromMyPosition = (distanceInMeter / realWidthInMeterPerPixel);
            
            final double angleFromMyPosition = this.calculateAngleFromCoordinate(this.myPosition, res.geometry.location) + 90;
            final double incrementX = Math.cos(Math.toRadians(angleFromMyPosition)) * distanceFromMyPosition;
            final double incrementY = Math.sin(Math.toRadians(angleFromMyPosition)) * distanceFromMyPosition;
            
            /*
             System.out.println( "\n" +
                                "\n - LOCATION = " + res.name + 
                                "\n - Distance = " + distanceFromMyPosition + " px" +
                                "\n - Angle = " + angleFromMyPosition + "°" +
                                "\n - incrementX = " + incrementX + 
                                "\n - incrementY = " + incrementY
                                );
            */
            Point actualLocationPositionRelativeToScreen = new Point(
                    (int)(this.CentralPoint.x + incrementX),
                    (int)(this.CentralPoint.y - incrementY));
                    

            g2d.setColor(DrawLocationsPanel.DEFAULT_NODE_COLOR);
            g2d.fillOval(
                    actualLocationPositionRelativeToScreen.x,
                    actualLocationPositionRelativeToScreen.y,
                    RADIUS, RADIUS);
            
            g2d.setColor(DrawLocationsPanel.DEFAULT_STRING_COLOR);
            g2d.drawString("YOU ARE HERE", CentralPoint.x, CentralPoint.y);
            g2d.drawString(
                    res.name,
                    actualLocationPositionRelativeToScreen.x, 
                    actualLocationPositionRelativeToScreen.y);
            g2d.setColor(DrawLocationsPanel.DEFAULT_LINE_COLOR);
            g2d.drawLine(
                    CentralPoint.x,
                    CentralPoint.y, 
                    actualLocationPositionRelativeToScreen.x + (RADIUS / 2), 
                    actualLocationPositionRelativeToScreen.y + (RADIUS / 2));
        }
    }
    
    private double calculateDistanceInMeter(LatLng p1, LatLng p2) {
        
        GeodeticCalculator geoCalc = new GeodeticCalculator();

        Ellipsoid reference = Ellipsoid.WGS84;  

        GlobalPosition userPos = new GlobalPosition(p1.lat, p1.lng, 0.0); // Point A

        GlobalPosition pointA = new GlobalPosition(p2.lat, p2.lng, 0.0); // Point B

        double distance = geoCalc.calculateGeodeticCurve(reference, userPos, pointA).getEllipsoidalDistance(); // Distance between Point A and Point B
        
        return distance;
        
        /*
        double lat1 = p1.lat;
        double lng1 = p1.lng;
        double lat2 = p2.lat;
        double lng2 = p2.lng;
        
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
        
        */
        /*
        double theta = p1.lng - p2.lng;
        double dist = Math.sin(Math.toRadians(p1.lat)) * Math.sin(Math.toRadians(p2.lat)) + Math.cos(Math.toRadians(p1.lat)) * Math.cos(Math.toRadians(p2.lat)) * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist * 1000;
        return dist;
        */
    }
    
    private double calculateAngleFromCoordinate(LatLng p1, LatLng p2) {

        double lat1 = Math.toRadians(p1.lat);
        double long1 = Math.toRadians(p1.lng);
        double lat2 = Math.toRadians(p2.lat);
        double long2 = Math.toRadians(p2.lng);
        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

        return brng;
    }

    /**
     * 
     * @param C1 is the coordinate pair to transform to a number useful to be put on screen whith coordinates
     * @return LatLng a new set of coordinates
     */
    
    private Point transformedCoordinates(final LatLng C1) {
        return new Point((int)(C1.lng * PROPORTIONAL_MULTIPLIER), (int)(C1.lat * PROPORTIONAL_MULTIPLIER));
    }  
    
    public Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

        int original_width = imgSize.width;
        int original_height = imgSize.height;
        int bound_width = boundary.width;
        int bound_height = boundary.height;
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }
    
    
}
