package modelo;

public class EfectoPuente {
    private final boolean[][] cubiertas;
    private Integer vencimiento;

    public EfectoPuente (int filas, int columnas){
        cubiertas = new boolean[filas][columnas];
    }

    public Integer getVencimiento() {
        return vencimiento;
    }

    public boolean cubre(Posicion posicion){
        return cubiertas[posicion.fila() - 1][posicion.columna() - 1];
    }

    public Terreno terrenoVisible(Posicion posicion, Terreno base){
        if(cubre(posicion)){
            return Terreno.PISO;
        }
        return base;
    }

    public void retirarCobertura(Posicion posicion){
        cubiertas[posicion.fila() - 1][posicion.columna() - 1] = false;
    }

    public int activar(Plano plano, int pasoActual, int duracion){
        if (pasoActual < 0 || duracion <= 0) {
            throw new IllegalArgumentException("Paso o duracion invalidos");
        }

        int primerPasoLibre = pasoActual + 1;
        if (vencimiento != null) {
            primerPasoLibre = Math.max(primerPasoLibre, vencimiento);
        }
        vencimiento = primerPasoLibre + duracion;

        for (int fila = 1; fila <= plano.getFilas(); fila++) {
            for (int columna = 1; columna <= plano.getColumnas(); columna++) {
                Posicion posicion = new Posicion(fila, columna);
                Terreno base = plano.terrenoBaseEn(posicion);

                if (base == Terreno.AGUA || base == Terreno.LAVA) {
                    cubiertas[fila - 1][columna - 1] = true;
                }
            }
        }

        return vencimiento;
    }

    public void finalizarSiVencio(int pasoActual){
        if (pasoActual < 0) {
            throw new IllegalArgumentException("El paso no puede ser negativo");
        }

        if (vencimiento != null && pasoActual >= vencimiento) {
            for (int fila = 0; fila < cubiertas.length; fila++) {
                for (int columna = 0; columna < cubiertas[fila].length; columna++) {
                    cubiertas[fila][columna] = false;
                }
            }
            vencimiento = null;
        }
    }
    public void restaurar(Plano plano, Integer fin, boolean[][] guardadas) {
        if (fin != null && fin < 0) {
            throw new IllegalArgumentException("Vencimiento invalido");
        }

        // Una partida anterior puede no tener todavía datos del Puente.
        if (guardadas == null && fin != null) {
            throw new IllegalArgumentException("Faltan las casillas del Puente");
        }
        if (guardadas != null && guardadas.length != cubiertas.length) {
            throw new IllegalArgumentException("Filas del Puente invalidas");
        }

        for (int fila = 0; fila < cubiertas.length; fila++) {
            if (guardadas != null
                    && (guardadas[fila] == null
                    || guardadas[fila].length != cubiertas[fila].length)) {
                throw new IllegalArgumentException("Columnas del Puente invalidas");
            }

            for (int columna = 0; columna < cubiertas[fila].length; columna++) {
                boolean cubierta = guardadas != null && guardadas[fila][columna];
                Posicion posicion = new Posicion(fila + 1, columna + 1);
                Terreno base = plano.terrenoBaseEn(posicion);

                if (cubierta && (fin == null
                        || (base != Terreno.AGUA && base != Terreno.LAVA))) {
                    throw new IllegalArgumentException("Cobertura de Puente invalida");
                }
            }
        }

        for (int fila = 0; fila < cubiertas.length; fila++) {
            for (int columna = 0; columna < cubiertas[fila].length; columna++) {
                cubiertas[fila][columna] =
                        guardadas != null && guardadas[fila][columna];
            }
        }
        vencimiento = fin;
    }


}
