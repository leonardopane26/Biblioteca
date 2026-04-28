/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.utils;

import biblioteca.model.Libro;
import java.util.Comparator;

/**
 * @class LibroComparator
 * @brief Fornisce un criterio di ordinamento per gli oggetti di tipo Libro.
 * @details Implementa l'interfaccia Comparator per permettere l'ordinamento 
 * alfabetico dei libri in base al titolo, ignorando la differenza tra maiuscole e minuscole.
 * @author lpane
 * @date 2026-04-16
 */
public class LibroComparator implements Comparator<Libro> {
    /**
     * @brief Confronta due libri in base al titolo.
     * @param l1 Il primo libro da confrontare.
     * @param l2 Il secondo libro da confrontare.
     * @return Un intero negativo, zero o positivo se il titolo del primo libro è 
     * rispettivamente precedente, uguale o successivo a quello del secondo in ordine alfabetico.
     * @details Utilizza il metodo `compareToIgnoreCase` per garantire che l'ordinamento 
     * sia naturale (es. 'a' e 'A' vengono trattate allo stesso modo).
     */
    @Override
    public int compare(Libro l1 , Libro l2){
        return l1.getTitolo().compareToIgnoreCase(l2.getTitolo());
    }
}
