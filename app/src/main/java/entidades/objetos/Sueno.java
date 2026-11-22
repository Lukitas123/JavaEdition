package entidades.objetos;

import modelo.*;

public class Sueno implements Objeto{
    public static final String ID_OBJETO = "sueno";
    private static final int DURACION = 7;

    @Override
    public ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda) {
        if (heroe == null || plano == null || agenda == null) {
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }

        int pasoActual = agenda.getPasoActual();
        int vencimiento = plano.dormirMonstruos(pasoActual, DURACION);
        Ubicacion ubicacion = heroe.getUbicacion();
        String dato = ubicacion.tablero() + "," + ubicacion.piso();

        agenda.programar(vencimiento - pasoActual, TipoEvento.FIN_SUENO, dato);
        return new ResultadoUso(true, null);
    }
}
