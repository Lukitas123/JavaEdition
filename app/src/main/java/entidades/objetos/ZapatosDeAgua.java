package entidades.objetos;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.TipoEvento;

public class ZapatosDeAgua implements Objeto{
    public static final String ID_OBJETO = "zapatos_agua";
    public static final String ID_EFECTO = "agua_como_piso";

    private static final int DURACION = 20;

    @Override
    public boolean usar(Heroe heroe, Plano plano, Agenda agenda) {
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
                TipoEvento.FIN_ZAPATOS_DE_AGUA,
                null);
        return true;
    }
}
