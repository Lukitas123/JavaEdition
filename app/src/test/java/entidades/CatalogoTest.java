package entidades;

import entidades.cofres.Cofre;
import entidades.cofres.CofreDePocion;
import entidades.cofres.CofreDePuntos;
import java.util.Random;

import entidades.cofres.TrampaDeEnergia;
import modelo.Posicion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CatalogoTest {

    @Test
    void creaInstanciasIndependientesEnSusPosiciones() {
        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("puntos", CofreDePuntos::new);

        Cofre primero = catalogo.crear("puntos", new Posicion(1, 1));
        Cofre segundo = catalogo.crear("puntos", new Posicion(2, 3));

        assertInstanceOf(CofreDePuntos.class, primero);
        assertNotSame(primero, segundo);
        assertEquals(new Posicion(1, 1), primero.getPosicion());
        assertEquals(new Posicion(2, 3), segundo.getPosicion());
    }

    @Test
    void rechazaTiposDuplicadosODesconocidos() {
        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("puntos", CofreDePuntos::new);

        assertThrows(IllegalArgumentException.class,
                () -> catalogo.registrar("puntos", CofreDePocion::new));
        assertThrows(IllegalArgumentException.class,
                () -> catalogo.crear("desconocido", new Posicion(1, 1)));
    }

    @Test
    void rechazaSorteoConCatalogoVacio() {
        Catalogo<Cofre> catalogo = new Catalogo<>();

        assertThrows(IllegalStateException.class,
                () -> catalogo.sortear(new Posicion(1, 1), new Random(1)));
    }

    @Test
    void mismaSemillaProduceLaMismaSecuenciaDeTipos() {
        Catalogo<Cofre> catalogo = new Catalogo<>();
        catalogo.registrar("puntos", CofreDePuntos::new);
        catalogo.registrar("pocion", CofreDePocion::new);

        Random primero = new Random(42);
        Random segundo = new Random(42);
        Posicion posicion = new Posicion(1, 1);

        for (int i = 0; i < 20; i++) {
            Cofre cofreA = catalogo.sortear(posicion, primero);
            Cofre cofreB = catalogo.sortear(posicion, segundo);

            assertEquals(cofreA.getClass(), cofreB.getClass());
        }
    }

    private record DatosTrampa(Posicion posicion, int danio) {}

}