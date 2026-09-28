package generador;

/**
 * Genera un plano de la mazmorra a partir de los parametros que recibe.
 * Responde el formato JSON exacto de los manuales de CB100.
 */
public interface GeneradorDeLaberinto {
    String generar(String parametrosJson);
}
