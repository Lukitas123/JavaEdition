package entidades.objetos;

import modelo.*;
import modelo.eventos.FinEfectoHeroe;

public class ZapatosDeAgua implements Objeto{
    public static final String ID_OBJETO = "zapatos_agua";

    private static final int DURACION = 20;

    @Override
    public ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda) {
        if (heroe == null || plano == null || agenda == null) {
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }
        int pasoActual = agenda.getPasoActual();
        Integer vencimientoAnterior = heroe.vencimientoDe(EfectoHeroe.ZAPATOS_DE_AGUA);

        int primerPasoLibre = pasoActual + 1;
        if (vencimientoAnterior != null) {
            primerPasoLibre = Math.max(primerPasoLibre, vencimientoAnterior);
        }

        int nuevoVencimiento = primerPasoLibre + DURACION;
        heroe.activarEfecto(EfectoHeroe.ZAPATOS_DE_AGUA, nuevoVencimiento);
        agenda.programar(
                nuevoVencimiento - pasoActual,
                new FinEfectoHeroe(EfectoHeroe.ZAPATOS_DE_AGUA));

        return new ResultadoUso(true, null);
    }
}
