package com.dlsra.assistenciatecnica707;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class InicioEntrar extends AppCompatActivity {
    //variaveis
    ConstraintLayout ecra = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_entrar_activity);

        aponta_elementos();
        implementa_inventos();
    }

    private void aponta_elementos(){
        ecra = (ConstraintLayout) findViewById(R.id.cLBemVindo);
    }

    private void implementa_inventos(){
        ecra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navega2credenciais();
            }
        });
    }

    private void navega2credenciais(){
        Log.i("Troca Tela", "Credenciais");
        startActivity(new Intent(this, InicioAcesso.class));
    }
}
