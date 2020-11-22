package useGradleAndMaps;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.errors.NotFoundException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;

public class Gui {
    private final MyFrame frame; 
    private final JPanel pMain;
    private final JPanel pTextPanel;
    private final JTextArea pResult;
    private final JPanel pSearchPanel;
    private final JTextField tOrigin;
    private final JTextField tDestination;
    private final JButton tGetDistance;
    private final JButton tGetTimeTravel;
    private final JButton tGetNear;
    private DrawLocationsPanel drawLocations;
    private final MapsHandlerRequest mapsHandler;
    
    public Gui() throws ApiException, InterruptedException, IOException {
        this.mapsHandler = new MapsHandlerRequest();
        this.frame = new MyFrame("Maps");
        
        this.pMain = new JPanel(new BorderLayout());
        this.pTextPanel = new JPanel(new FlowLayout());
        this.pTextPanel.setSize(15, 3);
        this.pSearchPanel = new JPanel(new FlowLayout());
        
        this.pResult = new JTextArea();
        this.pResult.setSize(10, 2);
        this.pTextPanel.setBorder(new TitledBorder("Select origin and destination"));

        this.tOrigin = new JTextField(10);
        this.tDestination = new JTextField(10);
        this.pTextPanel.add(this.tOrigin);
        this.pTextPanel.add(this.tDestination);
        this.pTextPanel.add(this.pResult);
        
        this.tGetDistance = new JButton("Get distance");       
        this.tGetDistance.addActionListener(e -> {
                try {
                    Gui.this.pResult.setText(Gui.this.mapsHandler.getDistance(Gui.this.tOrigin.getText(), Gui.this.tDestination.getText()));
                } catch (ApiException a) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Try to be a little more specific");
                } catch (InterruptedException a) {
                    // TODO Auto-generated catch block
                    a.printStackTrace();
                } catch (IOException a) {
                    // TODO Auto-generated catch block
                    a.printStackTrace();
                } catch (NoSuchElementException a) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Insert both origin and destination");
                }
        });
        
        this.tGetTimeTravel = new JButton("Get travel time");  
        this.tGetTimeTravel.addActionListener(e -> {
                try {
                    Gui.this.pResult.setText(Gui.this.mapsHandler.getTimeTravel(Gui.this.tOrigin.getText(), Gui.this.tDestination.getText()));
                } catch (ApiException a) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Try to be a little more specific");
                } catch (InterruptedException a) {
                    // TODO Auto-generated catch block
                    a.printStackTrace();
                } catch (IOException a) {
                    // TODO Auto-generated catch block
                    a.printStackTrace();
                } catch (NoSuchElementException a) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Insert both origin and destination");
                }
                
        });
        
        this.tGetNear = new JButton("Get near");       
        this.tGetNear.addActionListener(e -> {
                try {
                    Pair<List<PlacesSearchResult>, LatLng> results = Gui.this.mapsHandler.getTimeTravel(Gui.this.tOrigin.getText(), Gui.this.tDestination.getText(), 1);
                    Gui.this.drawLocations = new DrawLocationsPanel(results);
                    this.pMain.add(Gui.this.drawLocations, BorderLayout.CENTER);
                    Gui.this.drawLocations.repaint();
                } catch (ApiException a) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Try to be a little more specific");
                } catch (InterruptedException a) {
                    // TODO Auto-generated catch block
                    a.printStackTrace();
                } catch (IOException a) {
                    // TODO Auto-generated catch block
                    a.printStackTrace();
                } catch (NoSuchElementException a) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Insert both origin and destination");
                }
        });
        
        this.pSearchPanel.add(this.tGetDistance);
        this.pSearchPanel.add(this.tGetTimeTravel);
        this.pSearchPanel.add(this.tGetNear);
        this.pMain.add(this.pTextPanel, BorderLayout.NORTH);
        this.pMain.add(this.pSearchPanel, BorderLayout.SOUTH);  
        this.frame.add(this.pMain);
        
    }
    
    private void showMapsError(String message) {
        this.pResult.setText(message);
    }
}
