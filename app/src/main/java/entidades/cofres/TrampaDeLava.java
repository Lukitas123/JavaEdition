package entidades.cofres;

import estructuras.Lista;
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
        Lista<Posicion> vecinos = plano.vecinosTransitables(getPosicion());

        if (vecinos.cantidad() == 0) {
            return;
        }

        int pasoActual = agenda.getPasoActual();
        int vencimiento = 0;

        for (int i = 0; i < vecinos.cantidad(); i++) {
            vencimiento = plano.getEfectos().activarLava(
                    vecinos.obtener(i), pasoActual, duracion);
        }

        Ubicacion ubicacion = heroe.getUbicacion();
        agenda.programar(vencimiento - pasoActual,
                new FinLava(ubicacion.tablero(), ubicacion.piso()));
    }
}
