package entidades.cofres;

import entidades.objetos.PocionDeEnergia;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CofreDePocionTest {

    @Test
    void abrirAgregaUnaPocionSinRecuperarEnergia() {
        Posicion posicion = new Posicion(1, 1);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO}}, posicion, null);
        Heroe heroe = new Heroe(60, new Ubicacion(1, 1, posicion));
        CofreDePocion cofre = new CofreDePocion(posicion);
        Agenda agenda = new Agenda();
        plano.agregarCofre(cofre);

        cofre.abrir(heroe, plano, agenda);

        assertTrue(heroe.tieneObjeto(PocionDeEnergia.id));
        assertEquals(1, heroe.cantidadDeObjetos());
        assertEquals(60, heroe.getEnergia());
        assertEquals(1, heroe.getCofresAbiertos());
        assertNull(plano.cofreEn(posicion));

        cofre.abrir(heroe, plano, agenda);

        assertEquals(1, heroe.cantidadDeObjetos());
        assertEquals(1, heroe.getCofresAbiertos());
    }
}
