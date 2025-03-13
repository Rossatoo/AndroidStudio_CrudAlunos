package com.example.exemplocrud1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Dao;

import java.io.ByteArrayOutputStream;

import lombok.NonNull;

public class MainActivity extends AppCompatActivity {

    private EditText nome;
    private EditText cpf;
    private EditText telefone;

    private AlunoDAO dao;

    private Aluno aluno = null;



    private ImageView imageView;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dao = appDatabase.getInstance(this).alunoDaoRoom();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Vinculando os campos do layout com as variaveis do Java
        nome = findViewById(R.id.editNome);
        cpf = findViewById(R.id.editCPF);
        telefone = findViewById(R.id.editTelefone);
        imageView = findViewById(R.id.imageView);
        Button btnTakePhoto = findViewById(R.id.btnTakePhoto);

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),

                result -> {
                    if (result.getResultCode() == RESULT_OK){
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");

                        Bitmap imagemCorrigida = corrigirOrientacao(imageBitmap);

                        imageView.setImageBitmap(imagemCorrigida);
                    }
                }
        );


        Intent it = getIntent();
        if(it.hasExtra("aluno")){
            aluno = (Aluno) it.getSerializableExtra("aluno");
            nome.setText(aluno.getNome().toString());
            cpf.setText(aluno.getCpf().toString());
            telefone.setText(aluno.getTelefone());

            byte[] fotoBytes = aluno.getFotoBytes();
            if (fotoBytes != null && fotoBytes.length > 0){
                Bitmap bitmap = BitmapFactory.decodeByteArray(fotoBytes, 0, fotoBytes.length);
                imageView.setImageBitmap(bitmap);
            }
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

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();

        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            byte[] fotoBytes = stream.toByteArray();
            aluno.setFotoBytes(fotoBytes);
        }

        if (aluno == null) {
            // Criar objeto Aluno
            Aluno aluno = new Aluno();
            aluno.setNome(nomeDigitado);
            aluno.setCpf(cpfDigitado);
            aluno.setTelefone(telefoneDigitado);

            if (imageView.getDrawable() != null){
                BitmapDrawable drawable1 = (BitmapDrawable) imageView.getDrawable();

                Bitmap bitmap = drawable1.getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] fotoBytes = stream.toByteArray();

                aluno.setFotoBytes(fotoBytes);
            }
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

            if (imageView.getDrawable() != null){
                BitmapDrawable drawable1 = (BitmapDrawable) imageView.getDrawable();

                Bitmap bitmap = drawable1.getBitmap();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] fotoBytes = stream.toByteArray();

                aluno.setFotoBytes(fotoBytes);
            }

            dao.atualizar(aluno);
            Toast.makeText(this, "Aluno atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        }
        // Fecha a tela de cadastro e volta para a listagem
        irParaListar(view);
    }

    public void tirarFoto(View view){
        checkCameraPermissionAndStart();
    }

    private void checkCameraPermissionAndStart(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Verifica se a solicitação de permissão é referente à câmera
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            // Verifique se a permissão foi concedida
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, inicie a câmera
                startCamera();
            } else {
                // Permissão negada, informe ao usuário ou tome as medidas apropriadas
                Toast.makeText(this, "A permissão da câmera é necessária para tirar fotos.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        // Cria uma nova intenção para capturar uma imagem usando a ação padrão da câmera
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Verifica se existe uma atividade disponível no dispositivo que possa lidar com a intenção de captura de imagem
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Inicia a atividade de captura de imagem e espera o resultado através do 'cameraLauncher'
            cameraLauncher.launch(takePictureIntent);
        }
    }

    private Bitmap corrigirOrientacao(Bitmap bitmap){
        if (bitmap == null) return null;

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return  Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix,
                true);
    }

    public void irParaListar (View view){
        Intent intent = new Intent(this, MainActivityListarAlunos.class);
        startActivity(intent);
    }

}