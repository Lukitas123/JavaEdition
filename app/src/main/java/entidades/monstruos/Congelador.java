package entidades.monstruos;

import modelo.*;
import modelo.eventos.FinEfectoHeroe;

import java.util.Random;

public class Congelador extends Errante{
    private final int duracion;

    public Congelador(Posicion posicionInicial, Random random, int duracion){
        super(posicionInicial, random, 0);
        if (duracion <= 0) {
            throw new IllegalArgumentException("La duracion debe ser positiva");
        }
        this.duracion = duracion;
    }

    @Override
    public void alcanzarAlHeroe(Heroe heroe, Agenda agenda) {
        if (heroe == null || agenda == null) {
            throw new IllegalArgumentException("Heroe y agenda son obligatorios");
        }

        int pasoActual = agenda.getPasoActual();
        Integer vencimientoAnterior = heroe.vencimientoDe(EfectoHeroe.INMOVILIZADO);

        int primerPasoLibre = pasoActual + 1;
        if (vencimientoAnterior != null) {
            primerPasoLibre = Math.max(primerPasoLibre, vencimientoAnterior);
        }

        int nuevoVencimiento = primerPasoLibre + duracion;
        heroe.activarEfecto(EfectoHeroe.INMOVILIZADO, nuevoVencimiento);
        agenda.programar(
                nuevoVencimiento - pasoActual,
                new FinEfectoHeroe(EfectoHeroe.INMOVILIZADO));

        volverAlInicio();
    }
}
