package modelo;

import entidades.cofres.Cofre;
import entidades.monstruos.Monstruo;
import java.util.ArrayList;
import java.util.List;

/**
 * La partida: junta el mundo, el héroe y la agenda, y aplica las reglas del juego.
 * Cada acción válida del héroe (moverse o esperar) es un paso: al empezar se ejecutan
 * los eventos vencidos de la agenda, el héroe actúa y después actúan los monstruos
 * del plano donde está.
 * Esta clase nunca imprime ni dibuja: quien la usa (la vista) solo lee su estado.
 */
public class Mazmorra {
    private final Dificultad dificultad;
    private final Mundo mundo;
    private final Heroe heroe;
    private final Agenda agenda;
    private final Costo costos;
    private final LogPartida log = new LogPartida();
    private EstadoPartida estado;

    /**
     * Crea una partida nueva, en curso.
     *
     * @throws IllegalArgumentException si alguno de los argumentos es null.
     */
    public Mazmorra(Dificultad dificultad, Mundo mundo, Heroe heroe, Agenda agenda, Costo costos) {
        this(dificultad, mundo, heroe, agenda, costos, EstadoPartida.EN_CURSO);
    }

    /**
     * Crea una partida con un estado dado (lo usa la carga de una partida guardada).
     *
     * @throws IllegalArgumentException si alguno de los argumentos es null.
     */
    public Mazmorra(Dificultad dificultad, Mundo mundo, Heroe heroe, Agenda agenda,
                    Costo costos, EstadoPartida estado) {
        if (dificultad == null || mundo == null || heroe == null || agenda == null
                || costos == null || estado == null) {
            throw new IllegalArgumentException("Ningun dato de la partida puede ser null.");
        }
        this.dificultad = dificultad;
        this.mundo = mundo;
        this.heroe = heroe;
        this.agenda = agenda;
        this.costos = costos;
        this.estado = estado;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public Mundo getMundo() {
        return mundo;
    }

    public Heroe getHeroe() {
        return heroe;
    }

    public Agenda getAgenda() {
        return agenda;
    }

    public EstadoPartida getEstado() {
        return estado;
    }

    public Costo getCosto(){
        return costos;
    }

    public LogPartida getLog() {
        return log;
    }

    /** @return true si la partida ya se ganó o se perdió. */
    public boolean estaTerminada() {
        return estado != EstadoPartida.EN_CURSO;
    }

    /** El plano donde está el héroe ahora. */
    public Plano planoActual() {
        return mundo.planoEn(heroe.getUbicacion());
    }

    /** Puntaje según la regla 9: cofres × 100 + energía × 2 - pasos. */
    public Puntaje calcularPuntaje() {
        return new Puntaje(heroe.getCofresAbiertos(), heroe.getEnergia(), agenda.getPasoActual());
    }

    public boolean debeEsperarPorInmovilizacion() {
        Integer fin = heroe.vencimientoDe(EfectoHeroe.INMOVILIZADO);
        return fin != null && (long) fin > (long) agenda.getPasoActual() + 1;
    }

    /**
     * Cuánta energía cuesta entrar a un terreno. Con el efecto de los zapatos de agua
     * activo, el agua cuesta como piso.
     *
     * @throws IllegalArgumentException si el terreno es pared (no se puede pisar).
     */
    public int costoDe(Terreno terreno) {
        return switch (terreno) {
            case PISO -> costos.piso();
            case AGUA -> heroe.tieneEfecto(EfectoHeroe.ZAPATOS_DE_AGUA) ? costos.piso() : costos.agua();
            case LAVA -> costos.lava();
            case PARED -> throw new IllegalArgumentException("La pared no se puede pisar.");
        };
    }

    /**
     * El héroe intenta moverse una celda. Si el destino es pared o está fuera del plano
     * no es una acción válida: no pasa nada y no cuenta como paso.
     * Si es válida: se ejecutan los eventos vencidos, el héroe paga el costo del terreno,
     * y se resuelve la celda (salida, cofre, conexión) y el turno de los monstruos.
     * Si pisa la salida gana, aunque el movimiento le deje la energía en 0.
     *
     * @return true si la acción fue válida y se hizo un paso.
     * @throws IllegalArgumentException si la dirección es null.
     * @throws IllegalStateException    si la partida ya terminó.
     */
    public boolean moverHeroe(Direccion direccion) {
        validarEnCurso();
        if (direccion == null) {
            throw new IllegalArgumentException("La direccion no puede ser null.");
        }
        if (debeEsperarPorInmovilizacion()) {
            return false;
        }
        Plano plano = planoActual();
        Posicion destino = heroe.getUbicacion().posicion().mover(direccion);
        if (!plano.esTransitable(destino)) {
            return false;
        }

        comenzarPaso();
        heroe.perderEnergia(costoDe(plano.terrenoEn(destino)));
        heroe.irA(new Ubicacion(heroe.getUbicacion().tablero(), heroe.getUbicacion().piso(), destino));
        plano.marcarVisitada(destino);
        log.registrar(agenda.getPasoActual(),
                "Heroe se mueve " + direccion + " a " + destino + " (energia: " + heroe.getEnergia() + ")");

        if (heroe.getUbicacion().equals(mundo.ubicacionDeSalida())) {
            estado = EstadoPartida.GANADA;
            log.registrar(agenda.getPasoActual(), "Heroe llego a la salida. Partida ganada.");
            return true;
        }
        if (verificarDerrota()) {
            return true;
        }

        abrirCofreSiHay(plano, destino);
        usarConexionSiHay();
        if (verificarDerrota()) {
            return true;
        }

        moverMonstruos();
        verificarDerrota();
        return true;
    }

    /**
     * El héroe espera: no gasta energía, pero pasa un paso (corren los eventos y actúan
     * los monstruos).
     *
     * @throws IllegalStateException si la partida ya terminó.
     */
    public void esperar() {
        validarEnCurso();
        comenzarPaso();
        log.registrar(agenda.getPasoActual(), "Heroe espera (energia: " + heroe.getEnergia() + ")");
        moverMonstruos();
        verificarDerrota();
    }

    // ---------- Piezas de un paso ----------

    /** Empieza un paso: avanza la agenda y ejecuta los eventos que ya vencieron, en orden. */
    private void comenzarPaso() {
        agenda.avanzarPaso();
        while (agenda.hayEventoVencido()) {
            EventoProgramado programado = agenda.sacarProximo();
            programado.evento().ejecutar(this);
            log.registrar(agenda.getPasoActual(),
                    "Evento ejecutado: " + programado.evento().getClass().getSimpleName());
        }
    }


    /** Si hay un cofre en la celda, lo abre y lo saca del plano (regla 4). */
    private void abrirCofreSiHay(Plano plano, Posicion posicion) {
        Cofre cofre = plano.cofreEn(posicion);
        if (cofre != null) {
            cofre.abrir(heroe, plano, agenda);
            log.registrar(agenda.getPasoActual(),
                    "Cofre abierto: " + cofre.getTipo() + " en " + posicion);
        }
    }

    /**
     * Si la celda es el extremo de una conexión, lleva al héroe al otro extremo (regla 5).
     * Al llegar no se vuelve a usar la conexión: solo se usa al pisarla caminando.
     */
    private void usarConexionSiHay() {
        Conexion conexion = mundo.conexionEn(heroe.getUbicacion());
        if (conexion != null) {
            Ubicacion otroExtremo = conexion.extremoOpuesto(heroe.getUbicacion());
            heroe.irA(otroExtremo);
            mundo.planoEn(otroExtremo).marcarVisitada(otroExtremo.posicion());
            log.registrar(agenda.getPasoActual(),
                    "Conexion usada hacia tablero " + otroExtremo.tablero()
                            + ", piso " + otroExtremo.piso());
        }
    }

    /**
     * Hacen su turno los monstruos del plano donde está el héroe (nunca cambian de plano
     * y los de otros planos no actúan). Se recorre una copia de la lista por si un
     * monstruo se saca del plano mientras actúa.
     */
    private void moverMonstruos() {
        Plano plano = planoActual();
        List<Monstruo> copia = new ArrayList<>(plano.getMonstruos());
        for (Monstruo monstruo : copia) {
            monstruo.actuar(plano, heroe, agenda);
        }
    }

    /** @return true (y deja la partida perdida) si al héroe se le acabó la energía. */
    private boolean verificarDerrota() {
        if (heroe.estaSinEnergia()) {
            estado = EstadoPartida.PERDIDA;
            log.registrar(agenda.getPasoActual(), "Heroe sin energia. Partida perdida.");
            return true;
        }
        return false;
    }

    private void validarEnCurso() {
        if (estaTerminada()) {
            throw new IllegalStateException("La partida ya termino.");
        }
    }
}
