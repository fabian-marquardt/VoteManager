/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cgro.votemanager.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javax.xml.bind.JAXB;
import net.cgro.votemanager.model.ErgebnisEintrag;
import net.cgro.votemanager.model.ErgebnisListe;
import net.cgro.votemanager.model.Kandidat;
import net.cgro.votemanager.model.Liste;
import net.cgro.votemanager.model.Urne;
import net.cgro.votemanager.model.Wahl;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class MainWindowController implements Initializable {   
    @FXML
    private Button buttonUrneAdd;
    @FXML
    private Button buttonUrneRename;
    @FXML
    private Button buttonUrneDelete;
    @FXML
    private TableView<Urne> tableUrnen;
    @FXML
    private TableColumn<Urne, String> columnUrnenName;
    @FXML
    private TableColumn<Urne, Integer> columnUrnenNummer;
    
    @FXML
    private Button buttonListeAdd;
    @FXML
    private Button buttonListeRename;
    @FXML
    private Button buttonListeDelete;
    @FXML
    private TableView<Liste> tableListen;
    @FXML
    private TableColumn<Liste, String> columnListenName;
    @FXML
    private TableColumn<Liste, String> columnListenKuerzel;
    @FXML
    private TableColumn<Liste, Integer> columnListenNummer;
    
    @FXML
    private ComboBox<Liste> comboListen;
    @FXML
    private Button buttonKandidatAdd;
    @FXML
    private Button buttonKandidatRename;
    @FXML
    private Button buttonKandidatDelete;
    @FXML
    private TableView<Kandidat> tableKandidaten;
    @FXML
    private TableColumn<Kandidat, String> columnKandidatenName;
    @FXML
    private TableColumn<Kandidat, Integer> columnKandidatenNummer;
    
     @FXML
    private Button buttonStimmenModify;
    @FXML
    private Button buttonStimmenDelete;
    @FXML
    private TableView<Urne> tableStimmeneingabe;
    @FXML
    private TableColumn<Urne, String> columnStimmenUrne;
    @FXML
    private TableColumn<Urne, Integer> columnStimmenNummer;
    @FXML
    private TableColumn<Urne, String> columnStimmenStatus;
    
    @FXML
    private ComboBox<Urne> comboErgebnisUrne;
    @FXML
    private TreeTableView<ErgebnisEintrag> tableErgebnisanzeige;
    @FXML
    private TreeTableColumn<ErgebnisEintrag, String> columnErgebnisText;
    @FXML
    private TreeTableColumn<ErgebnisEintrag, Integer> columnErgebnisStimmen;
    
    
    private Liste current_liste;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initializeGUI();
    }
    
    public void initializeGUI()
    {
        Wahl wahl = Wahl.getInstance();
        
        // Initialisiere Tabelle und verbinde mit Model
        columnUrnenName.setCellValueFactory(new PropertyValueFactory<Urne, String>("name"));
        columnUrnenNummer.setCellValueFactory(new PropertyValueFactory<Urne, Integer>("nummer"));
        tableUrnen.setItems(wahl.getUrnen());
        // Nur einzelne Zeilen auswählen
        tableUrnen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Initialisiere Tabelle und verbinde mit Model
        columnListenName.setCellValueFactory(new PropertyValueFactory<Liste, String>("name"));
        columnListenKuerzel.setCellValueFactory(new PropertyValueFactory<Liste, String>("kuerzel"));
        columnListenNummer.setCellValueFactory(new PropertyValueFactory<Liste, Integer>("nummer"));
        tableListen.setItems(wahl.getListen());
        // Nur einzelne Zeilen auswählen
        tableListen.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Status einmal neu einlesen, damit die gerade geladenen Ergebnisse berücksichtigt werden.
        for (Urne urne: Wahl.getInstance().getUrnen())
        {
             urne.updateStatus();
        }
        
        // Initialisiere Tabelle und verbinde mit Model
        columnStimmenUrne.setCellValueFactory(new PropertyValueFactory<Urne, String>("name"));
        columnStimmenNummer.setCellValueFactory(new PropertyValueFactory<Urne, Integer>("nummer"));
        columnStimmenStatus.setCellValueFactory(new PropertyValueFactory<Urne, String>("status"));
        tableStimmeneingabe.setItems(wahl.getUrnen());
        // Nur einzelne Zeilen auswählen
        tableStimmeneingabe.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Combo-Box für die Listen in der Kandidatenansicht
        comboListen.setConverter(new StringConverter<Liste>() {
              @Override
              public String toString(Liste liste) {
                if (liste == null){
                  return null;
                } else {
                  return liste.getKuerzel();
                }
              }

            @Override
            public Liste fromString(String userId) {
                return null;
            }
        });
        
        comboListen.setItems(wahl.getListen());
        comboListen.getSelectionModel().selectFirst();
        
        // Combo-Box für die Urnen in der Ergebnisansicht
        comboErgebnisUrne.setConverter(new StringConverter<Urne>() {
              @Override
              public String toString(Urne urne) {
                if (urne == null){
                  return null;
                } else {
                  return urne.getName();
                }
              }

            @Override
            public Urne fromString(String userId) {
                return null;
            }
        });
        
        comboErgebnisUrne.setItems(wahl.getUrnen());
    }
    
    @FXML
    private void handleComboListen(ActionEvent event) {
        current_liste = comboListen.getSelectionModel().getSelectedItem();
        
        if(current_liste != null)
        {
            columnKandidatenName.setCellValueFactory(new PropertyValueFactory<Kandidat, String>("name"));
            columnKandidatenNummer.setCellValueFactory(new PropertyValueFactory<Kandidat, Integer>("nummer"));
            tableKandidaten.setItems(current_liste.getKandidaten());
        }
    }
    
    @FXML
    private void handleButtonKandidatAdd(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/KandidatAddDialog.fxml"));
            Parent dialog = loader.load();
            
            Scene scene = new Scene(dialog);
            scene.getStylesheets().add("/styles/Styles.css");
            
            KandidatAddDialogController controller = loader.getController();
            controller.setListe(comboListen.getValue());
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Kandidat hinzufügen");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleButtonKandidatRename(ActionEvent event) {
        
       
    }
    
    @FXML
    private void handleButtonKandidatDelete(ActionEvent event)
    {
       
    }

    @FXML
    private void handleButtonListeAdd(ActionEvent event) {
        try {
            Parent dialog = FXMLLoader.load(getClass().getResource("/fxml/ListeAddDialog.fxml"));
            
            Scene scene = new Scene(dialog);
            scene.getStylesheets().add("/styles/Styles.css");
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Liste hinzufügen");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleButtonListeRename(ActionEvent event) {
        if(tableListen.getSelectionModel().getSelectedItem() == null)
        {
            Dialogs.create()
            .owner((Stage) tableListen.getScene().getWindow())
            .title("Liste bearbeiten")
            .message( "Es ist keine Liste ausgewählt. Bitte eine Liste auswählen.")
            .showError();
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/ListeRenameDialog.fxml"));
            Parent dialog = loader.load();
            
            Scene scene = new Scene(dialog);
            scene.getStylesheets().add("/styles/Styles.css");
            
            ListeRenameDialogController controller = loader.getController();
            controller.setListe(tableListen.getSelectionModel().getSelectedItem());
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Liste ändern");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleButtonListeDelete(ActionEvent event)
    {
        //Liste liste = tableListen.getSelectionModel().getSelectedItem();
        //Wahl.getInstance().removeListe(liste);
    }
    
    @FXML
    private void handleButtonUrneAdd(ActionEvent event) {
        try {
            Parent dialog = FXMLLoader.load(getClass().getResource("/fxml/UrneAddDialog.fxml"));
            
            Scene scene = new Scene(dialog);
            scene.getStylesheets().add("/styles/Styles.css");
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Urne hinzufügen");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleButtonUrneRename(ActionEvent event) {
        if(tableUrnen.getSelectionModel().getSelectedItem() == null)
        {
            Dialogs.create()
            .owner((Stage) tableUrnen.getScene().getWindow())
            .title("Urne bearbeiten")
            .message( "Es ist keine Urne ausgewählt. Bitte eine Urne auswählen.")
            .showError();
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/UrneRenameDialog.fxml"));
            Parent dialog = loader.load();
            
            Scene scene = new Scene(dialog);
            scene.getStylesheets().add("/styles/Styles.css");
            
            UrneRenameDialogController controller = loader.getController();
            controller.setUrne(tableUrnen.getSelectionModel().getSelectedItem());
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Urne umbenennen");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleButtonUrneDelete(ActionEvent event)
    {
        //Urne urne = tableUrnen.getSelectionModel().getSelectedItem();
        //Wahl.getInstance().removeUrne(urne);
    }
    
    @FXML
    private void handleButtonStimmenModify(ActionEvent event)
    {
        if(tableStimmeneingabe.getSelectionModel().getSelectedItem() == null)
        {
            Dialogs.create()
            .owner((Stage) tableStimmeneingabe.getScene().getWindow())
            .title("Stimmen eingeben")
            .message( "Es ist keine Urne ausgewählt. Bitte eine Urne auswählen.")
            .showError();
            return;
        }
        
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/EingabeStimmenDialog.fxml"));
            Parent dialog = loader.load();
            
            Scene scene = new Scene(dialog);
            scene.getStylesheets().add("/styles/Styles.css");
            
            EingabeStimmenDialogController controller = loader.getController();
            Urne urne = tableStimmeneingabe.getSelectionModel().getSelectedItem();
            controller.setErgebnis(Wahl.getInstance().getErgebnis(urne).getDeepCopy());
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Stimmeneingabe");
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void handleButtonStimmenDelete(ActionEvent event)
    {
        
    }
    
    @FXML
    private void handleMenuOpen(ActionEvent event) {
        
        // Öffne aus Datei
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Datei öffnen");
        File file = fileChooser.showOpenDialog((Stage) tableListen.getScene().getWindow());
        Wahl wahl = JAXB.unmarshal(file, Wahl.class);
        Wahl.setInstance(wahl);
        
        // Update der GUI
        initializeGUI();
    }
    
    @FXML
    private void handleMenuSave(ActionEvent event) {
        
        // In Datei schreiben
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Datei speichern");
        File file = fileChooser.showSaveDialog((Stage) tableListen.getScene().getWindow());
        JAXB.marshal(Wahl.getInstance(),file);
    }
    
    @FXML
    private void handleMenuClose(ActionEvent event) {
        // Schließe das Fenster
        Action response = Dialogs.create()
            .owner((Stage) tableListen.getScene().getWindow())
            .title("Programm schließen")
            .message( "Soll das Programm jetzt geschlossen werden? Nicht gespeicherte Änderungen gehen dabei verloren.")
            .showConfirm();
        
        if (response == Dialog.ACTION_YES) {
            Stage stage = (Stage) tableListen.getScene().getWindow();
            stage.close();
        }
    }
    
    @FXML
    private void handleButtonBerechnen(ActionEvent event) {
        ErgebnisListe elist = new ErgebnisListe(comboErgebnisUrne.getSelectionModel().getSelectedItem());
        
        columnErgebnisText.setCellValueFactory(new Callback<CellDataFeatures<ErgebnisEintrag, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(CellDataFeatures<ErgebnisEintrag, String> p) {
                    return p.getValue().getValue().textProperty();
            }
        });
        
        columnErgebnisStimmen.setCellValueFactory(new Callback<CellDataFeatures<ErgebnisEintrag, Integer>, ObservableValue<Integer>>() {
            public ObservableValue<Integer> call(CellDataFeatures<ErgebnisEintrag, Integer> p) {
                    return p.getValue().getValue().stimmenProperty();
            }
        });

        columnErgebnisStimmen.setSortType(TreeTableColumn.SortType.DESCENDING);
        tableErgebnisanzeige.setRoot(elist.getEintraege());
        tableErgebnisanzeige.getSortOrder().clear();
        tableErgebnisanzeige.getSortOrder().add(columnErgebnisStimmen);
        
    }
    
    @FXML
    private void handleButtonAlle(ActionEvent event) {
        comboErgebnisUrne.getSelectionModel().clearSelection();
        this.handleButtonBerechnen(event);
    }
    
    @FXML
    private void handleButtonExport(ActionEvent event) {
        
        PrintWriter writer = null;
        try {
            // In Textdatei exportieren
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ergebnis exportieren");
            File file = fileChooser.showSaveDialog((Stage) tableListen.getScene().getWindow());
            writer = new PrintWriter(file, "UTF-8");
            
            if(comboErgebnisUrne.getSelectionModel().getSelectedItem() == null)
                writer.write("--- GESAMTERGEBNIS ---\n\n");
            else
            {
                Urne urne = comboErgebnisUrne.getSelectionModel().getSelectedItem();
                writer.write("--- Urne " + urne.getNummer() + " - " + urne.getName() + " ---\n\n");
            }
                
            
            for(TreeItem<ErgebnisEintrag> e1: tableErgebnisanzeige.getRoot().getChildren())
            {
                writer.write(e1.getValue().getText() + "\t" + e1.getValue().getStimmen() + "\n");
                
                for(TreeItem<ErgebnisEintrag> e2: e1.getChildren())
                {
                    writer.write(e2.getValue().getText() + "\t" + e2.getValue().getStimmen() + "\n");
                }
                
                writer.write("\n");
            }
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            writer.close();
        }
    }
}
