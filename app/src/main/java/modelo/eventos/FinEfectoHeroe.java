package modelo.eventos;

import modelo.EfectoHeroe;
import modelo.Mazmorra;

public record FinEfectoHeroe(EfectoHeroe efectoHeroe) implements Evento{
    public FinEfectoHeroe{
        if(efectoHeroe == null){
            throw new IllegalArgumentException("El efecto no puede ser null");
        }
    }

    @Override
    public void ejecutar(Mazmorra partida) {
        partida.getHeroe().finalizarEfectoSiVencio(efectoHeroe, partida.getAgenda().getPasoActual());
    }
}
