package entidades.monstruos;

import estructuras.Lista;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Direccion;

import java.util.Random;

/**
 * Monstruo Fantasma: arranca activo, se mueve cada dos pasos pero puede atravesar paredes.
 * Al alcanzar al heroe le quita energía (30)
 */
public class Fantasma extends Monstruo{
    private final Random random;
    private boolean moverEsteTurno;
    private final int danio;

    /**
     * Crea un fantasma en la posicion indicada. Comienza activo y luego alterna entre actuar y esperar
     * @param posicionInicial posicion inicial del fantasma, no puede ser null
     * @param random generador aleatorio para elegir movimientos; no puede ser null
     * @throws IllegalArgumentException si random es null
     */
    public Fantasma(Posicion posicionInicial, Random random, int danio){
        super(posicionInicial);
        if(random == null){
            throw new IllegalArgumentException("El generador aleatorio es obligatorio");
        }
        if (danio <= 0) {
            throw new IllegalArgumentException(
                    "El danio debe ser positivo");
        }
        this.random = random;
        moverEsteTurno = false; //comienza en false porque antesDeActuar() lo invierte
        this.danio = danio;
    }

    /**
     * Alterna si el fantasma puede moverse en este turno activo.
     * Como empieza en false, se alterna y se mueve en el primer turno activo; luego espera el siguiente
     * @param plano plano donde actua el fantasma
     * @param heroe heroe de la partida
     * @param agenda agenda de eventos de la partida
     */
    @Override
    protected void antesDeActuar(Plano plano, Heroe heroe, Agenda agenda) {
        moverEsteTurno = !moverEsteTurno;
    }


    /**
     * Indica si le corresponde moverse en el turno actual
     * @param agenda agenda de eventos de la partida
     * @return true si el fantasma debe moverse; false si debe esperar
     */
    @Override
    protected boolean puedeMoverseEstePaso(Agenda agenda) {
        return moverEsteTurno;
    }

    /**
     * Comprueba si el destino esta dentro del plano, y esta sea la posicion actual o una vecina
     * No comprueba el terreno: puede moverse sobre las paredes
     * @param plano plano donde se mueve
     * @param destino posicion a la que se intenta mover
     * @return true si el destino esta dentro del plano y a una celda de distancia
     */
    @Override
    protected boolean puedeMoverseA(Plano plano, Posicion destino) {
        return plano.estaDentro(destino) && getPosicion().distanciaManhattan(destino)<=1;
    }

    /**
     * Elige al azar una celda vecina perteneciente al plano. Si no hay celdas vecinas,
     * devuelve su posicion actual
     * @param plano plano donde se encuentra el monstruo
     * @param posicionHeroe posición del héroe
     * @return destino elegido o la posicion actual si no hay vecinos
     * @throws IllegalArgumentException si el plano o la posicion del heroe son null
     */
    @Override
    public Posicion decidirMovimiento(Plano plano, Posicion posicionHeroe) {
        if(plano == null || posicionHeroe == null){
            throw new IllegalArgumentException("El plano y la posicion del heroe son obligatorios");
        }
        Lista<Posicion> destinos = new Lista<>();
        for(Direccion direccion : Direccion.values()){
            Posicion candidato = getPosicion().mover(direccion);
            if(plano.estaDentro(candidato)){
                destinos.agregar(candidato);
            }
        }
        if(destinos.estaVacia()){
            return getPosicion();
        }
        return destinos.obtener(random.nextInt(destinos.cantidad()));

    }

    /**
     * Aplica el efecto del fantasma al alcanzar al heroe
     * Le quita 30 ptos de energia y devuelve al fantasma a su posicion inicial
     * @param heroe héroe alcanzado
     * @param agenda agenda de eventos; no se modifica en este comportamiento
     */
    @Override
    public void alcanzarAlHeroe(Heroe heroe, Agenda agenda) {
        daniarAlHeroe(heroe,danio);
        volverAlInicio();
    }

    /** Guarda si le toca moverse este turno */
    @Override
    public String exportarEstado() {
        return String.valueOf(moverEsteTurno);
    }

    /** Restaura si le toca moverse este turno */
    @Override
    public void importarEstado(String estado) {
        moverEsteTurno = Boolean.parseBoolean(estado);
    }
}
