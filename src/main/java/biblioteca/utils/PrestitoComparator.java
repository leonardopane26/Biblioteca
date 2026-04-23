/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.utils;

import biblioteca.model.Prestito;
import java.util.Comparator;

/**
 *
 * @author lpane
 */
public class PrestitoComparator implements Comparator<Prestito> {
    @Override
    public int compare(Prestito p1 , Prestito p2){
        return p1.getDataFine().compareTo(p2.getDataFine());
    }
}
