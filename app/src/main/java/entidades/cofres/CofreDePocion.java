package entidades.cofres;

import entidades.objetos.PocionDeEnergia;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;

/**
 * Cofre que agrega una posion de energia al inventario del Héroe
 */
public class CofreDePocion extends Cofre{
    public CofreDePocion(Posicion posicion){
        super(posicion);
    }

    /**
     * Agrega la pocion al inventario, sin recuperar energia hasta no ser usado
     * Pre: heroe, agenda y plano no son null
     * Post: el inventario contiene la pocion de energia
     */
    @Override
    protected void aplicarEfecto(Heroe heroe, Plano plano, Agenda agenda) {
        heroe.agregarObjeto(PocionDeEnergia.id);
    }
}
