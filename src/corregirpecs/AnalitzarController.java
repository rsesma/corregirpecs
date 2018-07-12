package corregirpecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;

import corregirpecs.model.Item;
import corregirpecs.model.Opcio;
import corregirpecs.model.OpcioSol;
import corregirpecs.model.PEC;
import corregirpecs.model.Pregunta;
import corregirpecs.model.Pregunta.Tipo;
import corregirpecs.model.PreguntaSol;
import corregirpecs.model.Resposta;
import corregirpecs.model.Solucio;
import corregirpecs.model.Stat;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

public class AnalitzarController implements Initializable {
    
    @FXML
    private TextField dir;

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
    
    private final String C_ANALISI = "analisi.txt";
    private final String C_SOL = "sol.txt";
    private final String C_DADES_PECS = "dades_pecs.txt";
    private final String C_ZIP = "*.zip";
    private final String C_PDF = "*.pdf";
    private final String C_PDFS = "PDFs";
    private final String C_PROBLEMES = "problemes";
    
    //private final String C_DEFDIR = System.getProperty("user.home");
    //private final String C_DEFDIR = "/Users/r/Desktop/CorregirPECs/2017-18_PEC4_DE0";
    private final String C_DEFDIR = "/home/drslump/Escritorio/CorregirPECs/2017-18_PEC4_DE0";
    
    
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
        
        // TEST
        Boolean ltest = true;
        if (ltest) {
	        this.dir.setText(C_DEFDIR);
	        Collection<File> files = FileUtils.listFiles(new File(dir.getText()), new WildcardFileFilter("analisi.txt"), null);
	        this.CarregaAnalisi(files.iterator().next());
        }
    }
        
    @FXML
    void getDir(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta de treball");
        directoryChooser.setInitialDirectory(new File(C_DEFDIR));
        File folder = directoryChooser.showDialog(null);
        if (folder != null) {
        	dir.setText(folder.getAbsolutePath());
        	Collection<File> files = FileUtils.listFiles(new File(dir.getText()), new WildcardFileFilter(C_ANALISI), null);
        	if (!files.isEmpty() && files.size() == 1) this.CarregaAnalisi(files.iterator().next());
        	else {
            	this.pregs.clear();
            	this.resps.clear();        		
        	}
        } else {
        	dir.setText("");
        }
    }

    @FXML
    public void mnuDescAnalitza(ActionEvent event) {
    	if (this.Descomprimir()) {
    		this.Analitzar();
    	}
    }
    
    @FXML
    public void mnuGraba(ActionEvent event) {
    	if (this.CheckDir()) {
    		this.Graba();
    	}
    }
    
    @FXML
    public void mnuNotes(ActionEvent event) {
    	if (this.CheckDir()) {
            File folder = new File(dir.getText());
    		File pecs = new File(dir.getText(),C_PDFS);
            Collection<File> files = FileUtils.listFiles(folder, new WildcardFileFilter(C_SOL), null);

            if (files.isEmpty()) {
            	ShowAlert("No es troba l'arxiu sol.txt","Error",AlertType.ERROR);
            } else {
            	// SOLUCIÓ: arxiu sol.txt 
            	ArrayList<Pregunta> Plantilla = GetPlantilla(files.iterator().next());
                	
                // DADES: si no s'han extret del PDF, extreure-les
                files = FileUtils.listFiles(folder, new WildcardFileFilter(C_DADES_PECS), null);
                if (files.isEmpty()) GetDadesPECs(folder, pecs, Plantilla);
                
                // PECS: respostes de les PECs dels alumnes
                ArrayList<PEC> PECs = new ArrayList<PEC>();
            	try {
            		// obtenir les dades de cada pec, fila a fila
            		LineIterator it = FileUtils.lineIterator(new File(folder.getAbsolutePath() + File.separator + C_DADES_PECS), "UTF-8");
                	try {
                	    while (it.hasNext()) {
                	    	String line = it.nextLine();
                	    	PECs.add(new PEC(Plantilla, line));
                	    }
                	} catch (Exception e) {
                    	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
                    } finally {
                	    it.close();
                	}
            	} catch (Exception e) {
                	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
                }
            	
            	// carregar les solucions a cada pregunta de la plantilla
            	for (Pregunta p : Plantilla) {
            		for (Solucio s : this.sol) {
            			if (s.pregunta.equals(p.nom)) {
            				p.SetSolucio(s);
            				break;
            			}
            		}
            	}
            	
            	for (PEC p : PECs) {
            		System.out.println(p.dni);
            		for (Resposta r : p.resp) {
            			System.out.println(r.pregunta);
            			System.out.println(r.resposta);
            			for (Opcio o : r.pregunta.sol.opcions) {
            				if (o.correcte) System.out.println("sol: " + o.value);
            			}
            		}
            		break;
            	}
            }
    	}
    	
    }
    
    public boolean Descomprimir() {
		Boolean lreturn = false;
		
    	if (this.CheckDir()) {
			// obtenir l'arxiu zip
	        File directory = new File(dir.getText());
	        Collection<File> files = FileUtils.listFiles(directory, new WildcardFileFilter(C_ZIP), null);
	                    
	        if (files.isEmpty()) {
	        	ShowAlert("No es troba l'arxiu comprimit","Error",AlertType.ERROR);
	        } else if (files.size() > 1) {
	        	ShowAlert("Hi ha més d'un arxiu comprimit","Error",AlertType.ERROR);
	        } else {
	        	File file = files.iterator().next();				// arxiu ZIP
	        	File folder = new File(dir.getText(),C_PDFS);		// carpeta a on es descomprimeix el ZIP 
	        	
	        	try {
	            	// la carpeta es crea nova; si ja existeix, s'elimina
	            	if (folder.exists()) FileUtils.deleteDirectory(folder); 
	            	folder.mkdir();
	            	
	            	byte[] buffer = new byte[1024];
	                // get the zip file content
	                ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
	                //get the zipped file list entry
	                ZipEntry ze = zis.getNextEntry();
	                while (ze!=null) {
	                    String fileName = ze.getName();
	                    File newFile = new File(folder.getAbsolutePath() + File.separator + fileName);
	
	                    // create all non exists folders else you will hit FileNotFoundException for compressed folder
	                    new File(newFile.getParent()).mkdirs();
	
	                    FileOutputStream fos = new FileOutputStream(newFile);             
	                    int len;
	                    while ((len = zis.read(buffer)) > 0) {
	                        fos.write(buffer, 0, len);
	                    }
	                    fos.close();
	                    
	                    ze = zis.getNextEntry();
	                }
	                zis.closeEntry();
	                zis.close();
	                
	        		// comprovar els arxius descomprimits i generar problemes si necessari
	                Boolean lproblems = false;
	                File problems = new File(dir.getText(),C_PROBLEMES);		// carpeta a on es copien les PECs problemàtiques
	            	if (problems.exists()) FileUtils.deleteDirectory(problems); 
	                for (File f : folder.listFiles()) {
	        			// loop pels arxius no ocults
	        	        if (f.isFile() && !f.isHidden()) {
	        	        	// és un PDF?
	        	        	String ext = FilenameUtils.getExtension(f.getName());
	        	        	if (ext.equalsIgnoreCase("pdf")) {
	        	                PdfReader reader = new PdfReader(f.getAbsolutePath());
	        	                AcroFields form = reader.getAcroFields();
	        	                // té camps?
	        	                if (!(form.getFields().size()>0)) {
	        	                	FileUtils.moveFileToDirectory(f, problems, true);
	        	                	lproblems = true;
	        	                }
	        	        	} else {
	        	        		FileUtils.moveFileToDirectory(f, problems, true);
	        	        		lproblems = true;
	        	        	}
	        	        }
	        	    }
	        		
	        		if (lproblems) ShowAlert("Hi ha PECs amb problemes.\n\nVeure la carpeta problemes","Atenció",AlertType.WARNING);
	        		else ShowAlert("PECs descomprimides","Procés acabat",AlertType.INFORMATION);
	        		
	        		lreturn = true;
	            } catch (Exception e) {
	            	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
	            }
	        }
		}
    	
    	return lreturn;
	}
    
    public void Analitzar() {
        File folder = new File(dir.getText());
		File pecs = new File(dir.getText(),C_PDFS);
        Collection<File> files = FileUtils.listFiles(folder, new WildcardFileFilter(C_SOL), null);

        if (files.isEmpty()) {
        	ShowAlert("No es troba l'arxiu sol.txt","Error",AlertType.ERROR);
        } else {
        	// SOLUCIÓ: arxiu sol.txt 
        	ArrayList<Pregunta> Plantilla = GetPlantilla(files.iterator().next());
            	
            // DADES: si no s'han extret del PDF, extreure-les
            files = FileUtils.listFiles(folder, new WildcardFileFilter(C_DADES_PECS), null);
            if (files.isEmpty()) GetDadesPECs(folder, pecs, Plantilla);

            // PECS: respostes de les PECs dels alumnes
            ArrayList<PEC> PECs = new ArrayList<PEC>();
        	try {
        		// obtenir les dades de cada pec, fila a fila
        		LineIterator it = FileUtils.lineIterator(new File(folder.getAbsolutePath() + File.separator + C_DADES_PECS), "UTF-8");
            	try {
            	    while (it.hasNext()) {
            	    	String line = it.nextLine();
            	    	PECs.add(new PEC(Plantilla, line));
            	    }
            	} catch (Exception e) {
                	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
                } finally {
            	    it.close();
            	}
        	} catch (Exception e) {
            	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
            }

        	// obtenir l'estadística de les respostes per cada pregunta no lliure (numèriques o tipus test)
        	Stat st = new Stat(Plantilla);
        	for (PEC p : PECs) {
        		for (Resposta r: p.resp) {
        			if (r.pregunta.tipo != Tipo.LLIURE) st.getItem(r.pregunta.nom).Add(r.resposta);
        		}
        	}
            	
        	// obtenir totes les possibles solucions ordenades de major a menor percentatge
        	this.sol = new ArrayList<Solucio>();
        	for (Item i: st.items) {
        		this.sol.add(new Solucio(i,PECs.size()));
        	}
        	// defecte: l'opció amb més % d'aparació (la primera) és la correcta
        	for (Solucio s: this.sol) {
        		if (!s.esLliure) {
	            	Opcio o = s.opcions.get(0);
	            	o.correcte = true;
	            	o.solucio = true;
        		}
        	}
        	
        	this.Graba();		// grabar les dades a un arxiu txt al disc
        	
        	// carregar les llistes amb les dades
        	this.SetPreguntes();
    	}
    }
    
    public void Graba() {
		NumberFormat formatter = new DecimalFormat("##0.000");
    	if (this.CheckDir()) {
            List<String> lines = new ArrayList<>();
            for (Solucio s : this.sol) {
            	// nom de la pregunta + anulada?
            	String c = s.pregunta + ";" + (s.anulada ? "1" : "0") + ";";
            	if (!s.esLliure) {
	            	Boolean lfirst = true;
	            	for (Opcio o : s.opcions) {
	            		c = c + (lfirst ? "" : ",") + o.value + "\t" + formatter.format(o.pct).replace(",", ".");
	            		c = c + "\t" + (o.correcte ? "1" : "0") + "\t" + (o.solucio ? "1" : "0");
	            		lfirst = false;
	            	}
            	} else {
            		c = c + "0";
            	}
            	
            	lines.add(c);
            }
            
            // escriure l'arxiu analisi.txt
            try {
            	Files.write(Paths.get(dir.getText() + File.separator + C_ANALISI), lines, Charset.forName("UTF-8"));
            } catch (Exception e) {
            	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
            }
    	}
    }
    
    public void CarregaAnalisi(File f) {
    	this.sol = new ArrayList<Solucio>();
		try {
			// obtenir les files de la solució una a una
			LineIterator it = FileUtils.lineIterator(f, "UTF-8");
	    	try {
	    	    while (it.hasNext()) {
	    	    	String line = it.nextLine();
	    	    	String[] fields = line.split(";");
	    	    	Solucio s = new Solucio(null,0);
	    	    	s.pregunta = fields[0];
	    	    	s.anulada = (fields[1].equals("1"));
	    	    	s.esLliure = (fields[2].equals("0"));
	    	    	if (!s.esLliure) {
		    	    	String[] opcions = fields[2].split(",",-1);
		    	    	for (String op: opcions) {
		    	    		String [] v = op.split("\t",-1);
		    	    		Opcio o = new Opcio("",0,0);
		    	    		o.value = v[0];
		    	    		o.pct = Double.parseDouble(v[1]);
		    	    		o.correcte = (v[2].equals("1"));
		    	    		o.solucio = (v[3].equals("1"));
		    	    		s.opcions.add(o);
		    	    	}
	    	    	}
	    	    	this.sol.add(s);
	    	    }
	    	    
	    	    this.SetPreguntes();
	    	    this.SetOpcions(this.sol.get(0).pregunta);
	    	} catch (Exception e) {
	        	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
	        } finally {
	    	    it.close();
	    	}
	    } catch (Exception e) {
	    	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
	    }
    }

	public ArrayList<Pregunta> GetPlantilla(File f) {
		ArrayList<Pregunta> Plantilla = new ArrayList<Pregunta>();
		try {
			// obtenir les files de la solució una a una
			LineIterator it = FileUtils.lineIterator(f, "UTF-8");
	    	try {
	    		Boolean lfirst = true;
	    	    while (it.hasNext()) {
	    	    	String line = it.nextLine();
	    	    	if (!lfirst) Plantilla.add(new Pregunta(line));
	    	    	else lfirst = false;
	    	    }
	    	} catch (Exception e) {
	        	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
	        } finally {
	    	    it.close();
	    	}
	    } catch (Exception e) {
	    	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
	    }
		return Plantilla;
	}
	
    public void GetDadesPECs(File folder, File pecs, ArrayList<Pregunta> Plantilla) {
    	// loop per les PECs (arxius PDF) de la carpeta PDFs
    	Collection<File> pdfs = FileUtils.listFiles(pecs, new WildcardFileFilter(C_PDF), null);
        List<String> lines = new ArrayList<>();
        for (File f : pdfs) {
            if (f.isFile()) {
                String n = f.getName();
                String dni = n.substring(n.lastIndexOf("_")+1);
                dni = dni.substring(0,dni.indexOf("."));
                
                // obrir la PEC
                try {
                    PdfReader reader = new PdfReader(f.getAbsolutePath());
                    AcroFields form = reader.getAcroFields();
                    if (form.getFields().size()>0) {
                        // capçalera amb les dades identificatives
                        String c = dni;
                        try {
                        	String h = form.getField("HONOR");
                        	c = c + (h.equalsIgnoreCase("Yes") ? ",1" : ",0");
                        } catch (Exception e) {
                        	// no fer res: el camp honor no existeix perquè la PEC és presencial
                        }

                        // loop obtenint les dades dels camps
                        for (Pregunta p : Plantilla) {
                            c = c + ";" + form.getField(p.nom).replace(",", ".");
                        }
                        lines.add(c);
                    }	                        
                } catch (Exception e) {
                	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
                }
            }
        }
        
        // escriure l'arxiu dades_pecs.txt
        try {
        	Files.write(Paths.get(folder.getAbsolutePath() + File.separator + C_DADES_PECS), lines, Charset.forName("UTF-8"));
        } catch (Exception e) {
        	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
        }
    }
    
    
    public void SetOpcions(String p) {
    	this.resps.clear();
    	// obtenir la solució escollida
    	for (Solucio s: this.sol) {
    		if (s.pregunta.equals(p)) {
    			if (!s.esLliure ) {
	    			// carregar les opcions
	    			for (Opcio o: s.opcions) {
	    				this.resps.add(new OpcioSol(o, this.respostes));
	    			}
    			}
    		}
    	}
    }
    
    public void SetPreguntes() {
        for (Solucio s: this.sol) {
        	PreguntaSol p = new PreguntaSol(s);
             this.pregs.add(p);
        }
        
        this.preguntas.requestFocus();
        this.preguntas.getSelectionModel().select(0);
        this.preguntas.getFocusModel().focus(0);
    }
    
    public Boolean CheckDir() {
    	Boolean lreturn = false;
    	if (dir.getText().isEmpty()) {
    		ShowAlert("Indicar la carpeta de treball","Error",AlertType.ERROR);
    	} else {
    		File folder = new File(dir.getText());
    		lreturn = folder.exists(); 
    		if (!lreturn) ShowAlert("La carpeta de treball no existeix","Error",AlertType.ERROR);
    	}
    	return lreturn;
    }

    public void ShowAlert(String message, String title, AlertType type) {
    	Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }    
}
