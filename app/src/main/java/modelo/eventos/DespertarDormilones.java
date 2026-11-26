package modelo.eventos;

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

        for(int i = 0; i<plano.getMonstruos().cantidad(); i++){
            plano.getMonstruos().obtener(i).despertarSiCorresponde(pasoActual);
        }
    }
}
