package entidades.monstruos;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Posicion;
import modelo.TipoEvento;

import java.util.Random;

public class Congelador extends Errante{
    public static final String INMOVILIZADO = "inmovilizado";
    private final int duracion;

    public Congelador(Posicion posicionInicial, Random random, int duracion){
        super(posicionInicial, random);
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
        Integer vencimientoAnterior = heroe.vencimientoDe(INMOVILIZADO);

        int primerPasoLibre = pasoActual + 1;
        if (vencimientoAnterior != null) {
            primerPasoLibre = Math.max(primerPasoLibre, vencimientoAnterior);
        }

        int nuevoVencimiento = primerPasoLibre + duracion;
        heroe.activarEfecto(INMOVILIZADO, nuevoVencimiento);
        agenda.programar(
                nuevoVencimiento - pasoActual,
                TipoEvento.FIN_INMOVILIZACION,
                null);

        volverAlInicio();
    }
}
