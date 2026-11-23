package entidades.cofres;

import estructuras.Lista;
import modelo.*;

public class TrampaDeLava extends Cofre {
    private static final int DURACION = 10;

    public TrampaDeLava(Posicion posicion){
        super(posicion);
    }

    @Override
    protected void aplicarEfecto(Heroe heroe, Plano plano, Agenda agenda) {
        Lista<Posicion> vecinos = plano.vecinosTransitables(getPosicion());

        if (vecinos.cantidad() == 0) {
            return;
        }

        int pasoActual = agenda.getPasoActual();
        int vencimiento = 0;

        for (int i = 0; i < vecinos.cantidad(); i++) {
            vencimiento = plano.activarLava(
                    vecinos.obtener(i), pasoActual, DURACION);
        }

        Ubicacion ubicacion = heroe.getUbicacion();
        String dato = ubicacion.tablero() + "," + ubicacion.piso();
        agenda.programar(vencimiento - pasoActual, TipoEvento.FIN_LAVA, dato);
    }
}
