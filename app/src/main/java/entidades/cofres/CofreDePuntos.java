package entidades.cofres;

import modelo.Heroe;
import modelo.Posicion;
import modelo.Agenda;

public class CofreDePuntos extends Cofre{
    public CofreDePuntos(Posicion posicion){
        super(posicion);
    }

    @Override
    public void aplicarEfecto(Heroe heroe, Agenda agenda) {
        // no tiene que sumar los puntos pq se calcula desde heroe
    }
}
