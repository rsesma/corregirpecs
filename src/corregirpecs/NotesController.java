package corregirpecs;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import corregirpecs.model.Nota;
import corregirpecs.model.PEC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class NotesController implements Initializable {
    
	@FXML
    private TableView<Nota> notas;
    @FXML
    private TableColumn<Nota, String> dniCol;
    @FXML
    private TableColumn<Nota, Float> notaCol;

    final ObservableList<Nota> data = FXCollections.observableArrayList();
	
	@Override
    public void initialize(URL url, ResourceBundle rb) {
        this.notas.setEditable(false);
        this.notas.setItems(this.data);
        this.dniCol.setCellValueFactory(new PropertyValueFactory<>("DNI"));
        this.notaCol.setCellValueFactory(new PropertyValueFactory<>("Nota"));
	}
	
    public void SetData(ArrayList<PEC> PECs) {
        for (PEC p: PECs) {
        	this.data.add(new Nota(p.dni,p.nota));
        }
        
        this.notas.requestFocus();
        this.notas.getSelectionModel().select(0);
        this.notas.getFocusModel().focus(0);
    }
}
