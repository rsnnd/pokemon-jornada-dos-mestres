/** Contrato de batalhas usado por Jornada (DIP). */
public interface Combate {
    ResultadoBatalha iniciar(Treinador treinador, Pokemon adversario);
    ResultadoBatalha iniciar(Treinador treinador, Pokemon adversario, String rival);
    ResultadoBatalha iniciar(Treinador treinador, Pokemon adversario, String rival, boolean podeCancelarInicio);
}
