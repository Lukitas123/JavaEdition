package entidades.cofres;

import entidades.objetos.PocionDeEnergia;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;

/**
 * Cofre que agrega una posion de energia al inventario del Héroe
 */
public class CofreDePocion extends CofreDeObjeto{
    public CofreDePocion(Posicion posicion){
        super(posicion, PocionDeEnergia.id);
    }


}
