package modelo;

public record ParametrosEntidades(
        int danioBasico,
        int danioTrampaEnergia,
        int duracionCongelador,
        int duracionPuente,
        int duracionLava,
        int duracionZapatosAgua,
        int duracionEscudo,
        int duracionSueno,
        int energiaPocion
) {
    public ParametrosEntidades {
        positivo("danioBasico", danioBasico);
        positivo("danioTrampaEnergia", danioTrampaEnergia);
        positivo("duracionCongelador", duracionCongelador);
        positivo("duracionPuente", duracionPuente);
        positivo("duracionLava", duracionLava);
        positivo("duracionZapatosAgua", duracionZapatosAgua);
        positivo("duracionEscudo", duracionEscudo);
        positivo("duracionSueno", duracionSueno);
        positivo("energiaPocion", energiaPocion);
    }

    private static void positivo(String nombre, int valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException(
                    nombre + " debe ser positivo");
        }
    }
}
