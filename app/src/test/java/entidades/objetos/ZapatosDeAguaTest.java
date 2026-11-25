
package entidades.objetos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import entidades.cofres.CofreZapatosDeAgua;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.TipoEvento;
import modelo.Ubicacion;

class ZapatosDeAguaTest {

    @Test
    void elCofreEntregaLosZapatosSinActivarlos() {
        Posicion posicion = new Posicion(1, 1);
        Plano plano = new Plano(new Terreno[][] {{Terreno.PISO}}, posicion, null);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, posicion));
        Agenda agenda = new Agenda();
        CofreZapatosDeAgua cofre = new CofreZapatosDeAgua(posicion);
        plano.agregarCofre(cofre);

        cofre.abrir(heroe, plano, agenda);

        assertEquals(1, heroe.cantidadDe(ZapatosDeAgua.ID_OBJETO));
        assertFalse(heroe.tieneEfecto(ZapatosDeAgua.ID_EFECTO));
        assertEquals(1, heroe.getCofresAbiertos());
        assertNull(plano.cofreEn(posicion));
        assertEquals(0, agenda.cantidadDeEventos());
    }

    @Test
    void usarOtroParSumaVeintePasos() {
        Posicion posicion = new Posicion(1, 1);
        Plano plano = new Plano(new Terreno[][] {{Terreno.PISO}}, posicion, null);
        Heroe heroe = new Heroe(100, new Ubicacion(1, 1, posicion));
        Agenda agenda = new Agenda();
        ZapatosDeAgua zapatos = new ZapatosDeAgua();

        avanzar(agenda, 10);
        zapatos.usar(heroe, plano, agenda);
        assertEquals(Integer.valueOf(31), heroe.vencimientoDe(ZapatosDeAgua.ID_EFECTO));

        avanzar(agenda, 2); // paso 12
        zapatos.usar(heroe, plano, agenda);
        assertEquals(Integer.valueOf(51), heroe.vencimientoDe(ZapatosDeAgua.ID_EFECTO));

        avanzar(agenda, 19); // paso 31
        assertEquals(TipoEvento.FIN_ZAPATOS_DE_AGUA, agenda.sacarProximo().tipo());
        heroe.finalizarEfectoSiVencio(ZapatosDeAgua.ID_EFECTO, agenda.getPasoActual());
        assertTrue(heroe.tieneEfecto(ZapatosDeAgua.ID_EFECTO));

        avanzar(agenda, 20); // paso 51
        assertEquals(TipoEvento.FIN_ZAPATOS_DE_AGUA, agenda.sacarProximo().tipo());
        heroe.finalizarEfectoSiVencio(ZapatosDeAgua.ID_EFECTO, agenda.getPasoActual());
        assertFalse(heroe.tieneEfecto(ZapatosDeAgua.ID_EFECTO));
    }

    private void avanzar(Agenda agenda, int pasos) {
        for (int i = 0; i < pasos; i++) {
            agenda.avanzarPaso();
        }
    }
}