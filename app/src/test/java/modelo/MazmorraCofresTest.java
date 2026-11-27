package modelo;

import static modelo.MundosDePrueba.abierto;
import static modelo.MundosDePrueba.mazmorraDe;
import static org.junit.jupiter.api.Assertions.*;

import entidades.cofres.CofreDePuntos;
import org.junit.jupiter.api.Test;

/** Regla: pisar un cofre lo abre, suma 100 puntos y desaparece */
class MazmorraCofresTest {
    @Test
    void pisarUnCofreLoAbreYDesaparece() {
        Plano plano = abierto(false);
        CofreDePuntos cofre = new CofreDePuntos(new Posicion(2, 3));
        plano.agregarCofre(cofre);
        Mazmorra mazmorra = mazmorraDe(plano, 100);

        mazmorra.moverHeroe(Direccion.DERECHA);

        assertTrue(cofre.estaAbierto());
        assertEquals(1, mazmorra.getHeroe().getCofresAbiertos());
        assertNull(plano.cofreEn(new Posicion(2, 3)));
        assertEquals(0, plano.getCofres().size());
    }

    @Test
    void elCofreAbiertoSumaCienPuntosAlPuntaje() {
        Plano plano = abierto(false);
        plano.agregarCofre(new CofreDePuntos(new Posicion(2, 3)));
        Mazmorra mazmorra = mazmorraDe(plano, 100);

        mazmorra.moverHeroe(Direccion.DERECHA);

        // 1 cofre por 100 + 99 de energia por 2 - 1 paso
        assertEquals(297, mazmorra.calcularPuntaje().valor());
    }

    @Test
    void pisarUnaCeldaSinCofreNoAbreNada() {
        Plano plano = abierto(false);
        CofreDePuntos lejano = new CofreDePuntos(new Posicion(3, 3));
        plano.agregarCofre(lejano);
        Mazmorra mazmorra = mazmorraDe(plano, 100);

        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(0, mazmorra.getHeroe().getCofresAbiertos());
        assertFalse(lejano.estaAbierto());
        assertSame(lejano, plano.cofreEn(new Posicion(3, 3)));
    }
}
