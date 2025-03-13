package com.example.exemplocrud1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivityListarAlunos extends AppCompatActivity {

    private ListView listView;
    private AlunoDAO dao;
    private List<Aluno> alunos;
    private List<Aluno> alunosFiltrados = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_listar_alunos);

        listView = findViewById(R.id.listaAlunos);
        registerForContextMenu(listView);
        dao = appDatabase.getInstance(this).alunoDaoRoom();
        alunos = dao.obterTodos();
        alunosFiltrados.addAll(alunos);

        ArrayAdapter<Aluno> adaptador = new ArrayAdapter<Aluno>(this, android.R.layout.simple_list_item_1, alunos);
        listView.setAdapter(adaptador);

        }

        public void excluir(MenuItem item){
            //pegar qual a posicao do item da lista que eu selecionei para excluir
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            final Aluno alunoExcluir = alunosFiltrados.get(menuInfo.position);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Atenção")
                    .setMessage("Realmente deseja excluir?")
                    .setNegativeButton("Não", null)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int whitch) {
                            alunosFiltrados.remove(alunoExcluir);
                            alunos.remove(alunoExcluir);
                            dao.excluir(alunoExcluir);
                            listView.invalidateViews();
                        }
                    }).create();
                dialog.show();
        }

        public void atualizar(MenuItem item){
            //pegar a posição do item da lista que foi selecionado para atualizar
            AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            final Aluno alunoAtualizar = alunosFiltrados.get(menuInfo.position);

            //Ao selecionar atualizar, abrir a janela de cadastro e enviar o aluno para la
            Intent it = new Intent(this, MainActivity.class);

            //Preencher os campos com os dados do aluno selecionado
            it.putExtra("aluno", alunoAtualizar);
            startActivity(it);
        }

        public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
            super.onCreateContextMenu(menu, v, menuInfo);

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_contexto, menu);
        }

    public void irParaCreate(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    }
