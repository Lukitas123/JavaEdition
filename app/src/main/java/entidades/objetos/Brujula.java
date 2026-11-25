package entidades.objetos;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;

public class Brujula implements Objeto{
    public static final String ID_OBJETO = "brujula";

    public String consultar(Heroe heroe, Plano plano){
        if(heroe == null || plano == null){
            throw new IllegalArgumentException("Heroe y Plano son obligatorioa");
        }
        Posicion salida = plano.getSalida();
        if (salida == null) {
            return "La salida no esta en este plano.";
        }

        Posicion actual = heroe.getUbicacion().posicion();
        if (actual.equals(salida)) {
            return "Estas en la salida.";
        }

        String vertical = "";
        if (salida.fila() < actual.fila()) {
            vertical = "arriba";
        } else if (salida.fila() > actual.fila()) {
            vertical = "abajo";
        }

        String horizontal = "";
        if (salida.columna() < actual.columna()) {
            horizontal = "izquierda";
        } else if (salida.columna() > actual.columna()) {
            horizontal = "derecha";
        }

        String orientacion;
        if (vertical.isEmpty()) {
            orientacion = horizontal;
        } else if (horizontal.isEmpty()) {
            orientacion = vertical;
        } else {
            orientacion = vertical + " a la " + horizontal;
        }

        return "La salida esta hacia " + orientacion;
    }

    @Override
    public ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda) {
        if (heroe == null || plano == null || agenda == null) {
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }
        return new ResultadoUso(true, consultar(heroe, plano));

    }
}
