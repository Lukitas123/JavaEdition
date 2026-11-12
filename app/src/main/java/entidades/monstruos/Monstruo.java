package entidades.monstruos;

import modelo.Heroe;
import modelo.Posicion;
import modelo.Agenda;
import modelo.Plano;

/**
 * Representa un monstruo que puede actuar en el plano y afectar al heroe.
 * Tiene una posicion inicial, actual y un estado activo que determina si participa en la partida
 */
public abstract class Monstruo {
    private Posicion posicion;
    private Posicion posicionInicial;
    protected boolean activo;

    protected Monstruo(Posicion posicionInicial){
        if (posicionInicial == null){
            throw new IllegalArgumentException("La posicion inicial no puede ser null");
        }
        this.posicionInicial = posicionInicial;
        this.posicion = posicionInicial;
        this.activo = true;
    }

    public Posicion getPosicion(){
        return posicion;
    }

    public void activar(){
        activo = true;
    }

    public void desactivar(){
        activo = false;
    }
    /**
     * Realiza un turno del monstruo: si está activo, decide y hace su movimiento
     * y si alcanza al héroe, ejecuta el efecto correspondiente a su tipo.
     *
     * Pre:plano, héroe y agenda no son null. Ambos personajes pertenecen al plano
     *
     * Post:si el monstruo estaba inactivo, su estado y posición no cambian.
     * Si estaba activo, su posición se actualiza según su comportamiento y si alcanza al héroe
     * se aplica el efecto definido por su tipo.
     *
     * @param plano plano en el que actúa el monstruo
     * @param heroe héroe al que puede perseguir o alcanzar
     * @param agenda agenda de eventos de la partida
     * @throws IllegalArgumentException si algún argumento es {@code null}
     */
    public void actuar(Plano plano, Heroe heroe, Agenda agenda){ 
        if (plano == null || heroe == null || agenda == null){
            throw new IllegalArgumentException("Plano, heroe y agenda son obligatorios");
        }
        

        antesDeActuar(plano, heroe, agenda);

        if(!activo || !puedeActuar(agenda)){
            return;
        }

        if (posicion.equals(heroe.getUbicacion().posicion())){
            alcanzarAlHeroe(heroe, agenda);
            return;
        }
        
        if(puedeMoverseEstePaso(agenda)){
            Posicion destino = decidirMovimiento(plano, heroe.getUbicacion().posicion());
            if (destino == null || !puedeMoverseA(plano, destino)){ 
                throw new IllegalArgumentException("El monstruo eligio un destino invalido");
            }
            posicion = destino;
            if(posicion.equals(heroe.getUbicacion().posicion())){
                alcanzarAlHeroe(heroe, agenda);
            }
        }
        

    }

    public boolean estaActivo(){
        return activo;
    }
    public void volverAlInicio(){
        posicion = posicionInicial;
    }

    /**
     * Decide la posición a la que intentará moverse el monstruo.
     *
     * Pre:plano y posición del héroe no son {@code null}.
     * Post: devuelve el destino elegido según el comportamiento del tipo concreto.
     * El metodo no modifica la posición del monstruo.
     *
     * @param plano plano donde se encuentra el monstruo
     * @param posicionHeroe posición del héroe
     * @return posición elegida como destino
     */
    public abstract Posicion decidirMovimiento(Plano plano, Posicion posicionHeroe);

    /**
     * Aplica el efecto del monstruo cuando alcanza al héroe.
     *
     * Pre: héroe y agenda no son {@code null}; el monstruo alcanzó al héroe.
     * Post: se aplica el efecto definido por el tipo concreto.
     * La implementación puede modificar el estado del héroe, del monstruo
     * o de la agenda.
     *
     * @param heroe héroe alcanzado
     * @param agenda agenda donde pueden programarse eventos futuros
     */
    public abstract void alcanzarAlHeroe(Heroe heroe, Agenda agenda);

    /**
     * Permite actualizar el comportamiento antes de comprobar si puede actuar
     * Por defecto no hace nada
     */
    protected void antesDeActuar(Plano plano, Heroe heroe, Agenda agenda){
        //casos como dormilon, que se activa segun la cercania, junto con puedeActuar()
    }

    /**
     * Indica si puede moverse o aplicar su efecto de encuentro en este turno.
     * Por defecto puede.
     */
    protected boolean puedeActuar(Agenda agenda){
        return true;
    }

    /**
     * Indica si puede moverse este paso, por default puede
     */
    protected boolean puedeMoverseEstePaso(Agenda agenda){
        return true;
        // Pensado para el fantasma, que pasa las paredes. Lo sobreescribe 
    }

    /**
     * Valida un movimiento normal: quedarse en posicion o moverse a la siguiente celda transitable
     * Pre: plano y destino no son null
     */
    protected boolean puedeMoverseA(Plano plano, Posicion destino){
        return posicion.distanciaManhattan(destino)<=1 && plano.esTransitable(destino);
    }

}
