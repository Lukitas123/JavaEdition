package entidades.objetos;

import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PocionDeEnergiaTest {

    @Test
    void usarRecuperaLaEnergiaConfigurada() {
        Posicion posicion = new Posicion(1, 1);
        Plano plano = new Plano(
                new Terreno[][] {{Terreno.PISO}}, posicion, null);
        Heroe heroe = new Heroe(60, new Ubicacion(1, 1, posicion));
        PocionDeEnergia pocion = new PocionDeEnergia(25);

        pocion.usar(heroe, plano, new Agenda());

        assertEquals(85, heroe.getEnergia());
    }

    @Test
    void rechazaUnaCantidadDeEnergiaNoPositiva() {
        assertThrows(IllegalArgumentException.class,
                () -> new PocionDeEnergia(0));
        assertThrows(IllegalArgumentException.class,
                () -> new PocionDeEnergia(-1));
    }
}