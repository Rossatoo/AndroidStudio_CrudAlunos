package com.example.exemplocrud1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AlunoDAOC {

    private Conexao conexao;

    private SQLiteDatabase banco;


    //context é usado para conexao
    public AlunoDAOC(Context context){
        conexao = new Conexao(context); //cria uma conexao
        banco = conexao.getWritableDatabase(); //iniciar um banco de dados para escrita
    }

    //metodo para inserir

    public long inserir(Aluno aluno){ //long por que retorna o id do aluno
        if(!cpfExistente(aluno.getCpf())) {
            ContentValues values = new ContentValues(); //valores que irei inserir
            values.put("nome", aluno.getNome());
            values.put("cpf", aluno.getCpf());
            values.put("telefone", aluno.getTelefone());
            values.put("fotoBytes", aluno.getFotoBytes());
            return banco.insert("aluno", null, values); //tabela aluno, sao tera valores vazios e valores values
        }
        else {
            return -1;
        }
    }

    public boolean cpfExistente(String cpf){
        Cursor cursor = banco.query("aluno", new String[]{"id"}, "cpf = ?", new String[]{cpf}, null, null,null);
        boolean cpfExiste = cursor.getCount() > 0;
        cursor.close();
        return cpfExiste;
    }

    public boolean validaCpf(String cpf) {
        cpf = cpf.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos

        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) { // Verifica tamanho e CPFs inválidos
            return false;
        }

        try {
            int soma = 0;
            int peso = 10;

            // Cálculo do primeiro dígito verificador
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int primeiroDigito = (soma * 10) % 11;
            if (primeiroDigito == 10) primeiroDigito = 0;

            if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
                return false;
            }

            // Cálculo do segundo dígito verificador
            soma = 0;
            peso = 11;

            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int segundoDigito = (soma * 10) % 11;
            if (segundoDigito == 10) segundoDigito = 0;

            return segundoDigito == Character.getNumericValue(cpf.charAt(10));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validaTelefone(String telefone){
        String regex = "^\\(\\d{2}\\)9\\d{4}-\\d{4}$";
        return Pattern.matches(regex, telefone);
    }

    //metodo para listar alunos
    public List<Aluno> obterTodos(){
        List<Aluno> alunos = new ArrayList<>();
        //cursor aponta para as linhas retornadas
        Cursor cursor = banco.query("aluno", new String[]{"id", "nome", "cpf", "telefone", "fotoBytes"},
                null, null, null, null, null);

        while(cursor.moveToNext()){
            Aluno aluno = new Aluno();
            aluno.setId(cursor.getInt(0));
            aluno.setNome(cursor.getString(1));
            aluno.setCpf(cursor.getString(2));
            aluno.setTelefone(cursor.getString(3));
            alunos.add(aluno);
        }
        return alunos;
    }

    public void excluir(Aluno a){
        banco.delete("aluno", "id = ?",new String[]{a.getId().toString()}); // no lugar do ? vai colocar o id do aluno
    }

    public void atualizar(Aluno aluno){
        ContentValues values = new ContentValues(); //valores que serao inseridos
        values.put("nome", aluno.getNome());
        values.put("cpf", aluno.getCpf());
        values.put("telefone", aluno.getTelefone());
        values.put("fotoBytes", aluno.getFotoBytes());
        banco.update("aluno", values, "id = ?", new String[]{aluno.getId().toString()});
    }


}
