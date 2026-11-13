/** Subtipo concreto de Pokemon: intercambiavel com a classe abstrata (LSP). */
public class PokemonPlanta extends Pokemon {
    public PokemonPlanta(String nome, int vidaMaxima, int nivel, Ataque... ataques) {
        super(nome, Tipo.PLANTA, vidaMaxima, nivel, ataques);
    }
    @Override public String getEspecialidade() { return "Especialista em planta"; }
}
