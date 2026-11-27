package entidades.objetos;

import modelo.*;
import modelo.eventos.FinSueno;

public class Sueno implements Objeto{
    public static final String ID_OBJETO = "sueno";
    private final int duracion;

    public Sueno(int duracion) {
        if (duracion <= 0) {
            throw new IllegalArgumentException(
                    "La duracion debe ser positiva");
        }

        this.duracion = duracion;
    }

    @Override
    public ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda) {
        if (heroe == null || plano == null || agenda == null) {
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }

        int pasoActual = agenda.getPasoActual();
        int vencimiento = plano.getEfectos().dormirMonstruos(pasoActual, duracion);
        Ubicacion ubicacion = heroe.getUbicacion();

        agenda.programar(vencimiento - pasoActual,
                new FinSueno(ubicacion.tablero(), ubicacion.piso()));
        return new ResultadoUso(true, null);
    }
}
