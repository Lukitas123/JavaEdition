package modelo;

import estructuras.Lista;
import java.util.Random;

/**
 * Coloca las conexiones entre planos que el generador no conoce:
 * escaleras entre pisos de un tablero y pasillos entre tableros
 * Usa un Random recibido por parámetro para que, con la misma semilla, el resultado sea el mismo
 * Esta clase nunca imprime ni dibuja
 */
public class ColocadorDeConexiones {
    private final Random azar;

    public ColocadorDeConexiones(Random azar) {
        if (azar == null) {
            throw new IllegalArgumentException("El generador aleatorio no puede ser null");
        }
        this.azar = azar;
    }

    /**
     * Coloca una escalera entre el piso indicado y el siguiente del mismo tablero
     * Elige una celda que sea transitable en los dos planos y que no sea entrada, salida,
     * cofre, monstruo ni extremo de otra conexión. La escalera queda en la misma posición
     * en ambos planos
     *
     * @return la conexión creada, que también queda guardada en el mundo
     * @throws IllegalArgumentException si el tablero o el piso (o el siguiente) no tienen plano
     * @throws IllegalStateException    si no hay ninguna celda posible
     */
    public Conexion colocarEscalera(Mundo mundo, int tablero, int piso) {
        Plano abajo = mundo.planoEn(tablero, piso);
        Plano arriba = mundo.planoEn(tablero, piso + 1);

        Lista<Posicion> candidatas = celdasLibresEnAmbos(mundo, tablero, piso, abajo, arriba);
        if (candidatas.estaVacia()) {
            throw new IllegalStateException("No hay una celda libre en ambos planos para la escalera");
        }
        Posicion elegida = elegir(candidatas);

        Conexion escalera = new Conexion(
                new Ubicacion(tablero, piso, elegida),
                new Ubicacion(tablero, piso + 1, elegida));
        mundo.agregarConexion(escalera);
        return escalera;
    }

    private Lista<Posicion> celdasLibresEnAmbos(Mundo mundo, int tablero, int piso, Plano abajo, Plano arriba) {
        Lista<Posicion> candidatas = new Lista<>();
        for (int f = 1; f <= abajo.getFilas(); f++) {
            for (int c = 1; c <= abajo.getColumnas(); c++) {
                Posicion pos = new Posicion(f, c);
                if (esCeldaLibre(mundo, tablero, piso, abajo, pos)
                        && esCeldaLibre(mundo, tablero, piso + 1, arriba, pos)) {
                    candidatas.agregar(pos);
                }
            }
        }
        return candidatas;
    }

    /** Transitable y sin entrada, salida, cofre, monstruo ni conexión encima */
    private boolean esCeldaLibre(Mundo mundo, int tablero, int piso, Plano plano, Posicion pos) {
        return plano.esTransitable(pos)
                && !pos.equals(plano.getEntrada())
                && !pos.equals(plano.getSalida())
                && plano.cofreEn(pos) == null
                && plano.monstruoEn(pos) == null
                && mundo.conexionEn(new Ubicacion(tablero, piso, pos)) == null;
    }

    private Posicion elegir(Lista<Posicion> candidatas) {
        return candidatas.obtener(azar.nextInt(candidatas.cantidad()));
    }
}
