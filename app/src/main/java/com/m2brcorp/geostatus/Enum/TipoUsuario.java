package com.m2brcorp.geostatus.Enum;

/**
 * Created by vinic on 06/05/2017.
 */

public enum TipoUsuario {
    ADMIN("AD","Administrador"),
    VISUALIAZADOR("VI","Visualizador"),
    EDITOR("ED","Editor");

    private String id;
    private String descricao;

    TipoUsuario(String id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
