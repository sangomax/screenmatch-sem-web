package br.com.agdeo.screenmatch.principal;

import br.com.agdeo.screenmatch.model.DadosEpisodio;
import br.com.agdeo.screenmatch.model.DadosSerie;
import br.com.agdeo.screenmatch.model.DadosTemporada;
import br.com.agdeo.screenmatch.model.Episodio;
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

    private Scanner leitura = new Scanner(System.in);

    private ConsumoAPI consumo = new ConsumoAPI();
    private ConverteDados conversor = new ConverteDados();

    public void exibirMenu() {
        System.out.println("Digite o nome de uma serie: ");
        var serieNome = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + serieNome.replace(" ", "+") + API_KEY);
        DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            json = consumo.obterDados(ENDERECO + serieNome.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada dadosTemporada = conversor.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }
//        temporadas.forEach(System.out::println);
//
//        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(t.numeroTemporada() + "." + e.numeroEpisodio() + " - " + e.titulo())));
//
//        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
//                .flatMap(t -> t.episodios().stream())
//                .collect(Collectors.toList());
//
//        System.out.println("Top 5 Episodios");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .limit(5)
//                .forEach(System.out::println);
//
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(e -> new Episodio(t.numeroTemporada(),e)))
                .collect(Collectors.toList());

        episodios.forEach(System.out::println);

//        System.out.println("A partir de que ano você deseja ver os episódios? ");
//        var ano = leitura.nextInt();
//        leitura.nextLine();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//        DateTimeFormatter dtFormatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() != null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() +
//                                " - Episodio: " + e.getTitulo() +
//                                " - Data Lançamento: " + e.getDataLancamento().format(dtFormatador)
//                ));
//
//        System.out.println("Digite o titulo do episodio desejado: ");
//        var trechoBusca = leitura.nextLine();
//
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toLowerCase().contains(trechoBusca.toLowerCase()))
//                .findFirst();
//
//        if(episodioBuscado.isPresent()) {
//            System.out.println("Episodio Encontrado");
//            System.out.println(episodioBuscado.get());
//        } else {
//            System.out.println("Episodio não encontrado");
//        }
//

        Map<Integer, Double> avaliacaoPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));

        System.out.println(avaliacaoPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));

        System.out.println("Media da Temporada: " + est.getAverage());
        System.out.println("Melhor Avaliação da Temporada: " + est.getMax());
        System.out.println("Pior Avaliação da Temporada: " + est.getMin());
        System.out.println("Numero de Episodios Avaliados: " + est.getCount());
    }
}
