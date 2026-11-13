/** Estrategia de captura proporcional ao HP do alvo (OCP). */
public class CapturaPorVida implements RegraCaptura {
    @Override
    public double chance(Pokemon alvo) {
        return 0.25 + 0.65 * (1.0 - (double) alvo.getVidaAtual() / alvo.getVidaMaxima());
    }
}
