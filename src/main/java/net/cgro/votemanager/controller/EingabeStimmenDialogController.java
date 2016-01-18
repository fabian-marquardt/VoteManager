/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.cgro.votemanager.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import net.cgro.votemanager.model.CheckResult;
import net.cgro.votemanager.model.Ergebnis;
import net.cgro.votemanager.model.Kandidatenergebnis;
import net.cgro.votemanager.model.Listenergebnis;
import net.cgro.votemanager.model.Wahl;
import net.cgro.votemanager.util.CheckArrayList;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

/**
 * FXML Controller class
 *
 * @author fabian
 */
public class EingabeStimmenDialogController implements Initializable {

    private Ergebnis ergebnis;
    private Listenergebnis current_lerg = null;
    
    @FXML
    private TextField inputStimmen;

    @FXML
    private Button buttonCancel;

    @FXML
    private TextField inputUngueltig;

    @FXML
    private TextField inputGueltig;

    @FXML
    private Label labelStatus;

    @FXML
    private TextField inputEnthaltungen;

    @FXML
    private Button buttonWeiter;
    
    @FXML
    private ComboBox<Listenergebnis> comboListen;
    
    @FXML
    private TextField inputListeGesamt;

    @FXML
    private TextField inputListenstimmen;

    @FXML
    private TableView<Kandidatenergebnis> tableEinzelstimmen;
    
    @FXML
    private TableColumn<Kandidatenergebnis, Integer> columnNummer;
    
    @FXML
    private TableColumn<Kandidatenergebnis, String> columnKandidat;
        
    @FXML
    private TableColumn<Kandidatenergebnis, Integer> columnStimmen;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    public void setErgebnis(Ergebnis ergebnis)
    {
        this.ergebnis = ergebnis;
        Integer stimmen_gesamt = ergebnis.getStimmenGesamt();
        Integer stimmen_gueltig = ergebnis.getStimmenGueltig();
        Integer stimmen_ungueltig = ergebnis.getStimmenUngueltig();
        Integer stimmen_enthaltung = ergebnis.getStimmenEnthaltung();
        inputStimmen.setText(stimmen_gesamt.toString());
        inputGueltig.setText(stimmen_gueltig.toString());
        inputUngueltig.setText(stimmen_ungueltig.toString());
        inputEnthaltungen.setText(stimmen_enthaltung.toString());
        
        comboListen.setConverter(new StringConverter<Listenergebnis>() {
              @Override
              public String toString(Listenergebnis lerg) {
                if (lerg == null){
                  return null;
                } else {
                  return lerg.getListe().getKuerzel();
                }
              }

            @Override
            public Listenergebnis fromString(String userId) {
                return null;
            }
        });
        
        comboListen.setItems(ergebnis.getListenergebnisse());
        comboListen.getSelectionModel().selectFirst();
        current_lerg = comboListen.getSelectionModel().getSelectedItem();
    }
    
    private void saveInputs()
    {
        // Neue Werte übernehmen
        ergebnis.setStimmenGesamt(Integer.parseInt(inputStimmen.getText()));
        ergebnis.setStimmenGueltig(Integer.parseInt(inputGueltig.getText()));
        ergebnis.setStimmenUngueltig(Integer.parseInt(inputUngueltig.getText()));
        ergebnis.setStimmenEnthaltung(Integer.parseInt(inputEnthaltungen.getText()));
        
        if(current_lerg != null && !"".equals(inputListenstimmen.getText()) && !"".equals(inputListeGesamt.getText())){
            current_lerg.setListenstimmen(Integer.parseInt(inputListenstimmen.getText()));
            current_lerg.setGesamtstimmen(Integer.parseInt(inputListeGesamt.getText()));
        }
        
    }
    
    @FXML
    private void handleButtonWeiter(ActionEvent event) {
        // Neue Werte übernehmen
        saveInputs();
        
        // Prüfung
        ArrayList<CheckResult> results = new CheckArrayList<CheckResult>();
        boolean state = this.ergebnis.doChecks(results);
        
        if(state == false){
             Action response = Dialogs.create()
            .owner((Stage) buttonWeiter.getScene().getWindow())
            .title("Probleme gefunden")
            .masthead( "Die Eingabeprüfung hat einen oder mehrere Fehler gefunden. Soll das eingegebene Ergebnis trotzdem übernommen werden?")
            .message(results.toString())
            .showConfirm();
            
            // Speichern forcieren, wenn Nutzer auf Ja klickt
            if (response == Dialog.ACTION_YES) {
                state = true;
            }
        }
       
        if(state==true){
            // Füge neues Ergebnis zur Wahl hinzu
            Wahl wahl = Wahl.getInstance();
            wahl.setErgebnis(ergebnis);
        
            // Schließe das Fenster
            Stage stage = (Stage) buttonWeiter.getScene().getWindow();
            stage.close();
        }
        
    }

    @FXML
    private void handleButtonCancel(ActionEvent event) {
        // Schließe das Fenster
        Stage stage = (Stage) buttonCancel.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleComboListen(ActionEvent event) {
        
        saveInputs();
        
        current_lerg = comboListen.getSelectionModel().getSelectedItem();
        
        inputListenstimmen.setText(Integer.toString(current_lerg.getListenstimmen()));
        inputListeGesamt.setText(Integer.toString(current_lerg.getGesamtstimmen()));
        
        columnNummer.setCellValueFactory(new PropertyValueFactory<Kandidatenergebnis,Integer>("kandidatNummer"));
        columnKandidat.setCellValueFactory(new PropertyValueFactory<Kandidatenergebnis,String>("kandidatName"));
        columnStimmen.setCellValueFactory(new PropertyValueFactory<Kandidatenergebnis,Integer>("stimmen"));
        
        columnStimmen.setCellFactory(TextFieldTableCell.<Kandidatenergebnis, Integer>forTableColumn(new IntegerStringConverter()));
        columnStimmen.setOnEditCommit(
            new EventHandler<CellEditEvent<Kandidatenergebnis, Integer>>() {
                @Override
                public void handle(CellEditEvent<Kandidatenergebnis, Integer> t) {
                    t.getRowValue().setStimmen(t.getNewValue());
                    //if(tableEinzelstimmen.getSelectionModel().getSelectedIndex() < tableEinzelstimmen.getItems().size()-1)
                    //    tableEinzelstimmen.edit(tableEinzelstimmen.getSelectionModel().getSelectedIndex()+1, columnStimmen);

                    Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        tableEinzelstimmen.getSelectionModel().selectNext();
                        tableEinzelstimmen.edit(tableEinzelstimmen.getSelectionModel().getSelectedIndex(), columnStimmen);
                    }
                    });
                }
            }
        );
        
        
        
        tableEinzelstimmen.setItems(current_lerg.getKandidatenergebnisse());
        
    }
    
}
