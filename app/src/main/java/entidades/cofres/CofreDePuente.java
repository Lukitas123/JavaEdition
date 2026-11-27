package entidades.cofres;

import modelo.*;
import modelo.eventos.FinPuente;

public class CofreDePuente extends Cofre{
    private final int duracion;

    public CofreDePuente(Posicion posicion, int duracion){
        super(posicion);
        if (duracion <= 0) {
            throw new IllegalArgumentException(
                    "La duracion debe ser positiva");
        }

        this.duracion = duracion;
    }

    @Override
    protected void aplicarEfecto(Heroe heroe, Plano plano, Agenda agenda) {
        int pasoActual = agenda.getPasoActual();
        int vencimiento = plano.getEfectos().activarPuente(pasoActual, duracion);
        Ubicacion ubicacion = heroe.getUbicacion();

        agenda.programar(vencimiento - pasoActual,
                new FinPuente(ubicacion.tablero(), ubicacion.piso()));
    }
}
