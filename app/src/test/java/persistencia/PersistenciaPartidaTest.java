package persistencia;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import entidades.cofres.CofreDePuntos;
import modelo.Agenda;
import modelo.Dificultad;
import modelo.EstadoPartida;
import modelo.Heroe;
import modelo.Mundo;
import modelo.Plano;
import modelo.Posicion;
import modelo.Terreno;

public class PersistenciaPartidaTest {

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

    /** Una partida guardada en un archivo y cargada de nuevo conserva todo su estado */
    @Test
    void partidaGuardadaYCargadaConservaElEstado(@TempDir Path carpeta) {
        String ruta = carpeta.resolve("partida.json").toString();

        Mundo mundo = new Mundo(1, 1);
        Plano plano = new Plano(terrenoDePrueba(), new Posicion(2, 2), new Posicion(3, 3));
        plano.agregarCofre(new CofreDePuntos(new Posicion(3, 2)));
        mundo.agregarPlano(1, 1, plano);

        Heroe heroe = new Heroe(100, mundo.getInicio());
        heroe.perderEnergia(15);
        heroe.agregarObjeto("pocion");
        heroe.activarEfecto("zapatos");

        Agenda agenda = new Agenda();
        agenda.avanzarPaso();
        agenda.programar(5, "zapatos", null);

        MazmorraGuardada original = ConversorMazmorra.aGuardada(
                Dificultad.FACIL, EstadoPartida.EN_CURSO, mundo, heroe, agenda);
        PersistenciaMazmorra.guardar(original, ruta);

        Mazmorra restaurada = ConversorMazmorra.aMazmorra(PersistenciaMazmorra.cargar(ruta));

        assertEquals(Dificultad.FACIL, restaurada.dificultad());
        assertEquals(EstadoPartida.EN_CURSO, restaurada.estado());
        assertEquals(85, restaurada.heroe().getEnergia());
        assertEquals(heroe.getUbicacion(), restaurada.heroe().getUbicacion());
        assertEquals(1, restaurada.heroe().cantidadDe("pocion"));
        assertTrue(restaurada.heroe().tieneEfecto("zapatos"));
        assertEquals(1, restaurada.agenda().getPasoActual());
        assertEquals(1, restaurada.agenda().cantidadDeEventos());
        Plano planoActual = restaurada.mundo().planoEn(restaurada.heroe().getUbicacion());
        assertEquals(1, planoActual.getCofres().cantidad());
    }

    /** Cargar sin archivo tira error */
    @Test
    void cargarSinArchivoRechaza() {
        assertThrows(IllegalArgumentException.class, () -> PersistenciaMazmorra.cargar("no_existe.json"));
    }
}
