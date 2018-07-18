package corregirpecs;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import corregirpecs.model.Nota;
import corregirpecs.model.PEC;
import corregirpecs.model.Pregunta;
import corregirpecs.model.Pregunta.Tipo;
import corregirpecs.model.Resposta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class NotesController implements Initializable {
    
	@FXML
    private TableView<Nota> notas;
    @FXML
    private TableColumn<Nota, String> dniCol;
    @FXML
    private TableColumn<Nota, Float> notaCol;
    @FXML
    private TextField any;
    @FXML
    private TextField curs;
    @FXML
    private TextField pec;

    private String dir;
    private ArrayList<PEC> PECs;
    private ArrayList<Pregunta> Solucio;
    
    final ObservableList<Nota> data = FXCollections.observableArrayList();

    private final String C_SEP = ",";
    private final String C_DELIM = "'";
    private final String C_SOLUCIO = "solucio.txt";
    private final String C_DADES = "dades.txt";
    private final String C_NOTES = "notes.txt";
    
    private final String C_ERROR = "Error";
    private final String C_CONFIRM = "Confirmació";
    private final String C_DADES_OK = "Són correctes les dades d'any, curs i PEC?";
    private final String C_ARXIUS_EXPORTATS = "Arxius exportats";
    private final String C_FINAL = "Procés finalitzat";

    public void initialize(URL url, ResourceBundle rb) {
        this.notas.setEditable(false);
        this.notas.setItems(this.data);
        this.dniCol.setCellValueFactory(new PropertyValueFactory<>("DNI"));
        this.notaCol.setCellValueFactory(new PropertyValueFactory<>("Nota"));
	}
    
    @FXML
    void pbExportar(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle(C_CONFIRM);
		alert.setHeaderText(null);
		alert.setContentText(C_DADES_OK);
		
		ButtonType buttonYes = new ButtonType("Sí");
		ButtonType buttonNo = new ButtonType("No");

		alert.getButtonTypes().setAll(buttonYes, buttonNo);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonYes){
		    String pref = this.any.getText() + "_" + this.curs.getText() + "_PEC" + this.pec.getText();
		    
	        // SOLUCIO
		    List<String> s = new ArrayList<>();
	        s.add("id,eti,tipres,rescor,val,numopc");
	        for (Pregunta p : this.Solucio) {
	        	// nº & nom
	        	String c = String.valueOf(p.num) + C_SEP + C_DELIM + p.nom + C_DELIM; 
	        	// tipus
	        	switch (p.tipo) {
		        case LLIURE: c = c + C_SEP + "1";
		        	break;
		        case TEST: c = c + C_SEP + "2";
		        	break;
		        case NUMERICA:  c = c + C_SEP + "3";
	        		break;
	        	}
	        	// solucions
	        	c = c + C_SEP + C_DELIM + p.getSolucions() + C_DELIM;
	        	// pes
	    		NumberFormat formatter = new DecimalFormat("0.0000");
	    		c = c + C_SEP + formatter.format(p.w).replace(",", ".");
	    		// nº opcions (si tipus test)
	    		c = c + C_SEP + (p.tipo.equals(Tipo.TEST) ? String.valueOf(p.numopc): "null");
	        	
	        	s.add(c);
	    	}
	        
	        // DADES
		    List<String> d = new ArrayList<>();
	        for (PEC p : this.PECs) {
	        	// header: ape1, ape2, nom, dni & honor
                String c = C_DELIM + p.ape1 + C_DELIM + C_SEP + C_DELIM + p.ape2 + C_DELIM + C_SEP +
                        C_DELIM + p.nom + C_DELIM + C_SEP + C_DELIM + p.dni + C_DELIM;
                if (p.honor!=null) c = c + C_SEP + (p.honor ? "1" : "0");
	        	// respostes dels alumnes
                for (Resposta r : p.resp) {
                	c = c + C_SEP + C_DELIM + r.resposta + C_DELIM;
                }
                
	        	d.add(c);
	    	}
	        
	        // NOTES
	        NumberFormat formatter = new DecimalFormat("#0.0");
		    List<String> n = new ArrayList<>();
		    n.add("nif,idc,anc,nuc,numpec,nota");
	        for (PEC p : this.PECs) {
	        	// NUC = 1 fijo
	        	String c = C_DELIM + p.dni + C_DELIM + C_SEP + 
	        			C_DELIM + this.curs.getText() + C_DELIM + C_SEP +
	        			C_DELIM + this.any.getText().substring(2, 4) + C_DELIM + C_SEP +
	        			"1" + C_SEP +
	        			this.pec.getText() + C_SEP +
	        			formatter.format(p.nota).replace(",", ".");
                
	        	n.add(c);
	    	}
	        
	        // write files
	        try {
	        	Files.write(Paths.get(this.dir + File.separator + pref + "_" + C_SOLUCIO), s, Charset.forName("UTF-8"));
	        	Files.write(Paths.get(this.dir + File.separator + pref + "_" + C_DADES), d, Charset.forName("UTF-8"));
	        	Files.write(Paths.get(this.dir + File.separator + pref + "_" + C_NOTES), n, Charset.forName("UTF-8"));
	        	

				File f = new File(this.dir + File.separator + pref + ".zip");
				ZipOutputStream out = new ZipOutputStream(new FileOutputStream(f));
				
				ZipEntry e = new ZipEntry(pref + "_" + C_SOLUCIO);
				out.putNextEntry(e);
				byte[] data = Files.readAllBytes(Paths.get(this.dir + File.separator + pref + "_" + C_SOLUCIO));
                out.write(data, 0, data.length);
				out.closeEntry();

				e = new ZipEntry(pref + "_" + C_DADES);
				out.putNextEntry(e);
				data = Files.readAllBytes(Paths.get(this.dir + File.separator + pref + "_" + C_DADES));
                out.write(data, 0, data.length);
				out.closeEntry();
				
				e = new ZipEntry(pref + "_" + C_NOTES);
				out.putNextEntry(e);
				data = Files.readAllBytes(Paths.get(this.dir + File.separator + pref + "_" + C_NOTES));
                out.write(data, 0, data.length);
				out.closeEntry();
				
				out.close();
	        } catch (Exception e) {
	        	ShowAlert(e.getMessage(),C_ERROR,AlertType.ERROR);
	        }

        	ShowAlert(C_ARXIUS_EXPORTATS,C_FINAL,AlertType.INFORMATION);
		}
	}
	
    public void SetData(ArrayList<PEC> PECs, ArrayList<Pregunta> Solucio, String anc, String dir) {
        this.dir = dir;
        this.PECs = PECs;
        this.Solucio = Solucio;
    	
    	for (PEC p: this.PECs) {
        	this.data.add(new Nota(p.dni,p.nota));
        }
        
        this.notas.requestFocus();
        this.notas.getSelectionModel().select(0);
        this.notas.getFocusModel().focus(0);
        
        if (!anc.isEmpty()) {
	        String[] data = anc.split(";");
	        this.curs.setText(data[0]);
	        this.pec.setText(data[1]);
	        this.any.setText(data[2]);
        }
    }
    
    public void ShowAlert(String message, String title, AlertType type) {
    	Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
