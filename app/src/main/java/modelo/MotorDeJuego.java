package modelo;

import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;
import entidades.objetos.Objeto;
import entidades.objetos.ResultadoUso;
import java.util.ArrayList;
import java.util.List;

/**
 * El motor del juego: aplica las reglas a una partida. No guarda nada, el estado
 * vive en la Mazmorra que recibe. Cada acción válida del héroe (moverse, usar un objeto
 * o esperar) es un paso: al empezar se ejecutan los eventos vencidos de la agenda, el héroe
 * actúa y después actúan los monstruos del plano donde está
 * Esta clase nunca imprime ni dibuja
 */
public class MotorDeJuego {

    /**
     * El héroe intenta moverse una celda. Si el destino es pared o está fuera del plano
     * no es una acción válida: no pasa nada y no cuenta como paso
     * Si es válida: se ejecutan los eventos vencidos, el héroe paga el costo del terreno,
     * y se resuelve la celda (salida, cofre, conexión) y el turno de los monstruos.
     * Si pisa la salida gana, aunque el movimiento le deje la energía en 0
     *
     * @return true si la acción fue válida y se hizo un paso
     * @throws IllegalArgumentException si la dirección es null
     * @throws IllegalStateException    si la partida ya terminó
     */
    public boolean moverHeroe(Mazmorra partida, Direccion direccion) {
        validarEnCurso(partida);
        if (direccion == null) {
            throw new IllegalArgumentException("La direccion no puede ser null");
        }
        if (partida.debeEsperarPorInmovilizacion()) {
            return false;
        }
        Heroe heroe = partida.getHeroe();
        Plano plano = partida.planoActual();
        Posicion destino = heroe.getUbicacion().posicion().mover(direccion);
        if (!plano.esTransitable(destino)) {
            return false;
        }

        comenzarPaso(partida);
        heroe.perderEnergia(partida.costoDe(plano.terrenoEn(destino)));
        heroe.irA(new Ubicacion(heroe.getUbicacion().tablero(), heroe.getUbicacion().piso(), destino));
        plano.marcarVisitada(destino);
        partida.getLog().registrar(partida.getAgenda().getPasoActual(),
                "Heroe se mueve " + direccion + " a " + destino + " (energia: " + heroe.getEnergia() + ")");

        if (heroe.getUbicacion().equals(partida.getMundo().ubicacionDeSalida())) {
            partida.terminar(EstadoPartida.GANADA);
            partida.getLog().registrar(partida.getAgenda().getPasoActual(),
                    "Heroe llego a la salida, partida ganada");
            return true;
        }
        if (verificarDerrota(partida)) {
            return true;
        }

        abrirCofreSiHay(partida, plano, destino);
        usarConexionSiHay(partida);
        if (verificarDerrota(partida)) {
            return true;
        }

        moverMonstruos(partida);
        verificarDerrota(partida);
        return true;
    }

    /**
     * El héroe espera: no gasta energía, pero pasa un paso (corren los eventos y actúan
     * los monstruos)
     *
     * @throws IllegalStateException si la partida ya terminó
     */
    public void esperar(Mazmorra partida) {
        validarEnCurso(partida);
        comenzarPaso(partida);
        partida.getLog().registrar(partida.getAgenda().getPasoActual(),
                "Heroe espera (energia: " + partida.getHeroe().getEnergia() + ")");
        moverMonstruos(partida);
        verificarDerrota(partida);
    }

    /**
     * El héroe usa un objeto de su inventario (regla 2). Si el objeto se pudo usar, el
     * objeto se saca del inventario y la acción cuenta como un paso: se ejecutan los
     * eventos vencidos y actúan los monstruos. La duración de los efectos cuenta el
     * paso en que se usa el objeto
     * Si el héroe no tiene el objeto, o el objeto no se pudo usar (por ejemplo, la daga
     * sin un monstruo al lado), no pasa nada y no cuenta como paso
     * El mensaje del objeto, si tiene, queda en el log de la partida
     *
     * @return true si el objeto se usó y se hizo un paso
     * @throws IllegalArgumentException si el id es null o vacío, o si el héroe tiene un
     * objeto que no está en el catálogo
     * @throws IllegalStateException    si la partida ya terminó
     */
    public boolean usarObjeto(Mazmorra partida, String idObjeto) {
        validarEnCurso(partida);
        if (idObjeto == null || idObjeto.isBlank()) {
            throw new IllegalArgumentException("El id del objeto no puede ser null ni vacio");
        }
        Heroe heroe = partida.getHeroe();
        if (!heroe.tieneObjeto(idObjeto)) {
            return false;
        }

        Objeto objeto = partida.getObjetos().obtener(idObjeto);
        ResultadoUso resultado = objeto.usar(heroe, partida.planoActual(), partida.getAgenda());
        if (!resultado.seConsume()) {
            return false;
        }

        heroe.quitarObjeto(idObjeto);
        comenzarPaso(partida);
        String detalle = resultado.mensaje() == null ? "" : ": " + resultado.mensaje();
        partida.getLog().registrar(partida.getAgenda().getPasoActual(), "Heroe usa " + idObjeto + detalle);
        moverMonstruos(partida);
        verificarDerrota(partida);
        return true;
    }

    // PIEZAS DE UN PASO --------------------------------

    /** Empieza un paso: avanza la agenda y ejecuta los eventos que ya vencieron, en orden */
    private void comenzarPaso(Mazmorra partida) {
        Agenda agenda = partida.getAgenda();
        agenda.avanzarPaso();
        while (agenda.hayEventoVencido()) {
            EventoProgramado programado = agenda.sacarProximo();
            programado.evento().ejecutar(partida);
            partida.getLog().registrar(agenda.getPasoActual(),
                    "Evento ejecutado: " + programado.evento().getClass().getSimpleName());
        }
    }

    /** Si hay un cofre en la celda, lo abre y lo saca del plano (regla 4) */
    private void abrirCofreSiHay(Mazmorra partida, Plano plano, Posicion posicion) {
        Cofre cofre = plano.cofreEn(posicion);
        if (cofre != null) {
            cofre.abrir(partida.getHeroe(), plano, partida.getAgenda());
            partida.getLog().registrar(partida.getAgenda().getPasoActual(),
                    "Cofre abierto: " + cofre.getTipo() + " en " + posicion);
        }
    }

    /**
     * Si la celda es el extremo de una conexión, lleva al héroe al otro extremo (regla 5)
     * Al llegar no se vuelve a usar la conexión: solo se usa al pisarla caminando
     */
    private void usarConexionSiHay(Mazmorra partida) {
        Heroe heroe = partida.getHeroe();
        Conexion conexion = partida.getMundo().conexionEn(heroe.getUbicacion());
        if (conexion != null) {
            Ubicacion otroExtremo = conexion.extremoOpuesto(heroe.getUbicacion());
            heroe.irA(otroExtremo);
            partida.getMundo().planoEn(otroExtremo).marcarVisitada(otroExtremo.posicion());
            partida.getLog().registrar(partida.getAgenda().getPasoActual(),
                    "Conexion usada hacia tablero " + otroExtremo.tablero()
                            + ", piso " + otroExtremo.piso());
        }
    }

    /**
     * Hacen su turno los monstruos del plano donde está el héroe (nunca cambian de plano
     * y los de otros planos no actúan). Se recorre una copia de la lista por si un
     * monstruo se saca del plano mientras actúa
     */
    private void moverMonstruos(Mazmorra partida) {
        Plano plano = partida.planoActual();
        List<Monstruo> copia = new ArrayList<>(plano.getMonstruos());
        for (Monstruo monstruo : copia) {
            monstruo.actuar(plano, partida.getHeroe(), partida.getAgenda());
        }
    }

    /** @return true (y deja la partida perdida) si al héroe se le acabó la energía */
    private boolean verificarDerrota(Mazmorra partida) {
        if (partida.getHeroe().estaSinEnergia()) {
            partida.terminar(EstadoPartida.PERDIDA);
            partida.getLog().registrar(partida.getAgenda().getPasoActual(),
                    "Heroe sin energia, partida perdida");
            return true;
        }
        return false;
    }

    private void validarEnCurso(Mazmorra partida) {
        if (partida.estaTerminada()) {
            throw new IllegalStateException("La partida ya termino");
        }
    }
}
