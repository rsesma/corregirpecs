package corregirpecs;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import corregirpecs.model.PreguntaSol;
import corregirpecs.model.Solucio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AnalitzarController implements Initializable {
    @FXML
    private TableView<PreguntaSol> preguntas;
    //@FXML
    //private TableColumn<?, ?> anulCol;
    @FXML
    private TableColumn<PreguntaSol, String> nomCol;

    final ObservableList<PreguntaSol> pregs= FXCollections.observableArrayList();
    private ArrayList<Solucio> sol;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.preguntas.setEditable(true);
        
        // Set up the alumnos table
        this.nomCol.setCellValueFactory(new PropertyValueFactory<>("Nom"));
        
        this.preguntas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                System.out.println(newSelection.getNom());
            }
        });

        this.preguntas.setItems(this.pregs);
    }
    
    public void SetData(ArrayList<Solucio> sols) {
        this.sol = sols;
        
        int count = 0;
        for (Solucio s: this.sol) {
        	PreguntaSol p = new PreguntaSol();
        	p.setNom(s.pregunta);
            this.pregs.add(p);
            count = count+1;
        }
        
        this.preguntas.requestFocus();
        this.preguntas.getSelectionModel().select(0);
        this.preguntas.getFocusModel().focus(0);
    }
    
}
