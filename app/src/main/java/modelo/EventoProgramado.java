package modelo;

import modelo.eventos.Evento;

/**
 * algo que va a pasar en un paso futuro (lo guarda la agenda)
 */
public record EventoProgramado(int pasoDeVencimiento, Evento evento) {
}