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
    private final JButton tGetNear;
    private DrawLocationsPanel drawLocations;
    private final MapsHandlerRequest mapsHandler;
    
    public Gui() throws ApiException, InterruptedException, IOException {
        this.mapsHandler = new MapsHandlerRequest();
        this.frame = new MyFrame("Maps");
        this.drawLocations = new DrawLocationsPanel();
        this.pMain = new JPanel(new BorderLayout());
        this.pTextPanel = new JPanel(new FlowLayout());
        this.pTextPanel.setSize(15, 3);
        this.pSearchPanel = new JPanel(new FlowLayout());
        
        this.pResult = new JTextArea();
        this.pResult.setSize(10, 2);
        this.pTextPanel.setBorder(new TitledBorder("Select origin and destination"));

        this.tOrigin = new JTextField("Via Fornaci 7, Cesena",10);
        this.tDestination = new JTextField("Pasticceria",10);
        this.pTextPanel.add(this.tOrigin);
        this.pTextPanel.add(this.tDestination);
        this.pTextPanel.add(this.pResult);

        this.tGetNear = new JButton("Get near locations"); 
        this.pMain.add(this.drawLocations, BorderLayout.CENTER); 
        this.tGetNear.addActionListener(e -> {
                try {
                    Pair<List<PlacesSearchResult>, LatLng> results = Gui.this.mapsHandler.getTimeTravel(Gui.this.tOrigin.getText(), Gui.this.tDestination.getText(), 1);
                    System.out.println("CHECCKA1 QUI-->" + results.first);
                    Gui.this.drawLocations.setResults(results);  
                    System.out.println("CHECCKA2 QUI-->" + results.first);
                    
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
        
        this.pSearchPanel.add(this.tGetNear);
        this.pMain.add(this.pTextPanel, BorderLayout.NORTH);
        this.pMain.add(this.pSearchPanel, BorderLayout.SOUTH);  
        this.frame.add(this.pMain);
        this.frame.refresh();      
    }
    
    private void showMapsError(String message) {
        this.pResult.setText(message);
    }
}
