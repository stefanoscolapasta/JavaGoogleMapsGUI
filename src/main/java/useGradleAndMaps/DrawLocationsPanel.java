package useGradleAndMaps;

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
    private static final int PROPORTIONAL_MULTIPLIER = 10_000_000 / 90 / 4;
    private Point CentralPoint;
    private final static Color DEFAULT_NODE_COLOR = Color.RED;
    private final static Color DEFAULT_LINE_COLOR = Color.ORANGE;
    private final static Color DEFAULT_STRING_COLOR = Color.WHITE;
    private ImageIcon backgroundImage = new ImageIcon();
    
    
    private final double widthInMeter = (40_000/Math.pow(2,  15)) * 2 * 1_000;
    private final double heightInMeter = (40_000/Math.pow(2,  15)) * 2 * 1_000;
    
    public void setResults(Pair <List<PlacesSearchResult>, LatLng> results, ImageResult geoImageRes) {
        this.results = results.first;
        this.myCoordinates = this.transformedCoordinates(results.second); 
        this.backgroundImage = new ImageIcon(geoImageRes.imageData);
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
        

        
        final double realWidthPerPixel = this.widthInMeter / (double)scaledImageDimension.width;
        final double realHeightPerPixel = this.heightInMeter / (double)scaledImageDimension.height;
        
        System.out.println("realWidthPerPixel = " + realWidthPerPixel);
        System.out.println("realHeightPerPixel = " + realHeightPerPixel);
        
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
            
            //System.out.println("LOCATION NEAR ---> name: " + res.name + " " + res.geometry.location + " ");
            LatLng whereToPlaceLocationOnPanel = calculateVectorDifference(myCoordinates, res.geometry.location);
            
            
            Point actualLocationPositionRelativeToScreen = new Point(
                    (int)((this.CentralPoint.x - (whereToPlaceLocationOnPanel.lng))),
                    (int)((this.CentralPoint.y - (whereToPlaceLocationOnPanel.lat)))
                    );
            /*
            Dimension scaledPointCoordinatesComparedToImage = this.getScaledDimension(
                    new Dimension(
                            actualLocationPositionRelativeToScreen.x,
                            actualLocationPositionRelativeToScreen.y),
                    scaledImageDimension
                    );
            */
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
    
    private LatLng calculateVectorDifference(final Point pivotLocation, final LatLng C2) {
        return new LatLng(
                (int)( (-pivotLocation.y) + transformedCoordinates(C2).y), //La latitudine sono le Y la long le X 
                (int)((pivotLocation.x) - transformedCoordinates(C2).x));
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
