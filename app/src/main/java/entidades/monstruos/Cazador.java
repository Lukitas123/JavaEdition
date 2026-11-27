package entidades.monstruos;

import java.util.List;
import modelo.*;

public class Cazador extends Monstruo{
    private final Costo costo;

    public Cazador(Posicion posicion, Costo costo){
        super(posicion);
        if (costo == null
                || costo.piso() < 0
                || costo.agua() < 0
                || costo.lava() < 0) {
            throw new IllegalArgumentException("Los costos no pueden ser nulos ni negativos");
        }
        this.costo = costo;
    }

    /**
     * Elige la siguiente celda hacia el héroe por el camino de menor costo
     * de terreno. Si no hay camino, permanece en su posición actual.
     *
     * @param plano plano donde se mueve el cazador
     * @param posicionHeroe posición del héroe en el plano
     * @return la próxima celda del camino o la posición actual
     * @throws IllegalArgumentException si plano o posicionHeroe son null
     */
    @Override
    public Posicion decidirMovimiento(Plano plano, Posicion posicionHeroe) {
        if(plano == null || posicionHeroe == null){
            throw new IllegalArgumentException("El plano y la posicion del heroe son obligatorios");
        }
        GrafoDelPlano grafo = new GrafoDelPlano(plano, costo);
        List<Posicion> camino = grafo.caminoMinimo(getPosicion(), posicionHeroe);

        if(camino.size()<2){
            return getPosicion();
        }
        return camino.get(1);
    }

    @Override
    public void alcanzarAlHeroe(Heroe heroe, Agenda agenda) {
        daniarAlHeroe(heroe, 30);
        volverAlInicio();
    }
}
