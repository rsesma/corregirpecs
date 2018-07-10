package corregirpecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import corregirpecs.model.PEC;
import corregirpecs.model.Pregunta;
import corregirpecs.model.Resposta;
import corregirpecs.model.Solucio;
import corregirpecs.model.Stat;
import corregirpecs.model.Pregunta.Tipo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CorregirController implements Initializable {

    @FXML
    private TextField dir;
    @FXML
    private CheckBox presencial;
    @FXML
    private CheckBox overwrite;

    //private final String C_DEFDIR = System.getProperty("user.home");
    private final String C_DEFDIR = "/Users/r/Desktop/CorregirPECs/2017-18_PEC4_DE0";
    //private final String C_DEFDIR = "/home/drslump/Escritorio/CorregirPECs/2017-18_PEC4_DE0";
    
    @FXML
    void getDir(ActionEvent event) {
        final DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta de treball");
        directoryChooser.setInitialDirectory(new File(C_DEFDIR));
        File folder = directoryChooser.showDialog(null);
        if (folder != null) dir.setText(folder.getAbsolutePath());
        else dir.setText("");
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

    @FXML
    void Descomprimir(ActionEvent event) {    	
    	if (this.CheckDir()) {
    		// obtenir l'arxiu zip
            File directory = new File(dir.getText());
            Collection<File> files = FileUtils.listFiles(directory, new WildcardFileFilter("*.zip"), null);
                        
            if (files.isEmpty()) {
            	ShowAlert("No es troba l'arxiu comprimit","Error",AlertType.ERROR);
            } else if (files.size() > 1) {
            	ShowAlert("Hi ha més d'un arxiu comprimit","Error",AlertType.ERROR);
            } else {
            	File file = files.iterator().next();				// arxiu ZIP
            	File folder = new File(dir.getText(),"PDFs");		// carpeta a on es descomprimeix el ZIP 
            	
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
                    File problems = new File(dir.getText(),"problemes");		// carpeta a on es copien les PECs problemàtiques
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
                } catch (Exception e) {
                	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
                }
            }
    	}
    }
    
    @FXML
    void Analitzar(ActionEvent event) {
    	if (this.CheckDir()) {
            File folder = new File(dir.getText());
    		File pecs = new File(dir.getText(),"PDFs");
            Collection<File> files = FileUtils.listFiles(folder, new WildcardFileFilter("sol.txt"), null);

            if (!pecs.exists()) {
            	ShowAlert("No es troba la carpeta PDFs","Error",AlertType.ERROR);
            } else if (files.isEmpty()) {
            	ShowAlert("No es troba l'arxiu sol.txt","Error",AlertType.ERROR);
            } else {
            	// SOLUCIÓ: arxiu sol.txt 
            	ArrayList<Pregunta> Plantilla = GetPlantilla(files.iterator().next());
            	
                // DADES: si no s'han extret del PDF, extreure-les
                files = FileUtils.listFiles(folder, new WildcardFileFilter("dades_pecs.txt"), null);
                if (files.isEmpty()) GetDadesPECs(folder, pecs, Plantilla);

                // PECS: respostes de les PECs dels alumnes
                ArrayList<PEC> PECs = new ArrayList<PEC>();
            	try {
            		// obtenir les dades de cada pec, fila a fila
            		LineIterator it = FileUtils.lineIterator(new File(folder.getAbsolutePath() + File.separator + "dades_pecs.txt"), "UTF-8");
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
            			if (r.tipo != Tipo.LLIURE) st.getItem(r.nom).Add(r.resp);
            		}
            	}
            	
            	// obtenir totes les possibles solucions ordenades de major a menor percentatge
            	ArrayList<Solucio> solucions = new ArrayList<Solucio>();
            	for (Item i: st.items) {
            		solucions.add(new Solucio(i,PECs.size()));
            	}
            	
            	// defecte: l'opció amb més % d'aparació (la primera) és la correcta
            	for (Solucio s: solucions) {
                	Opcio o = s.opcions.get(0);
                	o.correcte = true;
                	o.solucio = true;
            	}

                try {
                    FXMLLoader fxml = new FXMLLoader(getClass().getResource("Analitzar.fxml"));
                    Parent r = (Parent) fxml.load();
                    Stage stage = new Stage(); 
                    stage.initModality(Modality.APPLICATION_MODAL); 
                    stage.setScene(new Scene(r));
                    stage.setTitle("Analitzar Solució");
                    AnalitzarController analitzar = fxml.<AnalitzarController>getController();
                    analitzar.SetData(solucions);
                    stage.showAndWait();
                } catch(Exception e) {
                	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
                }
            	
/*            	for (Solucio s: solucions) {
            		System.out.println(s.pregunta);
            		for (Opcio o: s.opcions) {
            			System.out.print(o.value + " / ");
            			System.out.print(o.pct);
            			System.out.print(" / ");
            			System.out.print(o.correcte);
            			System.out.print(" / ");
            			System.out.println(o.solucio);
            		}
            	}*/
            }
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
    	Collection<File> pdfs = FileUtils.listFiles(pecs, new WildcardFileFilter("*.pdf"), null);
        List<String> lines = new ArrayList<>();
        for (File f : pdfs) {
            if (f.isFile()) {
                String n = f.getName();
                String dni = n.substring(n.lastIndexOf("_")+1);
                dni = dni.substring(0,dni.indexOf("."));
                //String dni = n.substring(n.lastIndexOf("_")+1,n.lastIndexOf("."));
                
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
        	Files.write(Paths.get(folder.getAbsolutePath() + File.separator + "dades_pecs.txt"), lines, Charset.forName("UTF-8"));
        } catch (Exception e) {
        	ShowAlert(e.getMessage(),"Error",AlertType.ERROR);
        }
    }
    
    public void ShowAlert(String message, String title, AlertType type) {
    	Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    	dir.setText(C_DEFDIR);
    }
}
