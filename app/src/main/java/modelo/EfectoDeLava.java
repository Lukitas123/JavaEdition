package modelo;


import java.util.HashMap;
import java.util.Map;

public class EfectoDeLava {
    private final int filas;
    private final int columnas;
    private final Map<Posicion, Integer> vencimientos = new HashMap<>();

    public EfectoDeLava(int filas, int columnas) {
        if (filas <= 0 || columnas <= 0) {
            throw new IllegalArgumentException("Dimensiones invalidas");
        }
        this.filas = filas;
        this.columnas = columnas;
    }

    public Integer getVencimiento(Posicion posicion) {
        validarPosicion(posicion);
        return vencimientos.get(posicion);
    }

    public boolean estaActiva(Posicion posicion) {
        return getVencimiento(posicion) != null;
    }

    public int activar(Posicion posicion, int pasoActual, int duracion) {
        validarPosicion(posicion);
        if (pasoActual < 0 || duracion <= 0) {
            throw new IllegalArgumentException("Paso o duracion invalidos");
        }

        int fin = pasoActual + 1 + duracion;
        vencimientos.put(posicion, fin);
        return fin;
    }

    public void cancelar(Posicion posicion) {
        validarPosicion(posicion);
        vencimientos.remove(posicion);
    }

    public void cancelarTodo() {
        vencimientos.clear();
    }

    public void finalizarSiVencio(int pasoActual) {
        if (pasoActual < 0) {
            throw new IllegalArgumentException("Paso invalido");
        }

        vencimientos.entrySet().removeIf(
                entrada -> entrada.getValue() <= pasoActual);
    }

    private void validarPosicion(Posicion posicion) {
        if (posicion == null || posicion.fila() < 1 || posicion.fila() > filas
                || posicion.columna() < 1 || posicion.columna() > columnas) {
            throw new IllegalArgumentException("Posicion fuera del plano");
        }
    }
    public Map<Posicion, Integer> getVencimientos() {
        return Map.copyOf(vencimientos);
    }

    public void restaurar(Plano plano, Map<Posicion, Integer> guardados) {
        Map<Posicion, Integer> copia = guardados;
        if(copia == null){
            copia = new HashMap<>();
        }else{
            copia = new HashMap<>(guardados);
        }


        for (Map.Entry<Posicion, Integer> entrada : copia.entrySet()) {
            Posicion posicion = entrada.getKey();
            Integer fin = entrada.getValue();

            validarPosicion(posicion);
            if (fin == null || fin < 0
                    || plano.terrenoBaseEn(posicion) == Terreno.PARED
                    || plano.estaCubiertaPorPuente(posicion)) {
                throw new IllegalArgumentException("Lava guardada invalida");
            }
        }

        vencimientos.clear();
        vencimientos.putAll(copia);
    }
}