package entidades.cofres;

import modelo.*;
import modelo.eventos.FinPuente;

public class CofreDePuente extends Cofre{
    private static final int DURACION = 15;

    public CofreDePuente(Posicion posicion){
        super(posicion);
    }

    @Override
    protected void aplicarEfecto(Heroe heroe, Plano plano, Agenda agenda) {
        int pasoActual = agenda.getPasoActual();
        int vencimiento = plano.activarPuente(pasoActual, DURACION);
        Ubicacion ubicacion = heroe.getUbicacion();

        agenda.programar(vencimiento - pasoActual,
                new FinPuente(ubicacion.tablero(), ubicacion.piso()));
    }
}
