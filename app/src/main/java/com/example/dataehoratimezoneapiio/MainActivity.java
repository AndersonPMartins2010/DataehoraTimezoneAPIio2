package com.example.dataehoratimezoneapiio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private Button botaoRecuperar;
    private TextView textoResultado, tvData, tvHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botaoRecuperar = findViewById(R.id.buttonRecuperar);
        textoResultado = findViewById(R.id.textResultado);
        tvData = findViewById(R.id.tvData);
        tvHora = findViewById(R.id.tvHora);

        botaoRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTask task = new MyTask();
                //obter uma chave para realizar a consulta na API - https://timezoneapi.io/developers/timezone
                String urlApi = "https://timezoneapi.io/api/ip/?token= SUA CHAVE";
                task.execute(urlApi);
            }
        });

    }
    //criando a task para executar a requisição em outra therd, para não travar a "tela" principal
    //do App
    /*Sendo
     * 1º parametro String - a url de requisição
     * 2º parametro - o progresso, que nesse caso não será exibido, por isso é Void
     * 3º parametro - String de retorno
     * ness ponto clicar na lâmpada para implementar o método necessário doInBackground
     *class MyTask extends AsyncTask <String, Void, String>{ */

    class MyTask extends AsyncTask<String, Void, String> {
        //Clicar bDireito para sobre escrever os métodos

        //Será executado antes da tarefa, que é baixar informações da url
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override//processo sendo executado
        protected String doInBackground(String... strings) {

            String stringUrl = strings[0];
            InputStream inputStream = null;
            InputStreamReader inputStreamReader = null;
            StringBuffer buffer = null;

            try { //para impedir erro na digitação das urls

                URL url = new URL(stringUrl); //converte a String para obj URL
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

                // Recupera os dados em Bytes
                inputStream = conexao.getInputStream();

                //inputStreamReader lê os dados em Bytes e decodifica para caracteres
                inputStreamReader = new InputStreamReader( inputStream );

                //Objeto utilizado para leitura dos caracteres do InpuStreamReader
                BufferedReader reader = new BufferedReader( inputStreamReader );
                buffer = new StringBuffer();
                String linha = "";

                while((linha = reader.readLine()) != null){
                    buffer.append( linha );
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return buffer.toString();
        }

        @Override // processo a ser executado após o término de baixar as informações da url
        protected void onPostExecute(String resultado) {
            super.onPostExecute(resultado);


            String objetoData = null;
            String objetoDateTime = null;
            String date = null;
            String time = null;
            String dia = null;
            String mes = null;
            String ano = null;

            try {

                JSONObject jsonObject = new JSONObject(resultado);
                objetoData = jsonObject.getString("data");

                JSONObject jsonObjectDataTime = new JSONObject(objetoData);

                objetoDateTime = jsonObjectDataTime.getString("datetime");

                JSONObject jsonObjectReal = new JSONObject(objetoDateTime);
                date = jsonObjectReal.getString("date");
                time = jsonObjectReal.getString("time");
                dia = jsonObjectReal.getString("day");
                mes = jsonObjectReal.getString("month_wilz");
                ano = jsonObjectReal.getString("year");


            } catch (JSONException e) {
                e.printStackTrace();
            }


            textoResultado.setText( "Dia: " + dia +" Mês: "+mes+" Ano: "+ano );
            tvData.setText("Data atual: " + date);
            tvHora.setText("Hora atual: " + time);
        }
    }
}