package entidades.cofres;

import estructuras.Lista;
import modelo.*;
import modelo.eventos.FinLava;

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
            vencimiento = plano.getEfectos().activarLava(
                    vecinos.obtener(i), pasoActual, DURACION);
        }

        Ubicacion ubicacion = heroe.getUbicacion();
        agenda.programar(vencimiento - pasoActual,
                new FinLava(ubicacion.tablero(), ubicacion.piso()));
    }
}
