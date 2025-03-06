package com.example.exemplocrud1;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
public class Aluno  implements Serializable {
    private Integer id;
    private String nome;
    private String cpf;
    private String telefone;

    @Override
    public String toString() {
        return nome + "\nCPF: " + cpf + "\nTelefone: " + telefone;
    }


}



