/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.utils;

import biblioteca.model.Prestito;
import java.util.Comparator;

/**
 * @class PrestitoComparator
 * @brief Fornisce un criterio di ordinamento temporale per gli oggetti di tipo Prestito.
 * @details Implementa l'interfaccia Comparator per permettere l'ordinamento dei prestiti 
 * in base alla data di scadenza (dataFine). È utile per visualizzare in cima alla lista 
 * i prestiti più urgenti o già scaduti.
 * @author lpane
 * @date 2026-04-16
 */
public class PrestitoComparator implements Comparator<Prestito> {
    /**
     * @brief Confronta due prestiti in base alla data di scadenza.
     * @param p1 Il primo prestito da confrontare.
     * @param p2 Il secondo prestito da confrontare.
     * @return Un valore negativo se la scadenza di p1 è precedente a p2, 
     * zero se sono uguali, un valore positivo se è successiva.
     * @details Il confronto sfrutta il metodo `compareTo` della classe LocalDate.
     */
    @Override
    public int compare(Prestito p1 , Prestito p2){
        return p1.getDataFine().compareTo(p2.getDataFine());
    }
}
