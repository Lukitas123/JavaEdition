package entidades.cofres;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;

public class TrampaDeEnergia extends Cofre{

    private final int danio;

    public TrampaDeEnergia(Posicion posicion, int danio) {
        super(posicion);
        if(danio<=0){
            throw new IllegalArgumentException("El daño debe ser positivo");
        }

        this.danio = danio;
    }


    @Override
    protected void aplicarEfecto(Heroe heroe, Plano plano, Agenda agenda) {
        heroe.perderEnergia(danio);

    }
}
