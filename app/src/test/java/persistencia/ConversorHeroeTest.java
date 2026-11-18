package persistencia;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import modelo.Heroe;
import modelo.Posicion;
import modelo.Ubicacion;

public class ConversorHeroeTest {

    /** Convierte un heroe a un HeroeGuardado y este conserva todo su estado */
    @Test
    void heroeSobreviveAConversion() {
        Ubicacion ubicacion = new Ubicacion(1, 2, new Posicion(3, 4));
        Heroe original = new Heroe(100, ubicacion);
        original.perderEnergia(20);
        original.registrarCofreAbierto();
        original.registrarCofreAbierto();
        original.agregarObjeto("pocion");
        original.agregarObjeto("daga");
        original.activarEfecto("zapatos");

        Heroe restaurado = ConversorHeroe.aHeroe(ConversorHeroe.aGuardado(original));

        assertEquals(80, restaurado.getEnergia());
        assertEquals(ubicacion, restaurado.getUbicacion());
        assertEquals(2, restaurado.getCofresAbiertos());
        assertEquals(2, restaurado.cantidadDeObjetos());
        assertEquals("pocion", restaurado.objetoEn(0));
        assertEquals("daga", restaurado.objetoEn(1));
        assertTrue(restaurado.tieneEfecto("zapatos"));
        assertFalse(restaurado.tieneEfecto("escudo"));
    }
}
