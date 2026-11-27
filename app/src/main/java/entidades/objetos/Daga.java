package entidades.objetos;

import java.util.ArrayList;
import java.util.List;
import modelo.Agenda;
import modelo.Heroe;
import modelo.Plano;
import entidades.monstruos.Monstruo;

import java.util.Random;

public class Daga implements Objeto{
    public static final String ID_OBJETO = "daga";

    private final Random random;

    public Daga(Random random){
        if(random==null){
            throw new IllegalArgumentException("El generador es obligatorio");
        }
        this.random = random;
    }

    @Override
    public ResultadoUso usar(Heroe heroe, Plano plano, Agenda agenda) {
        if (heroe == null || plano == null || agenda == null) {
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }

        List<Monstruo> candidatos = new ArrayList<>();

        for (Monstruo monstruo : plano.getMonstruos()) {
            if (heroe.getUbicacion().posicion()
                    .distanciaManhattan(monstruo.getPosicion()) == 1) {
                candidatos.add(monstruo);
            }
        }

        if (!candidatos.isEmpty()) {
            int elegido = random.nextInt(candidatos.size());
            plano.quitarMonstruo(candidatos.get(elegido));
            return new ResultadoUso(true, null);
        }
        return new ResultadoUso(false, null);
    }

}
