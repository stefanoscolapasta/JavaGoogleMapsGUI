package useGradleAndMaps;

import java.awt.BorderLayout;import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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
    private final MapsHandlerRequest mapsHandler;
    
    public Gui() throws ApiException, InterruptedException, IOException {
        this.mapsHandler = new MapsHandlerRequest();
        this.frame = new MyFrame("Maps");
        
        this.pMain = new JPanel(new BorderLayout());
        this.pTextPanel = new JPanel(new FlowLayout());
        this.pSearchPanel = new JPanel(new FlowLayout());
        
        this.pResult = new JTextArea();  
        this.pTextPanel.setBorder(new TitledBorder("Select origin and destination"));

        this.tOrigin = new JTextField(10);
        this.tDestination = new JTextField(10);
        this.pTextPanel.add(this.tOrigin);
        this.pTextPanel.add(this.tDestination);
        this.pTextPanel.add(this.pResult);
        
        this.tGetDistance = new JButton("Get distance");
        
        this.tGetDistance.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Gui.this.pResult.setText(Gui.this.mapsHandler.getDistance(Gui.this.tOrigin.getText(), Gui.this.tDestination.getText()));
                } catch (ApiException e) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Try to be a little more specific");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchElementException e) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Insert both origin and destination");
                }
                
                
            }
        });
        
        this.tGetTimeTravel = new JButton("Get travel time");  
        this.tGetTimeTravel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                try {
                    Gui.this.pResult.setText(Gui.this.mapsHandler.getTimeTravel(Gui.this.tOrigin.getText(), Gui.this.tDestination.getText()));
                } catch (ApiException e) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Try to be a little more specific");
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (NoSuchElementException e) {
                    // TODO Auto-generated catch block
                    Gui.this.showMapsError("Insert both origin and destination");
                }
                
            }
        });
        
        this.pSearchPanel.add(this.tGetDistance);
        this.pSearchPanel.add(this.tGetTimeTravel);
        this.pMain.add(this.pTextPanel, BorderLayout.NORTH);
        this.pMain.add(this.pSearchPanel, BorderLayout.CENTER);  
        this.frame.add(this.pMain);
        
    }
    
    private void showMapsError(String message) {
        this.pResult.setText(message);
    }
}
