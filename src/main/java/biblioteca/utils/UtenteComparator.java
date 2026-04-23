/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.utils;

import biblioteca.model.Utente;
import java.util.Comparator;

/**
 *
 * @author lpane
 */
public class UtenteComparator implements Comparator<Utente>{
    @Override
    public int compare(Utente u1 , Utente u2){
        int cognome = u1.getCognome().compareToIgnoreCase(u2.getCognome());
        if(cognome == 0)
            return u1.getNome().compareToIgnoreCase(u2.getNome());
        return cognome;
    }
}
