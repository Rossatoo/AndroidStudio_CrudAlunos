package com.example.exemplocrud1;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity(tableName = "aluno")
@Data
public class Aluno  implements Serializable {

    @PrimaryKey(autoGenerate = true)

    private Integer id;
    private String nome;
    private String cpf;
    private String telefone;

    @ColumnInfo(name = "foto")
    private byte[] fotoBytes;


    @Override
    public String toString() {
        return nome + "\nCPF: " + cpf + "\nTelefone: " + telefone;
    }


}



