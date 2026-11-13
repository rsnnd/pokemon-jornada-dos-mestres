import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/** Salva e restaura uma partida em arquivo local (sem bibliotecas externas). */
public class Persistencia {
    public static final Path ARQUIVO = Path.of("save", "partida.properties");
    private static final String VERSAO = "1";

    private Persistencia() { }

    public static boolean existeSave() { return Files.isRegularFile(ARQUIVO); }

    public static void salvar(Treinador treinador, Jornada jornada) throws IOException {
        Properties dados = new Properties();
        dados.setProperty("versao", VERSAO);
        dados.setProperty("treinador.nome", treinador.getNome());
        dados.setProperty("inventario.pokebolas", String.valueOf(treinador.getInventario().getPokebolas()));
        dados.setProperty("inventario.pocoes", String.valueOf(treinador.getInventario().getPocoes()));
        dados.setProperty("equipe.total", String.valueOf(treinador.getEquipe().size()));
        for (int i = 0; i < treinador.getEquipe().size(); i++) {
            Pokemon p = treinador.getEquipe().get(i);
            String prefixo = "equipe." + i + ".";
            dados.setProperty(prefixo + "nome", p.getNome());
            dados.setProperty(prefixo + "tipo", p.getTipo().name());
            dados.setProperty(prefixo + "hpMax", String.valueOf(p.getVidaMaxima()));
            dados.setProperty(prefixo + "hp", String.valueOf(p.getVidaAtual()));
            dados.setProperty(prefixo + "nivel", String.valueOf(p.getNivel()));
            dados.setProperty(prefixo + "xp", String.valueOf(p.getExperiencia()));
            Ataque[] ataques = p.getAtaques();
            dados.setProperty(prefixo + "ataques.total", String.valueOf(ataques.length));
            for (int a = 0; a < ataques.length; a++) {
                String chave = prefixo + "ataques." + a + ".";
                dados.setProperty(chave + "nome", ataques[a].getNome());
                dados.setProperty(chave + "tipo", ataques[a].getTipo().name());
                dados.setProperty(chave + "poder", String.valueOf(ataques[a].getPoder()));
            }
        }
        boolean[] treinadores = jornada.getTreinadoresVencidos();
        boolean[] insignias = jornada.getInsignias();
        for (int i = 0; i < treinadores.length; i++)
            dados.setProperty("treinadorVencido." + i, String.valueOf(treinadores[i]));
        for (int i = 0; i < insignias.length; i++)
            dados.setProperty("insignia." + i, String.valueOf(insignias[i]));
        dados.setProperty("campeao", String.valueOf(jornada.isCampeao()));

        Files.createDirectories(ARQUIVO.getParent());
        Path temporario = Files.createTempFile(ARQUIVO.getParent(), "partida-", ".tmp");
        try {
            try (OutputStream saida = Files.newOutputStream(temporario)) {
                dados.store(saida, "Pokemon Jornada dos Mestres - Versao Final");
            }
            try {
                Files.move(temporario, ARQUIVO, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
                Files.move(temporario, ARQUIVO, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporario);
        }
    }

    public static DadosPartida carregar(Combate batalha, Leitor leitor) throws IOException {
        Properties dados = new Properties();
        try (InputStream entrada = Files.newInputStream(ARQUIVO)) {
            dados.load(entrada);
        }
        try {
            if (!VERSAO.equals(obrigatorio(dados, "versao")))
                throw new IllegalArgumentException("Versao de salvamento nao reconhecida.");
            String nome = obrigatorio(dados, "treinador.nome");
            int bolas = inteiro(dados, "inventario.pokebolas", 0, 999999);
            int pocoes = inteiro(dados, "inventario.pocoes", 0, 999999);
            int total = inteiro(dados, "equipe.total", 1, 6);
            Pokemon[] equipe = new Pokemon[total];
            for (int i = 0; i < total; i++) {
                String prefixo = "equipe." + i + ".";
                String especie = obrigatorio(dados, prefixo + "nome");
                Tipo tipo = Tipo.valueOf(obrigatorio(dados, prefixo + "tipo"));
                int hpMax = inteiro(dados, prefixo + "hpMax", 1, 10000);
                int hp = inteiro(dados, prefixo + "hp", 0, hpMax);
                int nivel = inteiro(dados, prefixo + "nivel", 1, 100);
                int xp = inteiro(dados, prefixo + "xp", 0, 25 + nivel * 5 - 1);
                int totalAtaques = inteiro(dados, prefixo + "ataques.total", 1, 4);
                Ataque[] ataques = new Ataque[totalAtaques];
                for (int a = 0; a < totalAtaques; a++) {
                    String chave = prefixo + "ataques." + a + ".";
                    ataques[a] = new Ataque(obrigatorio(dados, chave + "nome"),
                            Tipo.valueOf(obrigatorio(dados, chave + "tipo")),
                            inteiro(dados, chave + "poder", 1, 100));
                }
                equipe[i] = Pokemon.restaurar(especie, tipo, hpMax, hp, nivel, xp, ataques);
            }
            Treinador treinador = new Treinador(nome, new Inventario(bolas, pocoes), equipe[0]);
            for (int i = 1; i < total; i++) {
                // Pode existir Pokemon desmaiado na equipe salva.
                treinador.restaurarPokemon(equipe[i]);
            }
            boolean[] vencidos = new boolean[2];
            boolean[] insignias = new boolean[4];
            for (int i = 0; i < 2; i++) vencidos[i] = booleano(dados, "treinadorVencido." + i);
            for (int i = 0; i < 4; i++) insignias[i] = booleano(dados, "insignia." + i);
            Jornada jornada = new Jornada(batalha, leitor);
            jornada.restaurarProgresso(vencidos, insignias, booleano(dados, "campeao"));
            return new DadosPartida(treinador, jornada);
        } catch (IllegalArgumentException ex) {
            throw new IOException("Arquivo de salvamento invalido: " + ex.getMessage(), ex);
        }
    }

    private static String obrigatorio(Properties dados, String chave) {
        String valor = dados.getProperty(chave);
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("Campo ausente: " + chave);
        return valor;
    }

    private static int inteiro(Properties dados, String chave, int min, int max) {
        int valor = Integer.parseInt(obrigatorio(dados, chave));
        if (valor < min || valor > max) throw new IllegalArgumentException("Valor invalido: " + chave);
        return valor;
    }

    private static boolean booleano(Properties dados, String chave) {
        String valor = obrigatorio(dados, chave);
        if (!valor.equals("true") && !valor.equals("false"))
            throw new IllegalArgumentException("Valor booleano invalido: " + chave);
        return Boolean.parseBoolean(valor);
    }
}
