package br.com.agdeo.screenmatch.principal;

import br.com.agdeo.screenmatch.model.*;
import br.com.agdeo.screenmatch.repository.SerieRepository;
import br.com.agdeo.screenmatch.service.ConsumoAPI;
import br.com.agdeo.screenmatch.service.ConverteDados;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.datetime.DateFormatter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=30dabf58";
    private SerieRepository repositorio;

    private Scanner leitura = new Scanner(System.in);
    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    private List<DadosSerie> dadosSeries = new ArrayList<>();

    private List<Serie> series = new ArrayList<>();
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repositorio) {
        this.repositorio = repositorio;
    }

    public void exibirMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    1  - Buscar séries
                    2  - Buscar episódios
                    3  - Listar séries buscadas
                    4  - Buscar série por titulo
                    5  - Buscar série por Ator(a)
                    6  - Buscar Top 5 Series
                    7  - Buscar série por Categoria
                    8  - Filtrar série por Numero Temp. e Avaliação
                    9  - Buscar Episodio por Titulo
                    10 - Buscar Top 5 Episodios por Serie
                    11 - Buscar episódios por serie e ano de lançamento
                                    
                    0 - Sair                                 
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriePorTitulo();
                    break;
                case 5:
                    buscarSeriePorAtor();
                    break;
                case 6:
                    buscarTop5Series();
                    break;
                case 7:
                    buscarSeriePorCategoria();
                    break;
                case 8:
                    buscarSeriePorNumTempAvaliacao();
                    break;
                case 9:
                    buscarEpisodioPorTitulo();
                    break;
                case 10:
                    top5EpisodiosPorSerie();
                    break;
                case 11:
                    buscarEpisodioPorSerieEAno();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }


    private void buscarSerieWeb() {
        DadosSerie dados = getDadosSerie();
        Serie serie = new Serie(dados);
        repositorio.save(serie);
        System.out.println(serie);
    }

    private DadosSerie getDadosSerie() {
        System.out.println("Digite o nome da série para busca");
        var nomeSerie = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        return dados;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escolha uma serie pelo nome:");
        var serieBuscada = leitura.nextLine();

        Optional<Serie> serie = repositorio.findByTituloContainingIgnoreCase(serieBuscada);

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumo.obterDados(ENDERECO + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(t -> t.episodios().stream()
                            .map(e -> new Episodio(t.numeroTemporada(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repositorio.save(serieEncontrada);
        } else {
            System.out.println("Serie nao encontrada");
        }
    }

    private void listarSeriesBuscadas() {
        series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriePorTitulo() {
        System.out.println("Escolha uma serie pelo nome:");
        var serieNome = leitura.nextLine();

        serieBuscada = repositorio.findByTituloContainingIgnoreCase(serieNome);

        if (serieBuscada.isPresent()) {
            System.out.println("Dados da Serie buscada: \n" + serieBuscada.get());
        } else {
            System.out.println("Serie não encontrada");
        }
    }

    private void buscarSeriePorAtor() {
        System.out.println("Qual o nome a ser buscado?");
        var nomeAtor = leitura.nextLine();
        System.out.println("Qual a avaliação minima a ser buscado?");
        var avaliacao = leitura.nextDouble();
        List<Serie> seriesEncontradas = repositorio.findByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + ", Avaliação: " + s.getAvaliacao()));
    }

    private void buscarTop5Series() {
        List<Serie> topSeries = repositorio.findTop5ByOrderByAvaliacaoDesc();
        topSeries.forEach(s -> System.out.println(s.getTitulo() + ", Avaliação: " + s.getAvaliacao()));
    }

    private void buscarSeriePorCategoria() {
        System.out.println("Deseja buscar séries de que categoria/gênero? ");
        var nomeGenero = leitura.nextLine();
        Categoria categoria = Categoria.fromPortugues(nomeGenero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Séries da categoria " + nomeGenero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarSeriePorNumTempAvaliacao() {
        System.out.println("Qual o maximo de tempordas que deseja?");
        var numTemp = leitura.nextInt();
        leitura.nextLine();
        System.out.println("Qual a avaliação minima a ser buscado?");
        var avaliacao = leitura.nextDouble();

        List<Serie> series = repositorio.seriesPorTemporadaEAValiacao(numTemp, avaliacao);
        System.out.println("****** Series Encontradas ******");
        series.forEach(s -> System.out.println(
                s.getTitulo() + ", Avaliação: " + s.getAvaliacao() + ", Num. Temp.: " + s.getTotalTemporadas()
        ));
        System.out.println("********************************");
    }

    private void buscarEpisodioPorTitulo() {
        System.out.println("Escolha um episodio pelo titulo:");
        var episodioTitulo = leitura.nextLine();

        List<Episodio> episodiosEncontrados = repositorio.episodioPorTitulo(episodioTitulo);
        episodiosEncontrados.forEach(e -> System.out.printf("Serie: %s, Temporada: %s - Episodio: %s - %s\n",
                e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo()));
    }

    private void top5EpisodiosPorSerie(){
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> episodiosTop5 = repositorio.top5EpisodioPorSerie(serie);
            episodiosTop5.forEach(e -> System.out.printf("Serie: %s, Temporada: %s - Episodio: %s - %s, Avaliação: %.1f\n",
                    e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getTitulo(), e.getAvaliacao()));
        }
    }

    private void buscarEpisodioPorSerieEAno() {
        buscarSeriePorTitulo();
        if(serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            System.out.println("Qual o ano de lançamento minimo que deseja?");
            var anoBusca = leitura.nextInt();
            List<Episodio> episodios = repositorio.episodioPorSerieEAnoLancamento(serie, anoBusca);
            episodios.forEach(System.out::println);
        }
    }
}
