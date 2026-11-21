package entidades.objetos;

import estructuras.Lista;
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
    public boolean usar(Heroe heroe, Plano plano, Agenda agenda) {
        if (heroe == null || plano == null || agenda == null) {
            throw new IllegalArgumentException("Heroe, plano y agenda son obligatorios");
        }

        Lista<Monstruo> candidatos = new Lista<>();
        Lista<Monstruo> monstruos = plano.getMonstruos();

        for (int i = 0; i < monstruos.cantidad(); i++) {
            Monstruo monstruo = monstruos.obtener(i);
            if (heroe.getUbicacion().posicion()
                    .distanciaManhattan(monstruo.getPosicion()) == 1) {
                candidatos.agregar(monstruo);
            }
        }

        if (!candidatos.estaVacia()) {
            int elegido = random.nextInt(candidatos.cantidad());
            plano.quitarMonstruo(candidatos.obtener(elegido));
            return true;
        }
        return false;
    }

}
