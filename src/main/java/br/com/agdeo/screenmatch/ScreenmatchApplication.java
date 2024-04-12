package br.com.agdeo.screenmatch;

import br.com.agdeo.screenmatch.principal.Principal;
import br.com.agdeo.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ScreenmatchApplication.class, args);
    }
    @Autowired
    private SerieRepository repositorio;

    @Override
    public void run(String... args) throws Exception {
        Principal principal = new Principal(repositorio);
        principal.exibirMenu();
    }
}
