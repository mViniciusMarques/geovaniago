package com.m2brcorp.geostatus.Enum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinic on 26/05/2017.
 */

public enum GeneroEnum {

    MASCULINO("MASC","Masculino"),
    FEMININO("FEM","Feminino");

    private String id;
    private String descricao;

    GeneroEnum(String id, String descricao) {
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

    public static List<String> getGeneroDescricao(){
        List<String> descricoes = new ArrayList<>();
        for ( GeneroEnum genero : GeneroEnum.values()) {
            descricoes.add(genero.descricao);
        }
        return descricoes;
    }
}
