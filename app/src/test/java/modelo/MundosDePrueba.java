package modelo;

/**
 * Piezas compartidas por los tests de Mazmorra: planos armados desde texto y
 * partidas listas para jugar. Los planos usan E para la entrada y S para la salida
 */
final class MundosDePrueba {

    private MundosDePrueba() {
    }

    /** Arma un plano desde cadenas (La E es la entrada y la S, si hay, la salida) */
    static Plano desdeCadenas(String... filas) {
        Terreno[][] t = new Terreno[filas.length][filas[0].length()];
        Posicion entrada = null;
        Posicion salida = null;
        for (int f = 0; f < filas.length; f++) {
            for (int c = 0; c < filas[f].length(); c++) {
                char simbolo = filas[f].charAt(c);
                t[f][c] = Terreno.desdeSimbolo(simbolo);
                if (simbolo == 'E') {
                    entrada = new Posicion(f + 1, c + 1);
                }
                if (simbolo == 'S') {
                    salida = new Posicion(f + 1, c + 1);
                }
            }
        }
        return new Plano(t, entrada, salida);
    }

    /** Plano de 5x5 con el interior libre, entrada en (2,2) y, si se pide, salida en (4,4) */
    static Plano abierto(boolean conSalida) {
        return desdeCadenas(
                "#####",
                "#E..#",
                "#...#",
                conSalida ? "#..S#" : "#...#",
                "#####");
    }

    /** Partida de un solo plano, con los costos por defecto (piso 1, agua 3, lava 8) */
    static Mazmorra mazmorraDe(Plano plano, int energia) {
        return mazmorraDe(plano, energia, new Costo(1, 3, 8));
    }

    /** Partida de un solo plano con costos a elección */
    static Mazmorra mazmorraDe(Plano plano, int energia, Costo costos) {
        Mundo mundo = new Mundo(1, 1);
        mundo.agregarPlano(1, 1, plano);
        return new Mazmorra(Dificultad.FACIL, mundo, new Heroe(energia, mundo.getInicio()),
                new Agenda(), costos, ParametrosDePrueba.ENTIDADES);
    }

    /** Partida sobre un mundo ya armado, con los costos por defecto */
    static Mazmorra mazmorraDe(Mundo mundo, int energia) {
        return new Mazmorra(Dificultad.MEDIO, mundo, new Heroe(energia, mundo.getInicio()),
                new Agenda(), new Costo(1, 3, 8), ParametrosDePrueba.ENTIDADES);
    }

    /** Mundo de 1 tablero y 2 pisos unidos por una escalera en la celda (3,3). La salida está en el piso 2 */
    static Mundo mundoConEscalera() {
        Mundo mundo = new Mundo(1, 2);
        mundo.agregarPlano(1, 1, abierto(false));
        mundo.agregarPlano(1, 2, abierto(true));
        mundo.agregarConexion(new Conexion(
                new Ubicacion(1, 1, new Posicion(3, 3)),
                new Ubicacion(1, 2, new Posicion(3, 3))));
        return mundo;
    }
}
