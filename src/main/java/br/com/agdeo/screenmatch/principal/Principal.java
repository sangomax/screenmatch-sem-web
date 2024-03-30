package br.com.agdeo.screenmatch.principal;

import br.com.agdeo.screenmatch.model.DadosSerie;
import br.com.agdeo.screenmatch.model.DadosTemporada;
import br.com.agdeo.screenmatch.service.ConsumoAPI;
import br.com.agdeo.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        temporadas.forEach(System.out::println);
    }
}