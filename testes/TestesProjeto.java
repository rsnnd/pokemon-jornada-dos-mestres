import java.io.*;
import java.util.*;

/** Testes automatizados sem dependencias externas. Execute com java -ea. */
public class TestesProjeto {
    private static void validar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
    }
    public static void main(String[] args) throws Exception {
        Pokemon fogo = FabricaPokemon.criar(1);
        Pokemon agua = FabricaPokemon.criar(2);
        Pokemon planta = FabricaPokemon.criar(3);
        Pokemon normal = FabricaPokemon.criar(4);
        validar(fogo instanceof PokemonFogo && agua instanceof PokemonAgua && planta instanceof PokemonPlanta && normal instanceof PokemonNormal, "Subclasses");
        for (Pokemon p : new Pokemon[]{fogo, agua, planta, normal}) {
            validar(!p.getEspecialidade().isBlank(), "Polimorfismo/LSP");
            validar(p.getVidaAtual() == p.getVidaMaxima(), "HP inicial");
            validar(p.curar(10) == 0, "Limite de cura");
        }
        CalculadoraDano dano = new DanoPorTipo();
        validar(dano.calcular(fogo, planta, new Ataque("Brasa", Tipo.FOGO, 11)) == 22, "Dano super efetivo");
        validar(dano.calcular(planta, fogo, new Ataque("Folha", Tipo.PLANTA, 11)) == 6, "Dano pouco efetivo");
        validar(planta.receberDano(1000) == 50 && planta.estaDerrotado(), "Limite de dano");
        RegraCaptura captura = new CapturaPorVida();
        validar(captura.chance(fogo) == 0.25, "Chance com HP cheio");
        fogo.receberDano(40);
        validar(captura.chance(fogo) > 0.25, "Captura melhora com HP baixo");
        RecompensaExperiencia xp = new RecompensaExperiencia();
        validar(xp.captura(fogo) == xp.vitoria(fogo) / 2, "XP de captura");
        // Implementacoes alternativas das interfaces, sem modificar Batalha: DIP/OCP.
        CalculadoraDano danoTeste = (atacante, defensor, ataque) -> 3;
        RegraCaptura capturaTeste = alvo -> 1.0;
        Scanner scanner = new Scanner(new ByteArrayInputStream("1\n3\n".getBytes()));
        Batalha batalha = new Batalha(new Leitor(scanner), new Random(1), danoTeste, capturaTeste, xp);
        Treinador treinador = new Treinador("Teste", new PokemonFogo("Charmander",45,5,new Ataque("Brasa",Tipo.FOGO,11)));
        Pokemon adversario = FabricaPokemon.criar(4);
        ResultadoBatalha r = batalha.iniciar(treinador, adversario);
        validar(r == ResultadoBatalha.CAPTURA, "Captura com regra injetada");
        validar(treinador.getEquipe().size() == 2, "Equipe apos captura");
        validar(treinador.getEquipe().get(0).getExperiencia() == xp.captura(adversario) || treinador.getEquipe().get(0).getNivel() > 5, "XP na captura");
        // Repositorio concreto garante round-trip via formato legado da V6.
        RepositorioPartida repositorio = new RepositorioProperties();
        Jornada jornada = new Jornada(batalha, new Leitor(new Scanner("")));
        repositorio.salvar(treinador, jornada);
        validar(repositorio.existe(), "Save criado");
        DadosPartida dados = repositorio.carregar(batalha, new Leitor(new Scanner("")));
        validar(dados.getTreinador().getEquipe().size() == 2, "Save restaurou a equipe");
        validar(dados.getTreinador().getEquipe().get(0) instanceof PokemonFogo, "Save restaurou subtipo");
        validar(dados.getTreinador().getEquipe().get(1) instanceof PokemonNormal, "Save restaurou captura");
        System.out.println("TODOS OS TESTES PASSARAM");
    }
}
