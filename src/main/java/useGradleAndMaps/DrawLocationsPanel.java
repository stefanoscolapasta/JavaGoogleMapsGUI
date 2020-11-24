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
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;

public class DrawLocationsPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Point CentralPoint;
    private final static Color DEFAULT_NODE_COLOR = Color.RED;
    private final static Color DEFAULT_LINE_COLOR = Color.ORANGE;
    private final static Color DEFAULT_STRING_COLOR = Color.WHITE;
    private final static Color DEFAULT_PATH_HOVER_COLOR = Color.CYAN;
    private ImageIcon backgroundImage = new ImageIcon();
    private MapsHandlerRequest maps;
    private final int EARTH_RADIUS = 7_000_000; //We fiddled around with various earth radius values and found out tha 7_000 km is a good number
    private final int FULL_CIRCLE = 360;
    private final int MAXIMUM_DISTANCE_IN_PX_TO_HOVER = 10; //
    private List<Place> places = new ArrayList<>();
    private Place myPosition;
    private double imageSideInMeters = (40_000 / Math.pow(2, MapsHandlerRequest.DEFAULT_ZOOM)) * 2 * 1_000;   
    private double metersPerPixel;

    public DrawLocationsPanel(MapsHandlerRequest maps) {
        this.maps = maps;
    }
    
    public Place getMyPosition() {
        return this.myPosition;
    }
    
    public void refreshImage() {
        try {
            this.backgroundImage = new ImageIcon(this.maps.getGeoImageAtCoordinates(this.myPosition.getPosition()).imageData);
            this.imageSideInMeters = (40_000 / Math.pow(2, this.maps.getZoom())) * 2 * 1_000;
        } catch (ApiException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * @param results is used to create some ADT to handle single coordinates
     * @param geoImageRes is the image we request the API for and is here handled to
     * be used effectively in the repaint() method
     * */
    public void setResults(Pair<List<PlacesSearchResult>, LatLng> results) {
        this.myPosition = new Place(null, results.second, 0.0, 0.0);
        this.refreshImage();
        this.places = new ArrayList<>();
      
        results.first.forEach(elem -> {
            /*
             * Creating new Place object, which contains a single PlaceSearchResult from
             * the API, the coordinates, and the starting position on screen. 
             * */
            Place p = new Place(elem, elem.geometry.location, 0.0, 0.0);
            
            List<EncodedPolyline> encodedPolys = new ArrayList<>();
            /*
             * Trying to access individual pathPointCoordinates for each destinations path from origin
             * 
             * */
            try {
                final DirectionsResult req = this.maps.getPath(this.myPosition.getPosition(), elem.geometry.location).await();
                List<DirectionsRoute> listRoutes = Arrays.asList(req.routes);
                List<DirectionsLeg> listLegs = new ArrayList<>();
                listRoutes.stream().forEach(i -> listLegs.addAll(Arrays.asList(i.legs)));
                
                List<DirectionsStep> listSteps = new ArrayList<>();
                listLegs.forEach(i -> listSteps.addAll(Arrays.asList(i.steps)));
                
                listSteps.forEach(i -> encodedPolys.addAll(Arrays.asList(i.polyline)));

            } catch (ApiException | InterruptedException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            /**
             * Here we are accessing the decoded PathPoints(in LatLng), saving them
             * in a List and adding to Place p this list with the getPath() method.
             * Once this is done, we add the Place object to the List<Place> places. 
             * */
            List<LatLng> pathLatLng = new ArrayList<>();  
            encodedPolys.forEach(i -> pathLatLng.addAll(i.decodePath()));
            p.setPath(pathLatLng);
            this.places.add(p);
        });
    }

    public void paint(final Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        Dimension scaledImageDimension = this.getScaledDimension(
                new Dimension(this.backgroundImage.getIconWidth(), this.backgroundImage.getIconHeight()),
                this.getSize());

        this.metersPerPixel = this.imageSideInMeters / (double) scaledImageDimension.width;

        //Here we handle the central point
        if (this.places.size() > 0) {
            this.myPosition.setX(this.getSize().getWidth() / 2);
            this.myPosition.setY(this.getSize().getHeight() / 2);

            this.CentralPoint = new Point(this.myPosition.getX().intValue(), this.myPosition.getY().intValue());

            g2d.drawImage(
                    this.backgroundImage.getImage(),
                    (int) (myPosition.getX() - (scaledImageDimension.width / 2)),
                    (int) (myPosition.getY() - (scaledImageDimension.height / 2)),
                    scaledImageDimension.width,
                    scaledImageDimension.height,
                    null
                    );
            
            g2d.setColor(DrawLocationsPanel.DEFAULT_STRING_COLOR);
            g2d.drawString("YOU ARE HERE", CentralPoint.x, CentralPoint.y);

        }
        
        //Here we handle destination points and paths/lines
                
        this.places.forEach(res -> {            
            
            Pair<Double, Double> increment = this.getPointsFromCoordinate(this.myPosition.getPosition(), res.getPosition());
    
            Point actualLocationPositionRelativeToScreen = new Point((int) (this.CentralPoint.x + increment.first),
                    (int) (this.CentralPoint.y - increment.second));
            
            res.setX((double) actualLocationPositionRelativeToScreen.x);
            res.setY((double) actualLocationPositionRelativeToScreen.y);
                      
            g2d.setColor(DrawLocationsPanel.DEFAULT_NODE_COLOR);
            
            g2d.fillOval(actualLocationPositionRelativeToScreen.x - (res.getSize()/2), 
                    actualLocationPositionRelativeToScreen.y - (res.getSize()/2), 
                    res.getSize(),
                    res.getSize());

            g2d.setStroke(new BasicStroke(res.getSize() / 5 ));
            g2d.setColor(DrawLocationsPanel.DEFAULT_LINE_COLOR);
                      
            g2d.setColor(DrawLocationsPanel.DEFAULT_STRING_COLOR);            
            
            g2d.setFont(new Font("Default", 0, res.getSize()));            
            g2d.drawString(
                    res.getPlace().name,
                    actualLocationPositionRelativeToScreen.x,
                    actualLocationPositionRelativeToScreen.y
                    );
            
            if(res.getSize() == Place.HOVER_SIZE) {
                g2d.setColor(DEFAULT_PATH_HOVER_COLOR);
            }else {
                g2d.setColor(DEFAULT_LINE_COLOR);
            }
            
            /*
             * For each place we draw it's path from origin
             */
            Point prevPointForPath = new Point(this.myPosition.getX().intValue(), this.myPosition.getY().intValue());
            
            for(LatLng pointCoordinates : res.getPath()) {
                Pair<Double, Double> singlePointIncrement = this.getPointsFromCoordinate(this.myPosition.getPosition(), pointCoordinates);
                
                Point singlePointActualLocationPositionRelativeToImage = new Point(
                        (int) (this.myPosition.getX() + singlePointIncrement.first),
                        (int) (this.myPosition.getY() - singlePointIncrement.second)
                        );
                
                g2d.drawLine(
                        prevPointForPath.x,
                        prevPointForPath.y,
                        singlePointActualLocationPositionRelativeToImage.x, 
                        singlePointActualLocationPositionRelativeToImage.y
                        );      
                
                prevPointForPath = singlePointActualLocationPositionRelativeToImage;  
            }
            
            g2d.drawLine(
                    prevPointForPath.x,
                    prevPointForPath.y,
                    actualLocationPositionRelativeToScreen.x, 
                    actualLocationPositionRelativeToScreen.y
                    );
            
            res.setSize(Place.DEFAULT_SIZE);
        });
        
    }
    
    public Optional<Place> getPlaceNearPoint(final int x,final int y) {
        double MAX_DIST = MAXIMUM_DISTANCE_IN_PX_TO_HOVER; // px

        return this.places.stream()
                .filter(p -> Math.pow(p.getX() - x, 2) + Math.pow(p.getY() - y, 2) < MAX_DIST)
                .min((p1, p2) -> (int)((Math.pow(p1.getX() - x, 2) + Math.pow(p1.getY() - y, 2) - (Math.pow(p2.getX() - x, 2) + Math.pow(p2.getY() - y, 2)))));
    }

    private Pair<Double, Double> getPointsFromCoordinate(LatLng p1, LatLng p2) {

        final double distanceInMeter = this.calculateDistanceInMeter(p1, p2);
        final double distanceFromMyPosition = (distanceInMeter / metersPerPixel); //gets the distance between two points in pixels

        final double angleFromMyPosition = this.calculateAngleFromCoordinate(p1, p2);
        final double incrementX = Math.cos(Math.toRadians(angleFromMyPosition)) * distanceFromMyPosition;
        final double incrementY = Math.sin(Math.toRadians(angleFromMyPosition)) * distanceFromMyPosition;

        return new Pair<Double, Double>(incrementX, incrementY);

    }

    private double calculateDistanceInMeter(LatLng p1, LatLng p2) {

        double lat1 = p1.lat;
        double lng1 = p1.lng;
        double lat2 = p2.lat;
        double lng2 = p2.lng;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) 
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (EARTH_RADIUS * c);

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
        brng = (brng + FULL_CIRCLE) % FULL_CIRCLE;
        brng = FULL_CIRCLE - brng  + 90; // count degrees counter-clockwise - remove to make clockwise

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
