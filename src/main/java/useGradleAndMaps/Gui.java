package useGradleAndMaps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;

public class Gui {
    private final MyFrame frame;
    private final JPanel pMain;
    private final JPanel pTextPanel;
    private final JTextArea pResult;
    private final JTextField tOrigin;
    private final JTextField tDestination;
    private final JButton tGetNear;
    private DrawLocationsPanel drawLocations;
    private final static int MAX_SEARCH_ZOOM_RADIUS = 50;
    private final MapsHandlerRequest mapsHandler;

    public Gui() throws ApiException, InterruptedException, IOException {
        this.mapsHandler = new MapsHandlerRequest();

        this.frame = new MyFrame("Maps");
        
        this.drawLocations = new DrawLocationsPanel(mapsHandler);

        this.pMain = new JPanel(new BorderLayout());
        this.pTextPanel = new JPanel(new FlowLayout());
        this.pTextPanel.setSize(15, 3);

        this.pResult = new JTextArea();
        this.pResult.setSize(10, 2);
        this.pTextPanel.setBorder(new TitledBorder("Select starting point and locations of interest"));

        JPanel pOriginPanel = new JPanel(new BorderLayout());
        pOriginPanel.setBorder(new TitledBorder("Select Origin here"));
        this.tOrigin = new JTextField("Via Fornaci 7, Cesena", 20);
        pOriginPanel.add(this.tOrigin, BorderLayout.CENTER);

        JPanel pPlaceOfInterestPanel = new JPanel(new BorderLayout());
        pPlaceOfInterestPanel.setBorder(new TitledBorder("What are you looking for nearby?"));
        this.tDestination = new JTextField("Sushi", 20);
        pPlaceOfInterestPanel.add(this.tDestination, BorderLayout.CENTER);

        JPanel pRadiusPanel = new JPanel(new BorderLayout());
        pRadiusPanel.setBorder(new TitledBorder("How far from you should I look for (KM)"));
        this.tGetNear = new JButton("Get near locations");
        this.pTextPanel.add(pOriginPanel);
        this.pTextPanel.add(pPlaceOfInterestPanel);
        this.pTextPanel.add(this.tGetNear);
       
        
        this.pMain.add(this.drawLocations, BorderLayout.CENTER);
        this.tGetNear.addActionListener(e -> {
            this.refreshNearLocationsInRadius();           
        });

        this.drawLocations.addMouseWheelListener(e -> {
                // TODO Auto-generated method stub
                double change = e.getPreciseWheelRotation();
                if (change < 0 && fromZoomValueToKmRadius(mapsHandler.getZoom()) < MAX_SEARCH_ZOOM_RADIUS) {
                    mapsHandler.setZoom(mapsHandler.getZoom() + 1);
                    this.refreshNearLocationsInRadius();  
                    System.out.println("Zoom In");
                    
                } else {
                    mapsHandler.setZoom(mapsHandler.getZoom() - 1);
                    this.refreshNearLocationsInRadius();  
                    System.out.println("Zoom Out");
                }
                Gui.this.drawLocations.repaint();
        });

        this.drawLocations.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Optional<Place> p = Gui.this.drawLocations.getPlaceNearPoint(e.getX(), e.getY());

                if (p.isPresent()) {
                    System.out.println(p.get().getPlace().name);
                    p.get().setSize(Place.HOVER_SIZE);
                }

                Gui.this.drawLocations.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }
        });

        
        this.pTextPanel.revalidate();
        this.pMain.add(this.pTextPanel, BorderLayout.NORTH);
        this.pMain.add(new JPanel(), BorderLayout.SOUTH);
        this.pMain.revalidate();
        this.frame.add(this.pMain);
        this.frame.refresh();
        this.frame.revalidate();
    }
    
    private void refreshNearLocationsInRadius() {
        try {
                Pair<List<PlacesSearchResult>, LatLng> results = this.mapsHandler.getTimeTravel(
                        this.tOrigin.getText(),
                        this.tDestination.getText(),
                        fromZoomValueToKmRadius(this.mapsHandler.getZoom()) //getting radius from zoom to query 
                        );
                
                System.out.println("CHECCKA1 QUI-->" + results.first);
                this.drawLocations.setResults(results);
                System.out.println("CHECCKA2 QUI-->" + results.first); 
                
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.drawLocations.refreshImage();
        this.drawLocations.repaint();
    }
    
    private int fromZoomValueToKmRadius(final int zoomValue) {
        return (int)(40_000 / Math.pow(2, zoomValue));
    }
}
