package com.example.exemplocrud1;

import java.io.Serializable;

import lombok.Data;

@Data
public class Aluno  implements Serializable {
    private Integer id;
    private String nome;
    private String cpf;
    private String telefone;
}
