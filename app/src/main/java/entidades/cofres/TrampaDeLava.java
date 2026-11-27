package entidades.cofres;

import java.util.List;
import modelo.*;
import modelo.eventos.FinLava;

public class TrampaDeLava extends Cofre {
    private final int duracion;

    public TrampaDeLava(Posicion posicion, int duracion){
        super(posicion);
        if (duracion <= 0) {
            throw new IllegalArgumentException(
                    "La duracion debe ser positiva");
        }

        this.duracion = duracion;
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
            vencimiento = plano.getEfectos().activarLava(
                    vecinos.get(i), pasoActual, duracion);
        }

        Ubicacion ubicacion = heroe.getUbicacion();
        agenda.programar(vencimiento - pasoActual,
                new FinLava(ubicacion.tablero(), ubicacion.piso()));
    }
}
