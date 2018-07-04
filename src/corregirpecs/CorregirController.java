package corregirpecs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class CorregirController implements Initializable {

    @FXML
    private TextField zip;
    @FXML
    private CheckBox presencials;

    
    @FXML
    void getZip(ActionEvent event) {
    	FileChooser chooser = new FileChooser();
    	chooser.setTitle("Obrir arxiu comprimit");
    	chooser.setTitle("View Pictures");
    	chooser.setInitialDirectory(new File(System.getProperty("user.home")));                 
    	chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP", "*.zip"));
    	File f = chooser.showOpenDialog(null);
        if (f != null) zip.setText(f.getAbsolutePath());
        else zip.setText("");    	
    }

    @FXML
    void Analisi(ActionEvent event) {
    	String c = zip.getText();
    	File fzip = new File(zip.getText());
        if (fzip.isFile()) {
            File dir = new File(c.substring(0, c.lastIndexOf(".")));
        	if (Descomprimeix(fzip, dir)) {
        		System.out.println("ok");
        	}
        } else {
    		Alert alert = new Alert(Alert.AlertType.INFORMATION, "L'arxiu ZIP no existeix");
    		alert.showAndWait();
        }
    }
    
    public boolean Descomprimeix(File f, File dir) {
    	// descomprimir l'arxiu ZIP f a la carpeta dir
        try {
        	// la carpeta es crea nova; si ja existeix, s'elimina
        	if (dir.exists()) FileUtils.deleteDirectory(dir); 
        	dir.mkdir();
        	
        	byte[] buffer = new byte[1024];
            // get the zip file content
            ZipInputStream zis = new ZipInputStream(new FileInputStream(f));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze!=null) {
                String fileName = ze.getName();
                File newFile = new File(dir + File.separator + fileName);

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
            
            return true;
        } catch (Exception e) {
        	Alert alert = new Alert(Alert.AlertType.INFORMATION, e.getMessage());
    		alert.showAndWait();
    		return false;    		
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    	
    }
}
