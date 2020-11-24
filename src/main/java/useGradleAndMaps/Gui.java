package useGradleAndMaps;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import com.google.maps.ImageResult;
import com.google.maps.errors.ApiException;
import com.google.maps.model.LatLng;
import com.google.maps.model.PlacesSearchResult;

public class Gui {
    private final MyFrame frame;
    private final JPanel pMain;
    private final JPanel pTextPanel;
    private final JTextArea pResult;
    private final JPanel pSearchPanel;
    private final JTextField tOrigin;
    private final JTextField tDistance;
    private final JTextField tDestination;
    private final JButton tGetNear;
    private DrawLocationsPanel drawLocations;
    private final MapsHandlerRequest mapsHandler;

    public Gui() throws ApiException, InterruptedException, IOException {
        this.mapsHandler = new MapsHandlerRequest();

        this.frame = new MyFrame("Maps");
        this.drawLocations = new DrawLocationsPanel(mapsHandler);

        this.pMain = new JPanel(new BorderLayout());
        this.pTextPanel = new JPanel(new FlowLayout());
        this.pTextPanel.setSize(15, 3);
        this.pSearchPanel = new JPanel(new FlowLayout());

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
        this.tDistance = new JTextField("1", 25);
        pRadiusPanel.add(this.tDistance, BorderLayout.CENTER);

        this.pTextPanel.add(pOriginPanel);
        this.pTextPanel.add(pPlaceOfInterestPanel);
        this.pTextPanel.add(pRadiusPanel);
        this.pTextPanel.add(this.pResult);

        this.tGetNear = new JButton("Get near locations");
        this.pMain.add(this.drawLocations, BorderLayout.CENTER);
        this.tGetNear.addActionListener(e -> {

            try {
                Pair<List<PlacesSearchResult>, LatLng> results = Gui.this.mapsHandler.getTimeTravel(
                        Gui.this.tOrigin.getText(), Gui.this.tDestination.getText(),
                        Integer.valueOf(Gui.this.tDistance.getText()));
                System.out.println("CHECCKA1 QUI-->" + results.first);
                ImageResult geoImageRes = Gui.this.mapsHandler.getGeoImageAtCoordinates(results.second);
                Gui.this.drawLocations.setResults(results, geoImageRes, Gui.this.mapsHandler.getImageScaleValue());
                System.out.println("CHECCKA2 QUI-->" + results.first);

                Gui.this.drawLocations.repaint();
            } catch (ApiException a) {
                // TODO Auto-generated catch block
                Gui.this.showMapsError("Try to be a little more specific");
            } catch (InterruptedException a) {
                // TODO Auto-generated catch block
                a.printStackTrace();
            } catch (NoSuchElementException a) {
                // TODO Auto-generated catch block
                Gui.this.showMapsError("Insert both origin and destination");
            } catch (NumberFormatException a) {
                Gui.this.showMapsError("Insert a valid radius in km (<50)");
            } catch (IOException a) {
                // TODO Auto-generated catch block
                a.printStackTrace();
            }
        });

        this.drawLocations.addMouseWheelListener(new MouseWheelListener() {
            
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                // TODO Auto-generated method stub
                double change = e.getPreciseWheelRotation();
                if (change < 0) {
                    mapsHandler.setZoom(mapsHandler.getZoom() + 1);
                    System.out.println("Zoom In");
                }else {
                    mapsHandler.setZoom(mapsHandler.getZoom() - 1);
                    System.out.println("Zoom Out");
                }
                
                Gui.this.drawLocations.refreshImage();
                Gui.this.drawLocations.repaint();
            }
        });

        this.drawLocations.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseMoved(MouseEvent e) {
                Place p = Gui.this.drawLocations.getPlaceNearPoint(e.getX(), e.getY());

                if (p != null) {
                    System.out.println(p.getPlace().name);
                    p.setSize(Place.HOVER_SIZE);

                }

                Gui.this.drawLocations.repaint();

            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }
        });

        this.pSearchPanel.add(this.tGetNear);
        this.pSearchPanel.revalidate();
        this.pTextPanel.revalidate();
        this.pMain.add(this.pTextPanel, BorderLayout.NORTH);
        this.pMain.add(this.pSearchPanel, BorderLayout.SOUTH);
        this.pMain.revalidate();
        this.frame.add(this.pMain);
        this.frame.refresh();
        this.frame.revalidate();
    }

    private void showMapsError(String message) {
        this.pResult.setText(message);
    }
}
