/** Subtipo concreto de Pokemon: intercambiavel com a classe abstrata (LSP). */
public class PokemonAgua extends Pokemon {
    public PokemonAgua(String nome, int vidaMaxima, int nivel, Ataque... ataques) {
        super(nome, Tipo.AGUA, vidaMaxima, nivel, ataques);
    }
    @Override public String getEspecialidade() { return "Especialista em agua"; }
}
