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
    void quitarObjetoNoAfectaLosOtrosTipos() {
        heroe.agregarObjeto("daga");
        heroe.agregarObjeto("brujula");

        assertTrue(heroe.quitarObjeto("daga"));

        assertFalse(heroe.tieneObjeto("daga"));
        assertTrue(heroe.tieneObjeto("brujula"));
        assertEquals(1, heroe.cantidadDeObjetos());
    }

    @Test
    void efectosSeActivanYSeDesactivan() {
        assertFalse(heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));
        heroe.activarEfecto(EfectoHeroe.ZAPATOS_DE_AGUA, 20);
        assertTrue(heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));
        heroe.desactivarEfecto(EfectoHeroe.ZAPATOS_DE_AGUA);
        assertFalse(heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA));
    }

    @Test
    void idsInvalidosLanzanExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> heroe.agregarObjeto(null));
        assertThrows(IllegalArgumentException.class, () -> heroe.activarEfecto(null, 20));
    }
    @Test
    void cuentaUnidadesPorTipoYEliminaLaUltima() {
        heroe.agregarObjeto("pocion");
        heroe.agregarObjeto("pocion");
        heroe.agregarObjeto("daga");

        assertEquals(2, heroe.cantidadDe("pocion"));
        assertEquals(1, heroe.cantidadDe("daga"));
        assertEquals(0, heroe.cantidadDe("brujula"));
        assertEquals(3, heroe.cantidadDeObjetos());

        assertTrue(heroe.quitarObjeto("pocion"));
        assertEquals(1, heroe.cantidadDe("pocion"));

        assertTrue(heroe.quitarObjeto("pocion"));
        assertFalse(heroe.getInventario().containsKey("pocion"));
        assertFalse(heroe.quitarObjeto("pocion"));

        assertEquals(1, heroe.cantidadDeObjetos());
    }

    @Test
    void consultarInventarioNoPermiteModificarElEstadoDelHeroe() {
        heroe.agregarObjeto("pocion");

        assertThrows(UnsupportedOperationException.class,
                () -> heroe.getInventario().put("daga", 1));

        assertFalse(heroe.tieneObjeto("daga"));
        assertEquals(1, heroe.cantidadDe("pocion"));
    }
}
