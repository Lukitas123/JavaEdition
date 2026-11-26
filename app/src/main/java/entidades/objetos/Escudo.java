package entidades.objetos;

import modelo.*;
import modelo.eventos.FinEfectoHeroe;

public class Escudo implements Objeto{
    public static final String ID_OBJETO = "escudo";

    private static final int DURACION = 10;

    @Override
    public ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda) {
        if (heroe == null || plano == null || agenda == null) {
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }
        int pasoActual = agenda.getPasoActual();
        Integer vencimientoAnterior = heroe.vencimientoDe(EfectoHeroe.ESCUDO);

        int primerPasoLibre = pasoActual + 1;
        if (vencimientoAnterior != null) {
            primerPasoLibre = Math.max(primerPasoLibre, vencimientoAnterior);
        }

        int nuevoVencimiento = primerPasoLibre + DURACION;
        heroe.activarEfecto(EfectoHeroe.ESCUDO, nuevoVencimiento);
        agenda.programar(
                nuevoVencimiento - pasoActual,
                new FinEfectoHeroe(EfectoHeroe.ESCUDO));

        return new ResultadoUso(true, null);
    }
}
