package com.example.exemplocrud1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlunoDAO {

    @Insert
    long inserir(Aluno aluno);

    @Update
    void atualizar(Aluno aluno);

    @Query("SELECT * FROM alunos")
    List<Aluno> obterTodos();

    @Delete
    void excluir(Aluno aluno);

    @Query("SELECT COUNT(*) FROM alunos WHERE cpf = :cpf")
    int cpfExistente(String cpf);

}
