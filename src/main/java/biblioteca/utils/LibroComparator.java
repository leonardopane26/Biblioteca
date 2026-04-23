/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.utils;

import biblioteca.model.Libro;
import java.util.Comparator;

/**
 *
 * @author lpane
 */
public class LibroComparator implements Comparator<Libro> {
    @Override
    public int compare(Libro l1 , Libro l2){
        return l1.getTitolo().compareToIgnoreCase(l2.getTitolo());
    }
}
