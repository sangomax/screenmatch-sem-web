package br.com.agdeo.screenmatch.model;

public enum Categoria {

    ACAO("Action"),
    ROMANCE("Romace"),
    COMEDIA("Comedy"),
    DRAMA("Drama"),
    CRIME("Crime"),
    ANIMACAO("Animation");

    private String categoriaPortugues;

    Categoria(String categoriaOmdb){
        this.categoriaPortugues = categoriaOmdb;
    }

    public static Categoria fromPortugues(String text) {
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaPortugues.equalsIgnoreCase(text)) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Nenhuma categoria encontrada para a string fornecida: " + text);
    }
}
