package corregirpecs;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import corregirpecs.model.Opcio;
import corregirpecs.model.OpcioSol;
import corregirpecs.model.PreguntaSol;
import corregirpecs.model.Solucio;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class AnalitzarController implements Initializable {
    @FXML
    private TableView<PreguntaSol> preguntas;
    @FXML
    private TableColumn<PreguntaSol, Boolean> anulCol;
    @FXML
    private TableColumn<PreguntaSol, String> nomCol;
    @FXML
    private TableView<OpcioSol> respostes;
    @FXML
    private TableColumn<OpcioSol, String> respCol;
    @FXML
    private TableColumn<OpcioSol, String> pctCol;
    @FXML
    private TableColumn<OpcioSol, Boolean> corrCol;
    @FXML
    private TableColumn<OpcioSol, Boolean> solCol;

    private ArrayList<Solucio> sol;
    
    final ObservableList<PreguntaSol> pregs= FXCollections.observableArrayList();
    final ObservableList<OpcioSol> resps= FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // configurar taula de preguntes
        this.preguntas.setEditable(true);
        this.preguntas.setItems(this.pregs);
        this.nomCol.setCellValueFactory(new PropertyValueFactory<>("Nom"));        
        // checkbox anul·lar
        this.anulCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PreguntaSol, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<PreguntaSol, Boolean> param) {
                PreguntaSol p = param.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(p.getAnulada());
                // When "Anul·lar" column change.
                booleanProp.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                            Boolean newValue) { 
                        p.setAnulada(newValue);
                        p.setChanged(true);
                    }
                });
                return booleanProp;
            }
        });
        this.anulCol.setCellFactory(new Callback<TableColumn<PreguntaSol, Boolean>, TableCell<PreguntaSol, Boolean>>() {
            @Override
            public TableCell<PreguntaSol, Boolean> call(TableColumn<PreguntaSol, Boolean> p) {
                CheckBoxTableCell<PreguntaSol, Boolean> cell = new CheckBoxTableCell<PreguntaSol, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        // canvi de pregunta
        this.preguntas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            	this.SetOpcions(newSelection.getNom());
            }
        });

        // configurar taula de respostes
        this.respostes.setEditable(true);
        this.respostes.setItems(this.resps);
        this.respCol.setCellValueFactory(new PropertyValueFactory<>("Valor"));
        this.pctCol.setCellValueFactory(new PropertyValueFactory<>("PCT"));
        // checkbox correcte
        this.corrCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OpcioSol, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<OpcioSol, Boolean> param) {
                OpcioSol o = param.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(o.getCorrecte());
                // When "Anul·lar" column change.
                booleanProp.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                            Boolean newValue) { 
                        o.setCorrecte(newValue);
                        o.setChanged(true);
                    }
                });
                return booleanProp;
            }
        });
        this.corrCol.setCellFactory(new Callback<TableColumn<OpcioSol, Boolean>, TableCell<OpcioSol, Boolean>>() {
            @Override
            public TableCell<OpcioSol, Boolean> call(TableColumn<OpcioSol, Boolean> p) {
                CheckBoxTableCell<OpcioSol, Boolean> cell = new CheckBoxTableCell<OpcioSol, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        // checkbox solucio
        this.solCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<OpcioSol, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<OpcioSol, Boolean> param) {
                OpcioSol o = param.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(o.getSolucio());
                // When "Anul·lar" column change.
                booleanProp.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue,
                            Boolean newValue) { 
                        o.setSolucio(newValue);
                        o.setChanged(true);
                    }
                });
                return booleanProp;
            }
        });
        this.solCol.setCellFactory(new Callback<TableColumn<OpcioSol, Boolean>, TableCell<OpcioSol, Boolean>>() {
            @Override
            public TableCell<OpcioSol, Boolean> call(TableColumn<OpcioSol, Boolean> p) {
                CheckBoxTableCell<OpcioSol, Boolean> cell = new CheckBoxTableCell<OpcioSol, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
    }
    
    public void SetOpcions(String p) {
    	this.resps.clear();
    	// obtenir la solució escollida
    	for (Solucio s: this.sol) {
    		if (s.pregunta.equals(p)) {
    			// carregar les opcions
    			for (Opcio o: s.opcions) {
    				this.resps.add(new OpcioSol(o));
    			}
    		}
    	}
    }
    
    public void SetData(ArrayList<Solucio> sols) {
        this.sol = sols;
        
        for (Solucio s: this.sol) {
        	PreguntaSol p = new PreguntaSol();
        	p.setNom(s.pregunta);
            this.pregs.add(p);
        }
        
        this.preguntas.requestFocus();
        this.preguntas.getSelectionModel().select(0);
        this.preguntas.getFocusModel().focus(0);
    }
    
}
