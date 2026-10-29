package modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HeroeTest {
    private Heroe heroe;

    @BeforeEach
    void armarHeroe() {
        heroe = new Heroe(100, new Ubicacion(1, 1, new Posicion(2, 2)));
    }

    @Test
    void empiezaConLaEnergiaIndicada() {
        assertEquals(100, heroe.getEnergia());
        assertEquals(0, heroe.getCofresAbiertos());
        assertFalse(heroe.estaSinEnergia());
    }

    @Test
    void perderEnergiaDescuenta() {
        heroe.perderEnergia(30);
        assertEquals(70, heroe.getEnergia());
    }

    @Test
    void laEnergiaNuncaBajaDeCero() {
        heroe.perderEnergia(500);
        assertEquals(0, heroe.getEnergia());
        assertTrue(heroe.estaSinEnergia());
    }

    @Test
    void ganarEnergiaSuma() {
        heroe.ganarEnergia(25);
        assertEquals(125, heroe.getEnergia());
    }

    @Test
    void cantidadesNegativasLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> heroe.perderEnergia(-1));
        assertThrows(IllegalArgumentException.class, () -> heroe.ganarEnergia(-1));
    }

    @Test
    void energiaInicialInvalidaLanzaExcepcion() {
        Ubicacion u = new Ubicacion(1, 1, new Posicion(2, 2));
        assertThrows(IllegalArgumentException.class, () -> new Heroe(0, u));
    }

    @Test
    void irAcambiaLaUbicacion() {
        Ubicacion destino = new Ubicacion(1, 2, new Posicion(3, 4));
        heroe.irA(destino);
        assertEquals(destino, heroe.getUbicacion());
    }

    @Test
    void registrarCofreAbiertoCuenta() {
        heroe.registrarCofreAbierto();
        heroe.registrarCofreAbierto();
        assertEquals(2, heroe.getCofresAbiertos());
    }

    @Test
    void inventarioGuardaObjetosRepetidos() {
        heroe.agregarObjeto("pocion");
        heroe.agregarObjeto("pocion");
        assertEquals(2, heroe.cantidadDeObjetos());
        assertTrue(heroe.quitarObjeto("pocion"));
        assertTrue(heroe.tieneObjeto("pocion"));
        assertTrue(heroe.quitarObjeto("pocion"));
        assertFalse(heroe.tieneObjeto("pocion"));
    }

    @Test
    void quitarObjetoQueNoTieneDevuelveFalse() {
        assertFalse(heroe.quitarObjeto("daga"));
    }

    @Test
    void quitarObjetoPorIndice() {
        heroe.agregarObjeto("daga");
        heroe.agregarObjeto("brujula");
        assertEquals("daga", heroe.quitarObjetoEn(0));
        assertEquals("brujula", heroe.objetoEn(0));
        assertEquals(1, heroe.cantidadDeObjetos());
    }

    @Test
    void efectosSeActivanYSeDesactivan() {
        assertFalse(heroe.tieneEfecto("zapatos"));
        heroe.activarEfecto("zapatos");
        assertTrue(heroe.tieneEfecto("zapatos"));
        heroe.desactivarEfecto("zapatos");
        assertFalse(heroe.tieneEfecto("zapatos"));
    }

    @Test
    void idsInvalidosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> heroe.agregarObjeto(null));
        assertThrows(IllegalArgumentException.class, () -> heroe.activarEfecto(" "));
    }
}
