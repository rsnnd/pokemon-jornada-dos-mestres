/** Regra padrao de dano; novas regras podem implementar CalculadoraDano (OCP). */
public class DanoPorTipo implements CalculadoraDano {
    @Override
    public int calcular(Pokemon atacante, Pokemon defensor, Ataque ataque) {
        double multiplicador = ataque.getTipo().multiplicadorContra(defensor.getTipo());
        return Math.max(1, (int) Math.round(ataque.getPoder() * multiplicador));
    }
}
