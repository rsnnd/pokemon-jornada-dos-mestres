/** Contrato pequeno: calcula dano sem alterar HP (ISP, DIP). */
public interface CalculadoraDano {
    int calcular(Pokemon atacante, Pokemon defensor, Ataque ataque);
}
