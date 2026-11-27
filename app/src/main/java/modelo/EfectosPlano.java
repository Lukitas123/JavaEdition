package modelo;

import java.util.Map;

/**
 * Gestiona los efectos temporales de un unico plano.
 * Los eventos de la agenda indican cuando finalizar los efectos.
 */
public class EfectosPlano {
    private final Plano plano;
    private Integer finSuenoMonstruos;
    private final EfectoPuente puente;
    private final EfectoDeLava lava;

    public EfectosPlano(Plano plano) {
        if (plano == null) {
            throw new IllegalArgumentException("El plano no puede ser null");
        }
        this.plano = plano;
        puente = new EfectoPuente(plano.getFilas(), plano.getColumnas());
        lava = new EfectoDeLava(plano.getFilas(), plano.getColumnas());
    }

    public Terreno terrenoVisible(Posicion posicion) {
        Terreno base = plano.terrenoBaseEn(posicion);
        if (lava.estaActiva(posicion)) {
            return Terreno.LAVA;
        }
        return puente.terrenoVisible(posicion, base);
    }

    public void cancelarEn(Posicion posicion) {
        validarPosicion(posicion);
        puente.retirarCobertura(posicion);
        lava.cancelar(posicion);
    }

    // -------------- Dormir Monstruos -----------
    public boolean monstruosDormidos() {
        return finSuenoMonstruos != null;
    }

    public Integer getFinSuenoMonstruos() {
        return finSuenoMonstruos;
    }

    public int dormirMonstruos(int pasoActual, int duracion) {
        if (pasoActual < 0 || duracion <= 0) {
            throw new IllegalArgumentException("Paso o duracion invalidos");
        }

        int primerPasoLibre = pasoActual + 1;
        if (finSuenoMonstruos != null) {
            primerPasoLibre = Math.max(primerPasoLibre, finSuenoMonstruos);
        }

        finSuenoMonstruos = primerPasoLibre + duracion;
        return finSuenoMonstruos;
    }

    public void despertarMonstruosSiVencio(int pasoActual) {
        if (pasoActual < 0) {
            throw new IllegalArgumentException("El paso no puede ser negativo");
        }
        if (finSuenoMonstruos != null && pasoActual >= finSuenoMonstruos) {
            finSuenoMonstruos = null;
        }
    }

    public void restaurarFinSuenoMonstruos(Integer vencimiento) {
        if (vencimiento != null && vencimiento < 0) {
            throw new IllegalArgumentException(
                    "El vencimiento no puede ser negativo");
        }
        finSuenoMonstruos = vencimiento;
    }

    // ------------ Puente -----------
    public Integer getFinPuente() {
        return puente.getVencimiento();
    }

    public boolean estaCubiertaPorPuente(Posicion posicion) {
        validarPosicion(posicion);
        return puente.cubre(posicion);
    }

    public int activarPuente(int pasoActual, int duracion) {
        int vencimiento = puente.activar(plano, pasoActual, duracion);
        lava.cancelarTodo();
        return vencimiento;
    }

    public void finalizarPuenteSiVencio(int pasoActual) {
        puente.finalizarSiVencio(pasoActual);
    }

    public void restaurarPuente(Integer fin, boolean[][] cubiertas) {
        puente.restaurar(plano, fin, cubiertas);
    }


    // --------------- Lava ------------------
    public int activarLava(
            Posicion posicion, int pasoActual, int duracion) {
        if (!plano.esTransitable(posicion)) {
            throw new IllegalArgumentException(
                    "La celda debe ser transitable");
        }

        int vencimiento = lava.activar(posicion, pasoActual, duracion);
        puente.retirarCobertura(posicion);
        return vencimiento;
    }

    public void finalizarLavaSiVencio(int pasoActual) {
        lava.finalizarSiVencio(pasoActual);
    }

    public Map<Posicion, Integer> getVencimientosLava() {
        return lava.getVencimientos();
    }

    public void restaurarLava(Map<Posicion, Integer> guardados) {
        lava.restaurar(plano, guardados);
    }

    private void validarPosicion(Posicion posicion) {
        if (!plano.estaDentro(posicion)) {
            throw new IllegalArgumentException(
                    "Posicion fuera del plano: " + posicion);
        }
    }

}
