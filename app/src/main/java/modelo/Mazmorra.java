package modelo;

import entidades.cofres.Cofre;
import entidades.monstruos.Congelador;
import entidades.monstruos.Dormilon;
import entidades.monstruos.Monstruo;
import entidades.objetos.Escudo;
import entidades.objetos.ZapatosDeAgua;
import estructuras.Lista;

/**
 * La partida: junta el mundo, el héroe y la agenda, y aplica las reglas del juego.
 * Cada acción válida del héroe (moverse o esperar) es un paso: al empezar se ejecutan
 * los eventos vencidos de la agenda, el héroe actúa y después actúan los monstruos
 * del plano donde está.
 * Esta clase nunca imprime ni dibuja: quien la usa (la vista) solo lee su estado.
 */
public class Mazmorra {
    /** Tipo de evento de la agenda que apaga un efecto del héroe; el dato es el id del efecto. */
    /** Id del efecto de los zapatos de agua: mientras está activo, el agua cuesta como piso. */
    public static final String EFECTO_ZAPATOS_AGUA = ZapatosDeAgua.ID_EFECTO;

    private final Dificultad dificultad;
    private final Mundo mundo;
    private final Heroe heroe;
    private final Agenda agenda;
    private final Costo costos;
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
        Integer fin = heroe.vencimientoDe(Congelador.INMOVILIZADO);
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
            case AGUA -> heroe.tieneEfecto(EFECTO_ZAPATOS_AGUA) ? costos.piso() : costos.agua();
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

        if (heroe.getUbicacion().equals(mundo.ubicacionDeSalida())) {
            estado = EstadoPartida.GANADA;
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
        moverMonstruos();
        verificarDerrota();
    }

    // ---------- Piezas de un paso ----------

    /** Empieza un paso: avanza la agenda y ejecuta los eventos que ya vencieron, en orden. */
    private void comenzarPaso() {
        agenda.avanzarPaso();
        while (agenda.hayEventoVencido()) {
            ejecutarEvento(agenda.sacarProximo());
        }
    }


    private void ejecutarEvento(EventoProgramado evento) {
        int pasoActual = agenda.getPasoActual();
        switch (evento.tipo()) {
            case FIN_INMOVILIZACION ->
                    heroe.finalizarEfectoSiVencio(Congelador.INMOVILIZADO, pasoActual);
            case FIN_ZAPATOS_DE_AGUA ->
                    heroe.finalizarEfectoSiVencio(ZapatosDeAgua.ID_EFECTO, pasoActual);
            case FIN_ESCUDO ->
                    heroe.finalizarEfectoSiVencio(Escudo.ID_EFECTO, pasoActual);
            case FIN_SUENO -> planoDeEvento(evento).despertarMonstruosSiVencio(pasoActual);
            case FIN_PUENTE -> planoDeEvento(evento).finalizarPuenteSiVencio(pasoActual);
            case FIN_LAVA -> planoDeEvento(evento).finalizarLavaSiVencio(pasoActual);
            case DESPERTAR_DORMILON -> despertarDormilones(evento, pasoActual);
        }
    }

    private Plano planoDeEvento(EventoProgramado evento) {
        String dato = evento.dato();
        if (dato == null) {
            throw new IllegalArgumentException("El evento debe identificar su plano.");
        }
        String[] identificadores = dato.split(",", -1);
        if (identificadores.length != 2) {
            throw new IllegalArgumentException("La ubicacion del evento no es valida.");
        }
        try {
            return mundo.planoEn(
                    Integer.parseInt(identificadores[0]),
                    Integer.parseInt(identificadores[1]));
        } catch (NumberFormatException excepcion) {
            throw new IllegalArgumentException("La ubicacion del evento no es valida.", excepcion);
        }
    }

    private void despertarDormilones(EventoProgramado evento, int pasoActual) {
        Plano plano = planoDeEvento(evento);
        for (int i = 0; i < plano.getMonstruos().cantidad(); i++) {
            Monstruo monstruo = plano.getMonstruos().obtener(i);
            if (monstruo instanceof Dormilon dormilon) {
                dormilon.despertarSiCorresponde(pasoActual);
            }
        }
    }

    /** Si hay un cofre en la celda, lo abre y lo saca del plano (regla 4). */
    private void abrirCofreSiHay(Plano plano, Posicion posicion) {
        Cofre cofre = plano.cofreEn(posicion);
        if (cofre != null) {
            cofre.abrir(heroe, plano, agenda);
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
        }
    }

    /**
     * Hacen su turno los monstruos del plano donde está el héroe (nunca cambian de plano
     * y los de otros planos no actúan). Se recorre una copia de la lista por si un
     * monstruo se saca del plano mientras actúa.
     */
    private void moverMonstruos() {
        Plano plano = planoActual();
        Lista<Monstruo> copia = new Lista<>();
        for (int i = 0; i < plano.getMonstruos().cantidad(); i++) {
            copia.agregar(plano.getMonstruos().obtener(i));
        }
        for (int i = 0; i < copia.cantidad(); i++) {
            copia.obtener(i).actuar(plano, heroe, agenda);
        }
    }

    /** @return true (y deja la partida perdida) si al héroe se le acabó la energía. */
    private boolean verificarDerrota() {
        if (heroe.estaSinEnergia()) {
            estado = EstadoPartida.PERDIDA;
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
