package entidades.objetos;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.TipoEvento;

public class Escudo implements Objeto{
    public static final String ID_OBJETO = "escudo";
    public static final String ID_EFECTO = "proteccion_monstruos";

    private static final int DURACION = 10;

    @Override
    public ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda) {
        if (heroe == null || plano == null || agenda == null) {
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }
        int pasoActual = agenda.getPasoActual();
        Integer vencimientoAnterior = heroe.vencimientoDe(ID_EFECTO);

        int primerPasoLibre = pasoActual + 1;
        if (vencimientoAnterior != null) {
            primerPasoLibre = Math.max(primerPasoLibre, vencimientoAnterior);
        }

        int nuevoVencimiento = primerPasoLibre + DURACION;
        heroe.activarEfecto(ID_EFECTO, nuevoVencimiento);
        agenda.programar(
                nuevoVencimiento - pasoActual,
                TipoEvento.FIN_ESCUDO,
                null);

        return new ResultadoUso(true, null);
    }
}
