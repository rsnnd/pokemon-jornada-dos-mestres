/** Regra isolada de experiencia (SRP). Capturas recebem metade, arredondada para baixo. */
public class RecompensaExperiencia {
    public int vitoria(Pokemon adversario) {
        return 50 + adversario.getNivel() * 12;
    }
    public int captura(Pokemon adversario) {
        return vitoria(adversario) / 2;
    }
}
