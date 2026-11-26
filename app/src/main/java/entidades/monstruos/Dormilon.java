package entidades.monstruos;

import modelo.*;
import modelo.eventos.DespertarDormilones;

public class Dormilon extends Cazador{
    private EstadoDormilon estado;
    private Integer pasoDespertar;

    public Dormilon(Posicion posicionInicial, Costo costos){
        super(posicionInicial, costos);
        estado = EstadoDormilon.DORMIDO;
    }

    @Override
    protected void antesDeActuar(Plano plano, Heroe heroe, Agenda agenda) {
        if (estado == EstadoDormilon.DORMIDO
                && getPosicion().distanciaManhattan(
                heroe.getUbicacion().posicion()) <= 3) {

            pasoDespertar = agenda.getPasoActual() + 1;
            estado = EstadoDormilon.PENDIENTE_DESPERTAR;

            Ubicacion ubicacion = heroe.getUbicacion();
            agenda.programar(1,
                    new DespertarDormilones(ubicacion.tablero(), ubicacion.piso()));
        }
    }

    @Override
    protected boolean puedeActuar(Agenda agenda) {
        return estaDespierto();
    }

    public boolean estaDespierto(){
        return estado == EstadoDormilon.DESPIERTO;
    }

    @Override
    public void despertarSiCorresponde(int pasoActual){
        if (estado == EstadoDormilon.PENDIENTE_DESPERTAR
                && pasoActual >= pasoDespertar) {
            estado = EstadoDormilon.DESPIERTO;
            pasoDespertar = null;
        }
    }

    @Override
    public String exportarEstado() {
        if (estado == EstadoDormilon.PENDIENTE_DESPERTAR) {
            return estado.name() + ":" + pasoDespertar;
        }
        return estado.name();
    }

    @Override
    public void importarEstado(String guardado) {
        if ("DORMIDO".equals(guardado)) {
            estado = EstadoDormilon.DORMIDO;
            pasoDespertar = null;
        } else if ("DESPIERTO".equals(guardado)) {
            estado = EstadoDormilon.DESPIERTO;
            pasoDespertar = null;
        } else if (guardado != null
                && guardado.startsWith("PENDIENTE_DESPERTAR:")) {
            int paso = Integer.parseInt(
                    guardado.substring("PENDIENTE_DESPERTAR:".length()));
            if (paso < 0) {
                throw new IllegalArgumentException("Paso de despertar invalido");
            }
            estado = EstadoDormilon.PENDIENTE_DESPERTAR;
            pasoDespertar = paso;
        } else {
            throw new IllegalArgumentException("Estado de dormilon invalido");
        }
    }
}
