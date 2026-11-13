package entidades.cofres;

import modelo.Heroe;
import modelo.Posicion;
import modelo.Agenda;
import modelo.Plano;

public abstract class Cofre {
    private final Posicion posicion;
    private boolean abierto;

    protected Cofre(Posicion posicion){
        if (posicion== null){
            throw new IllegalArgumentException("La posicion no puede ser null");

        }
        this.posicion = posicion;
        this.abierto = false;

    }

    public Posicion getPosicion(){
        return posicion;
    }

    public boolean estaAbierto(){
        return abierto;
    }

    /**
     * Abre el cofre una sola vez y aplica su efecto.
     *
     * Pre: héroe y agenda no son {@code null}.
     * Post: si estaba cerrado, queda abierto,se registra la apertura en el héroe y
     * se aplica el efecto del tipo  concreto. Si ya estaba abierto, no cambia el estado.
     * 
     * @throws IllegalArgumentException si algun argumento es null
     * @throws IllegalStateException si el cofre no pertenece al plano
     */
    public void abrir(Heroe heroe, Plano plano, Agenda agenda){
        if (heroe == null || agenda == null || plano == null){
            throw new IllegalArgumentException("El plano, la agenda y el heroe no pueden ser null");
        }
        if (!abierto){
            if(plano.cofreEn(posicion) != this){
                throw new IllegalStateException("El cofre no esta registrado en el plano indicado");
            }
            aplicarEfecto(heroe, plano, agenda);
            abierto = true;
            heroe.registrarCofreAbierto();
            plano.quitarCofre(this);
        }
    }

    /**
     * Aplica el efecto propio del cofre. La implementación vacía se aplica para los
     * cofres de puntos.
     */
    public abstract void aplicarEfecto(Heroe heroe, Plano plano, Agenda agenda);
}
