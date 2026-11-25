package entidades.cofres;

import modelo.*;

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

        String dato = ubicacion.tablero() + "," + ubicacion.piso();
        agenda.programar(vencimiento - pasoActual, TipoEvento.FIN_PUENTE, dato);
    }
}
