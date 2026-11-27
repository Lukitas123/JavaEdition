package entidades.cofres;

import java.util.List;
import modelo.*;
import modelo.eventos.FinLava;

public class TrampaDeLava extends Cofre {
    private static final int DURACION = 10;

    public TrampaDeLava(Posicion posicion){
        super(posicion);
    }

    @Override
    protected void aplicarEfecto(Heroe heroe, Plano plano, Agenda agenda) {
        List<Posicion> vecinos = plano.vecinosTransitables(getPosicion());

        if (vecinos.isEmpty()) {
            return;
        }

        int pasoActual = agenda.getPasoActual();
        int vencimiento = 0;

        for (int i = 0; i < vecinos.size(); i++) {
            vencimiento = plano.activarLava(
                    vecinos.get(i), pasoActual, DURACION);
        }

        Ubicacion ubicacion = heroe.getUbicacion();
        agenda.programar(vencimiento - pasoActual,
                new FinLava(ubicacion.tablero(), ubicacion.piso()));
    }
}
