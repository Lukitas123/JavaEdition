package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

import entidades.cofres.Cofre;
import entidades.cofres.TrampaDeEnergia;
import modelo.*;
import org.junit.jupiter.api.Test;

import entidades.monstruos.Errante;
import entidades.monstruos.Fantasma;
import entidades.monstruos.Monstruo;
import entidades.Catalogo;

public class ConversorEntidadesTest {

    private static Catalogo<Monstruo> pruebaMonstruos(){
        return CatalogosDePrueba.monstruos();
    }


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

        Monstruo restaurado = ConversorEntidades.aMonstruo(ConversorEntidades.monstruoAGuardado(original), pruebaMonstruos());

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

        Monstruo restaurado = ConversorEntidades.aMonstruo(ConversorEntidades.monstruoAGuardado(original), pruebaMonstruos());

        assertEquals("true", restaurado.exportarEstado());
    }

    @Test
    void catalogoRestauraTrampaConElDanioDeLaPartida() {
        TrampaDeEnergia original =
                new TrampaDeEnergia(new Posicion(2, 2), 40);
        CofreGuardado guardado = ConversorEntidades.cofreAGuardado(original);

        Cofre restaurado = ConversorEntidades.aCofre(
                guardado, CatalogosDePrueba.cofres(40));

        assertInstanceOf(TrampaDeEnergia.class, restaurado);
        assertEquals(40, restaurado.getDanio());
    }

    @Test
    void rechazaCatalogoConOtroDanio() {
        TrampaDeEnergia original =
                new TrampaDeEnergia(new Posicion(2, 2), 40);
        CofreGuardado guardado = ConversorEntidades.cofreAGuardado(original);

        assertThrows(IllegalArgumentException.class,
                () -> ConversorEntidades.aCofre(
                        guardado, CatalogosDePrueba.cofres(25)));
    }
}
