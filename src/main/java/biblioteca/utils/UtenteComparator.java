/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.utils;

import biblioteca.model.Utente;
import java.util.Comparator;

/**
 * @class UtenteComparator
 * @brief Definisce il criterio di ordinamento per gli oggetti di tipo Utente.
 * @details Implementa l'interfaccia Comparator per ordinare gli utenti in modo 
 * alfabetico. L'ordinamento è prioritario sul cognome e, in caso di omonimia, 
 * viene effettuato sul nome.
 * @author lpane
 * @date 2026-04-16
 */
public class UtenteComparator implements Comparator<Utente>{
    /**
     * @brief Confronta due utenti per determinarne l'ordine alfabetico.
     * @param u1 Il primo utente da confrontare.
     * @param u2 Il secondo utente da confrontare.
     * @return Un intero negativo, zero o positivo.
     * @details Il confronto avviene in due fasi:
     * 1. Confronto tra i cognomi.
     * 2. Se i cognomi sono identici, confronto tra i nomi.
     */
    @Override
    public int compare(Utente u1 , Utente u2){
        int cognome = u1.getCognome().compareToIgnoreCase(u2.getCognome());
        if(cognome == 0)
            return u1.getNome().compareToIgnoreCase(u2.getNome());
        return cognome;
    }
}
