package modelo.eventos;

import modelo.Mazmorra;
import modelo.Plano;

public record FinLava(int tablero, int piso) implements Evento {
    public FinLava{
        if (tablero < 1 || piso < 1) {
            throw new IllegalArgumentException("Tablero y piso deben ser positivos");
        }
    }
    @Override
    public void ejecutar(Mazmorra partida) {
        Plano plano = partida.getMundo().planoEn(tablero, piso);
        plano.finalizarLavaSiVencio(partida.getAgenda().getPasoActual());
    }
}
