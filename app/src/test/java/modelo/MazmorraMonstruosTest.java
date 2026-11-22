package modelo;

import static modelo.MundosDePrueba.abierto;
import static modelo.MundosDePrueba.mazmorraDe;
import static modelo.MundosDePrueba.mundoConEscalera;
import static org.junit.jupiter.api.Assertions.*;

import entidades.monstruos.Monstruo;
import org.junit.jupiter.api.Test;

/** Regla: los monstruos actúan después del héroe, solo en su plano, y al alcanzarlo hacen su efecto */
class MazmorraMonstruosTest {
    /** Monstruo quieto que cuenta cuántas veces decidió y, al alcanzar al héroe, le quita 30 */
    private static class MonstruoDePrueba extends Monstruo {
        private int decisiones = 0;

        MonstruoDePrueba(Posicion posicion) {
            super(posicion);
        }

        @Override
        public Posicion decidirMovimiento(Plano plano, Posicion posicionHeroe) {
            decisiones++;
            return getPosicion();
        }

        @Override
        public void alcanzarAlHeroe(Heroe heroe, Agenda agenda) {
            heroe.perderEnergia(30);
        }
    }

    @Test
    void alAlcanzarAlHeroeLeQuitaTreintaDeEnergia() {
        Plano plano = abierto(false);
        plano.agregarMonstruo(new MonstruoDePrueba(new Posicion(2, 3)));
        Mazmorra mazmorra = mazmorraDe(plano, 100);

        mazmorra.moverHeroe(Direccion.DERECHA);

        // 1 de costo del piso y 30 del monstruo
        assertEquals(69, mazmorra.getHeroe().getEnergia());
    }

    @Test
    void esperarHaceActuarALosMonstruosSinGastarEnergia() {
        Plano plano = abierto(false);
        MonstruoDePrueba monstruo = new MonstruoDePrueba(new Posicion(2, 4));
        plano.agregarMonstruo(monstruo);
        Mazmorra mazmorra = mazmorraDe(plano, 100);

        mazmorra.esperar();

        assertEquals(1, monstruo.decisiones);
        assertEquals(100, mazmorra.getHeroe().getEnergia());
        assertEquals(1, mazmorra.getAgenda().getPasoActual());
    }

    @Test
    void soloActuanLosMonstruosDelPlanoDondeEstaElHeroe() {
        Mundo mundo = mundoConEscalera();
        MonstruoDePrueba delPisoDeArriba = new MonstruoDePrueba(new Posicion(2, 4));
        mundo.planoEn(1, 2).agregarMonstruo(delPisoDeArriba);
        Mazmorra mazmorra = mazmorraDe(mundo, 100);

        mazmorra.moverHeroe(Direccion.ABAJO);
        assertEquals(0, delPisoDeArriba.decisiones);

        mazmorra.moverHeroe(Direccion.DERECHA); // pisa la escalera y pasa al piso 2
        assertEquals(1, delPisoDeArriba.decisiones);
    }

    @Test
    void siUnMonstruoLeQuitaLaUltimaEnergiaSePierde() {
        Plano plano = abierto(false);
        plano.agregarMonstruo(new MonstruoDePrueba(new Posicion(2, 3)));
        Mazmorra mazmorra = mazmorraDe(plano, 30);

        mazmorra.moverHeroe(Direccion.DERECHA);

        assertEquals(0, mazmorra.getHeroe().getEnergia());
        assertEquals(EstadoPartida.PERDIDA, mazmorra.getEstado());
    }
}
