package modelo;

/**
 * algo que va a pasar en un paso futuro (lo guarda la agenda)
 */
public record EventoProgramado(int pasoDeVencimiento, String tipo, String dato) {
}