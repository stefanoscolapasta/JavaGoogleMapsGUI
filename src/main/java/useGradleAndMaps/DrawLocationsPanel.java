package useGradleAndMaps;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import com.google.maps.DirectionsApiRequest;
import com.google.maps.DirectionsApiRequest.Waypoint;
import com.google.maps.ImageResult;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.GeocodedWaypoint;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;

public class DrawLocationsPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<PlacesSearchResult> results = new ArrayList<>();
    private static final int RADIUS = 10;
    private Point CentralPoint;
    private final static Color DEFAULT_NODE_COLOR = Color.RED;
    private final static Color DEFAULT_LINE_COLOR = Color.ORANGE;
    private final static Color DEFAULT_STRING_COLOR = Color.WHITE;
    private ImageIcon backgroundImage = new ImageIcon();
    private MapsHandlerRequest maps;

    private List<Place> places = new ArrayList<>();
    private Place myPosition;
    private final double widthInMeter = (40_000 / Math.pow(2, 15)) * 2 * 1_000;
    private final double heightInMeter = (40_000 / Math.pow(2, 15)) * 2 * 1_000;
    private double realWidthInMeterPerPixel;
    private double realHeightInMeterPerPixel;

    public DrawLocationsPanel(MapsHandlerRequest maps) {
        this.maps = maps;
    }

    public void setResults(Pair<List<PlacesSearchResult>, LatLng> results, ImageResult geoImageRes) {
        this.results = results.first;
        this.backgroundImage = new ImageIcon(geoImageRes.imageData);

        this.places = new ArrayList<>();
        results.first.forEach(elem -> {
            places.add(new Place(elem, elem.geometry.location, 0.0, 0.0));
        });

        this.myPosition = new Place(null, results.second, 0.0, 0.0);
    }

    public void paint(final Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        Dimension scaledImageDimension = this.getScaledDimension(
                new Dimension(this.backgroundImage.getIconWidth(), this.backgroundImage.getIconHeight()),
                this.getSize());

        realWidthInMeterPerPixel = this.widthInMeter / (double) scaledImageDimension.width;
        realHeightInMeterPerPixel = this.heightInMeter / (double) scaledImageDimension.height;

        if (this.places.size() > 0) {

            this.myPosition.setX(this.getSize().getWidth() / 2);
            this.myPosition.setY(this.getSize().getHeight() / 2);

            this.CentralPoint = new Point(this.myPosition.getX().intValue(), this.myPosition.getY().intValue());

            g2d.drawImage(this.backgroundImage.getImage(), (int) (myPosition.getX() - (scaledImageDimension.width / 2)),
                    (int) (myPosition.getY() - (scaledImageDimension.height / 2)), scaledImageDimension.width,
                    scaledImageDimension.height, null);
            
            g2d.setColor(DrawLocationsPanel.DEFAULT_STRING_COLOR);
            g2d.drawString("YOU ARE HERE", CentralPoint.x, CentralPoint.y);

        }

        this.places.forEach(res -> {
            
            List<EncodedPolyline> encodedPolys = new ArrayList<>();
            
            try {
                final DirectionsResult req = this.maps.getPath(this.myPosition.getPosition(), res.getPosition()).await();
                List<DirectionsRoute> listRoutes = Arrays.asList(req.routes);
                List<DirectionsLeg> listLegs = new ArrayList<>();
                listRoutes.stream().forEach(i -> listLegs.addAll(Arrays.asList(i.legs)));
                
                List<DirectionsStep> listSteps = new ArrayList<>();
                listLegs.forEach(i -> listSteps.addAll(Arrays.asList(i.steps)));
                
                listSteps.forEach(i -> encodedPolys.addAll(Arrays.asList(i.polyline)));
                
                //list.stream().forEach(i -> System.out.println(i.));
            } catch (ApiException | InterruptedException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
            //QUA DENTRO CI SONO LE COORDINATE DI TUTTI I PUNTI DEL PATH!!!
            encodedPolys.forEach(i -> System.out.print(" " + i.decodePath()));
            
            Pair<Double, Double> increment = this.getPointsFromCoordinate(this.myPosition.getPosition(), res.getPosition());

            Point actualLocationPositionRelativeToScreen = new Point((int) (this.CentralPoint.x + increment.first),
                    (int) (this.CentralPoint.y - increment.second));
            
            res.setX((double) actualLocationPositionRelativeToScreen.x);
            res.setY((double) actualLocationPositionRelativeToScreen.y);
            
            //LatLng whereToPlaceLocationOnPanel = calculateVectorDifference(myCoordinates, res.geometry.location);
            
            g2d.setColor(DrawLocationsPanel.DEFAULT_NODE_COLOR);
            g2d.fillOval(actualLocationPositionRelativeToScreen.x, 
                    actualLocationPositionRelativeToScreen.y, 
                    res.getSize(),
                    res.getSize());

            g2d.setStroke(new BasicStroke(res.getSize() / 5 ));
            g2d.setColor(DrawLocationsPanel.DEFAULT_LINE_COLOR);
            g2d.drawLine(CentralPoint.x, CentralPoint.y, actualLocationPositionRelativeToScreen.x + (res.getSize() / 2),
                   actualLocationPositionRelativeToScreen.y + (res.getSize() / 2));
            
            
            
            g2d.setColor(DrawLocationsPanel.DEFAULT_STRING_COLOR);            
            
            g2d.setFont(new Font("Default", 0, res.getSize()));            
            g2d.drawString(res.getPlace().name, actualLocationPositionRelativeToScreen.x,
                    actualLocationPositionRelativeToScreen.y);
            
            res.setSize(Place.DEFAULT_SIZE);
            
        });
        
    }
    
    public Place getPlaceNearPoint(int x, int y) {
        double MAX_DIST = 10; // px
        
        Place nearestPlace = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Place place : this.places) {
            double distance = Math.sqrt(
                    Math.pow(place.getX() - x, 2) + Math.pow(place.getY() - y, 2));
            
            if (distance < MAX_DIST && distance < nearestDistance) {
                nearestPlace = place;
            }
        }       
        
        
        return nearestPlace;
    }

    private Pair<Double, Double> getPointsFromCoordinate(LatLng p1, LatLng p2) {

        final double distanceInMeter = this.calculateDistanceInMeter(p1, p2);
        final double distanceFromMyPosition = (distanceInMeter / realWidthInMeterPerPixel);

        final double angleFromMyPosition = this.calculateAngleFromCoordinate(p1, p2) + 90;
        final double incrementX = Math.cos(Math.toRadians(angleFromMyPosition)) * distanceFromMyPosition;
        final double incrementY = Math.sin(Math.toRadians(angleFromMyPosition)) * distanceFromMyPosition;

        return new Pair<Double, Double>(incrementX, incrementY);

    }

    private double calculateDistanceInMeter(LatLng p1, LatLng p2) {

        double lat1 = p1.lat;
        double lng1 = p1.lng;
        double lat2 = p2.lat;
        double lng2 = p2.lng;

        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist;

    }

    private double calculateAngleFromCoordinate(LatLng p1, LatLng p2) {

        double lat1 = Math.toRadians(p1.lat);
        double long1 = Math.toRadians(p1.lng);
        double lat2 = Math.toRadians(p2.lat);
        double long2 = Math.toRadians(p2.lng);
        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        brng = 360 - brng; // count degrees counter-clockwise - remove to make clockwise

        return brng;
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
            // scale width to fit
            new_width = bound_width;
            // scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            // scale height to fit instead
            new_height = bound_height;
            // scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new Dimension(new_width, new_height);
    }

}
