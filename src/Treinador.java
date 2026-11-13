import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Treinador {
    private final String nome;
    private final Inventario inventario;
    private final List<Pokemon> equipe = new ArrayList<>();

    public Treinador(String nome, Pokemon inicial) {
        this(nome, new Inventario(5, 3), inicial);
    }

    public Treinador(String nome, Inventario inventario, Pokemon inicial) {
        if (nome == null || nome.isBlank() || inventario == null || inicial == null) {
            throw new IllegalArgumentException("Treinador invalido.");
        }
        this.nome = nome;
        this.inventario = inventario;
        equipe.add(inicial);
    }

    public String getNome() { return nome; }
    public Inventario getInventario() { return inventario; }
    public List<Pokemon> getEquipe() { return Collections.unmodifiableList(equipe); }
    public boolean equipeCheia() { return equipe.size() >= 6; }

    public boolean adicionarPokemon(Pokemon pokemon) {
        if (equipeCheia() || pokemon.estaDerrotado()) return false;
        equipe.add(pokemon);
        return true;
    }

    // Usado apenas para reconstruir a equipe salva; aceita Pokemon desmaiado.
    void restaurarPokemon(Pokemon pokemon) {
        if (pokemon == null || equipeCheia()) throw new IllegalArgumentException("Equipe salva invalida.");
        equipe.add(pokemon);
    }

    public void curarEquipe() {
        for (Pokemon p : equipe) p.restaurarTotalmente();
    }

    public boolean temPokemonDisponivel() {
        for (Pokemon p : equipe) if (!p.estaDerrotado()) return true;
        return false;
    }

    public void mostrarEquipe() {
        System.out.println("Equipe de " + nome + " (" + equipe.size() + "/6):");
        for (int i = 0; i < equipe.size(); i++) {
            Pokemon p = equipe.get(i);
            System.out.printf("%d - %s (Nv.%d, %s) %d/%d HP | XP %d/%d%s%n", i + 1, p.getNome(), p.getNivel(), p.getTipo(),
                    p.getVidaAtual(), p.getVidaMaxima(), p.getExperiencia(), p.getExperienciaParaProximoNivel(), p.estaDerrotado() ? " [DESMAIADO]" : "");
            System.out.println("    " + p.getEspecialidade()); // despacho polimorfico
        }
    }
}
