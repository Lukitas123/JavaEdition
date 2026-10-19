package modelo;

/**
 * héroe: guarda su energía, dónde está y cuántos cofres abrió
 * falta inventario y efectos activos
 */
public class Heroe {
    private int energia;
    private Ubicacion ubicacion;
    private int cofresAbiertos;

    /**
     * @param energiaInicial debe ser mayor a 0
     * @param ubicacion      dónde empieza (no puede ser null)
     */
    public Heroe(int energiaInicial, Ubicacion ubicacion) {
        if (energiaInicial <= 0) {
            throw new IllegalArgumentException("La energia inicial debe ser positiva");
        }
        if (ubicacion == null) {
            throw new IllegalArgumentException("La ubicacion no puede ser null");
        }
        this.energia = energiaInicial;
        this.ubicacion = ubicacion;
        this.cofresAbiertos = 0;
    }

    public int getEnergia() {
        return energia;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public int getCofresAbiertos() {
        return cofresAbiertos;
    }

    /** Resta energía. Nunca baja de 0. */
    public void perderEnergia(int cantidad) {
        validarNoNegativa(cantidad);
        energia = Math.max(0, energia - cantidad);
    }

    public void ganarEnergia(int cantidad) {
        validarNoNegativa(cantidad);
        energia += cantidad;
    }

    public boolean estaSinEnergia() {
        return energia == 0;
    }

    public void irA(Ubicacion destino) {
        if (destino == null) {
            throw new IllegalArgumentException("El destino no puede ser null");
        }
        this.ubicacion = destino;
    }

    public void registrarCofreAbierto() {
        cofresAbiertos++;
    }

    private void validarNoNegativa(int cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
    }
}