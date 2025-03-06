package com.example.exemplocrud1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText nome;
    private EditText cpf;
    private EditText telefone;
    private AlunoDAO  dao;

    private Aluno aluno = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Vinculando os campos do layout com as variaveis do Java
        nome = findViewById(R.id.editNome);
        cpf = findViewById(R.id.editCPF);
        telefone = findViewById(R.id.editTelefone);

        dao = new AlunoDAO(this);

        Intent it = getIntent();
        if(it.hasExtra("aluno")){
            aluno = (Aluno) it.getSerializableExtra("aluno");
            nome.setText(aluno.getNome().toString());
            cpf.setText(aluno.getCpf().toString());
            telefone.setText(aluno.getTelefone());
        }

    }

    //metodo para salvar quando clicado
    public void salvar(View view){

        String nomeDigitado = nome.getText().toString().trim();
        String cpfDigitado = cpf.getText().toString().trim();
        String telefoneDigitado = telefone.getText().toString().trim();

        if(nomeDigitado.isEmpty() || cpfDigitado.isEmpty() || telefoneDigitado.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dao.validaCpf(cpfDigitado)){
            Toast.makeText(this, "CPF invalido!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (aluno == null || !cpfDigitado.equals(aluno.getCpf())) {
            if (dao.cpfExistente(cpfDigitado)) {
                Toast.makeText(this, "CPF ja existe no banco de dados!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (!dao.validaTelefone(telefoneDigitado)){
            Toast.makeText(this, "Telefone invalido! Use o formato (XX) 9XXXX-XXXX", Toast.LENGTH_SHORT).show();
            return;
        }

        if (aluno == null) {
            // Criar objeto Aluno
            Aluno aluno = new Aluno();
            aluno.setNome(nomeDigitado);
            aluno.setCpf(cpfDigitado);
            aluno.setTelefone(telefoneDigitado);
            // Inserir aluno no banco de dados
            long id = dao.inserir(aluno);
            if (id != -1) {
                Toast.makeText(this, "Aluno inserido com id: " + id, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao inserir aluno. Tente novamente.", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            // Atualização de um aluno existente
            aluno.setNome(nomeDigitado);
            aluno.setCpf(cpfDigitado);
            aluno.setTelefone(telefoneDigitado);
            dao.atualizar(aluno);
            Toast.makeText(this, "Aluno atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        }
        // Fecha a tela de cadastro e volta para a listagem
        irParaListar(view);
    }


    public void irParaListar(View view){
        Intent intent = new Intent(this, MainActivityListarAlunos.class);
        startActivity(intent);
    }

}