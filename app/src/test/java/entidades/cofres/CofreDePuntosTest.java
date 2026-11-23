package entidades.cofres;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CofreDePuntosTest {
    private Plano plano;
    private Heroe heroe;
    private Agenda agenda;
    private CofreDePuntos cofre;

    @BeforeEach
    void preparar() {
        Posicion posicion = new Posicion(1, 1);
        plano = new Plano(
                new Terreno[][] {{Terreno.PISO}}, posicion, null);
        heroe = new Heroe(100, new Ubicacion(1, 1, posicion));
        agenda = new Agenda();
        cofre = new CofreDePuntos(posicion);
        plano.agregarCofre(cofre);
    }

    @Test
    void empiezaCerrado() {
        assertFalse(cofre.estaAbierto());
        assertEquals(new Posicion(1, 1), cofre.getPosicion());
    }

    @Test
    void abrirRegistraUnaAperturaYLoRetiraDelPlano() {
        cofre.abrir(heroe, plano, agenda);

        assertTrue(cofre.estaAbierto());
        assertEquals(1, heroe.getCofresAbiertos());
        assertNull(plano.cofreEn(cofre.getPosicion()));
        assertEquals(0, plano.getCofres().cantidad());
        assertEquals(100, heroe.getEnergia());
        assertEquals(0, heroe.cantidadDeObjetos());
        assertEquals(0, agenda.cantidadDeEventos());
    }

    @Test
    void abrirDosVecesNoDuplicaElConteo() {
        cofre.abrir(heroe, plano, agenda);
        cofre.abrir(heroe, plano, agenda);

        assertEquals(1, heroe.getCofresAbiertos());
        assertEquals(0, plano.getCofres().cantidad());
    }

    @Test
    void rechazaArgumentosNulosSinAbrirse() {
        assertThrows(IllegalArgumentException.class,
                () -> cofre.abrir(null, plano, agenda));
        assertThrows(IllegalArgumentException.class,
                () -> cofre.abrir(heroe, null, agenda));
        assertThrows(IllegalArgumentException.class,
                () -> cofre.abrir(heroe, plano, null));

        assertFalse(cofre.estaAbierto());
        assertEquals(0, heroe.getCofresAbiertos());
        assertSame(cofre, plano.cofreEn(cofre.getPosicion()));
    }

    @Test
    void rechazaPosicionInicialNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new CofreDePuntos(null));
    }

    @Test
    void cofreNoRegistradoNoPuedeAbrirse() {
        plano.quitarCofre(cofre);

        assertThrows(IllegalStateException.class,
                () -> cofre.abrir(heroe, plano, agenda));

        assertFalse(cofre.estaAbierto());
        assertEquals(0, heroe.getCofresAbiertos());
        assertEquals(100, heroe.getEnergia());
        assertEquals(0, agenda.cantidadDeEventos());
    }
}