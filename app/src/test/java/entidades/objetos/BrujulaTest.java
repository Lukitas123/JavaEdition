package entidades.objetos;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import entidades.cofres.CofreDeBrujula;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;
import modelo.Ubicacion;

class BrujulaTest {
    private final Posicion posicion = new Posicion(2, 2);
    private final Heroe heroe = new Heroe(
            100, new Ubicacion(1, 1, posicion));
    private final Agenda agenda = new Agenda();
    private final Brujula brujula = new Brujula();

    private Plano crearPlano(Posicion salida) {
        return new Plano(new Terreno[][] {
                {Terreno.PISO, Terreno.PISO, Terreno.PISO},
                {Terreno.PISO, Terreno.PISO, Terreno.PISO},
                {Terreno.PISO, Terreno.PISO, Terreno.PISO}
        }, posicion, salida);
    }

    @Test
    void elCofreEntregaUnaBrujula() {
        Plano plano = crearPlano(null);
        CofreDeBrujula cofre = new CofreDeBrujula(posicion);
        plano.agregarCofre(cofre);

        cofre.abrir(heroe, plano, agenda);

        assertEquals(1, heroe.cantidadDe(Brujula.ID_OBJETO));
        assertEquals(1, heroe.getCofresAbiertos());
        assertNull(plano.cofreEn(posicion));
    }

    @Test
    void indicaLaOrientacionGeneralDeLaSalida() {
        Posicion[] salidas = {
                new Posicion(1, 2), new Posicion(3, 2),
                new Posicion(2, 1), new Posicion(2, 3),
                new Posicion(1, 1), new Posicion(1, 3),
                new Posicion(3, 1), new Posicion(3, 3)
        };
        String[] orientaciones = {
                "arriba", "abajo", "izquierda", "derecha",
                "arriba a la izquierda", "arriba a la derecha",
                "abajo a la izquierda", "abajo a la derecha"
        };

        for (int i = 0; i < salidas.length; i++) {
            ResultadoUso resultado = brujula.usar(
                    heroe, crearPlano(salidas[i]), agenda);

            assertTrue(resultado.seConsume());
            assertEquals("La salida esta hacia " + orientaciones[i],
                    resultado.mensaje());
        }
    }

    @Test
    void avisaSiLaSalidaNoEstaEnElPlanoYSeConsume() {
        ResultadoUso resultado = brujula.usar(
                heroe, crearPlano(null), agenda);

        assertTrue(resultado.seConsume());
        assertEquals("La salida no esta en este plano.", resultado.mensaje());
    }

    @Test
    void avisaSiElHeroeEstaEnLaSalida() {
        ResultadoUso resultado = brujula.usar(
                heroe, crearPlano(posicion), agenda);

        assertTrue(resultado.seConsume());
        assertEquals("Estas en la salida.", resultado.mensaje());
    }
}