package modelo.eventos;

import entidades.monstruos.Monstruo;
import modelo.Mazmorra;
import modelo.Plano;

public record DespertarDormilones(int tablero, int piso) implements Evento {

    public DespertarDormilones{
        if (tablero < 1 || piso < 1) {
            throw new IllegalArgumentException("Tablero y piso deben ser positivos");
        }
    }
    @Override
    public void ejecutar(Mazmorra partida) {
        Plano plano = partida.getMundo().planoEn(tablero, piso);
        int pasoActual = partida.getAgenda().getPasoActual();

        for (Monstruo monstruo : plano.getMonstruos()) {
            monstruo.despertarSiCorresponde(pasoActual);
        }
    }
}
