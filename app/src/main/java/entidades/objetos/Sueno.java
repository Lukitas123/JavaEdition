package entidades.objetos;

import modelo.*;
import modelo.eventos.FinSueno;

public class Sueno implements Objeto{
    public static final String ID_OBJETO = "sueno";
    private static final int DURACION = 7;

    @Override
    public ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda) {
        if (heroe == null || plano == null || agenda == null) {
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }

        int pasoActual = agenda.getPasoActual();
        int vencimiento = plano.getEfectos().dormirMonstruos(pasoActual, DURACION);
        Ubicacion ubicacion = heroe.getUbicacion();

        agenda.programar(vencimiento - pasoActual,
                new FinSueno(ubicacion.tablero(), ubicacion.piso()));
        return new ResultadoUso(true, null);
    }
}
