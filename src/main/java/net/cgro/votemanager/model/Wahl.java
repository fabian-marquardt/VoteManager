package net.cgro.votemanager.model;

import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Wahl
{
        private static Wahl instance = null;
    
	private ObservableList<Urne> urnen = FXCollections.observableArrayList();
	private ObservableList<Liste> listen = FXCollections.observableArrayList();
	private ObservableList<Ergebnis> ergebnisse = FXCollections.observableArrayList();

        private Wahl()
        {
            // Constructor private!
        }
        
        public static void setInstance(Wahl wahl)
        {
            instance = wahl;
        }
        
        // Singleton pattern
        public static Wahl getInstance()
        {
            if (instance == null)
            {
                instance = new Wahl();
            }
            return instance;
        }
        
	@XmlElementWrapper
	@XmlElement(name="urne")
	public ObservableList<Urne> getUrnen()
	{
		return urnen;
	}

	public void addUrne(Urne urne)
	{
		urnen.add(urne);
	}
        
        public void removeUrne(Urne urne)
        {
            urnen.remove(urne);
            // TODO: Entferne Ergebnisse dieser Urne
        }

	@XmlElementWrapper
	@XmlElement(name="liste")
	public ObservableList<Liste> getListen()
	{
		return listen;
	}

	public void addListe(Liste liste)
	{
		listen.add(liste);
	}
        
         public void removeListe(Liste liste)
        {
            listen.remove(liste);
            // TODO: Entferne Ergebnisse dieser Liste
        }

	@XmlElementWrapper
	@XmlElement(name="ergebnis")
	public ObservableList<Ergebnis> getErgebnisse()
	{
		return ergebnisse;
	}

	public void setErgebnis(Ergebnis ergebnis)
	{
            // Altes Ergebnis der Urne löschen, falls es existiert
            for(Iterator<Ergebnis> iterator = ergebnisse.iterator(); iterator.hasNext();)
            {
                if(iterator.next().getUrne() == ergebnis.getUrne())
                {
                    iterator.remove();
                }
            }
            
            // Neues Ergebnis eintragen
            ergebnisse.add(ergebnis);
            ergebnis.getUrne().updateStatus();
	}
        
        public boolean existiertErgebnis(Urne urne)
        {
            // Suche passendes Ergebnis
            for(Ergebnis e: ergebnisse)
            {
                if(e.getUrne() == urne)
                {
                    return true;
                }
            }
            
            return false;
        }
        
        public Ergebnis getErgebnis(Urne urne)
        {
            // Suche passendes Ergebnis
            for(Ergebnis e: ergebnisse)
            {
                if(e.getUrne() == urne)
                {
                    return e;
                }
            }
            
            // Wenn noch kein Ergebnis existiert, neues anlegen.
            Ergebnis erg = new Ergebnis(urne);
            return erg;
        }
}