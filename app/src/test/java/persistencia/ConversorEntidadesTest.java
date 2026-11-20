package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import org.junit.jupiter.api.Test;

import entidades.monstruos.Errante;
import entidades.monstruos.Fantasma;
import entidades.monstruos.Monstruo;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;

public class ConversorEntidadesTest {

    private static Terreno[][] terrenoDePrueba() {
        Terreno p = Terreno.PARED;
        Terreno o = Terreno.PISO;
        return new Terreno[][] {
            {p, p, p, p},
            {p, o, o, p},
            {p, o, o, p},
            {p, p, p, p}
        };
    }

    /** Un monstruo inactivo conserva su tipo y su estado inactivo */
    @Test
    void monstruoInactivoSigueInactivo() {
        Errante original = new Errante(new Posicion(2, 2), new Random(1));
        original.desactivar();

        Monstruo restaurado = ConversorEntidades.aMonstruo(ConversorEntidades.monstruoAGuardado(original));

        assertEquals("Errante", restaurado.getTipo());
        assertFalse(restaurado.estaActivo());
        assertEquals(new Posicion(2, 2), restaurado.getPosicionInicial());
    }

    /** El fantasma conserva si le toca moverse este turno */
    @Test
    void fantasmaConservaSuEstado() {
        Plano plano = new Plano(terrenoDePrueba(), new Posicion(2, 2), null);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, new Posicion(3, 3)));
        Fantasma original = new Fantasma(new Posicion(2, 2), new Random(1));
        assertEquals("false", original.exportarEstado());

        original.actuar(plano, heroe, new Agenda());

        Monstruo restaurado = ConversorEntidades.aMonstruo(ConversorEntidades.monstruoAGuardado(original));

        assertEquals("true", restaurado.exportarEstado());
    }
}
