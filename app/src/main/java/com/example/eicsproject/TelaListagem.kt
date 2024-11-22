package com.example.eicsproject

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class TelaListagem : AppCompatActivity() {

    private lateinit var adapter: DadosAdapter
    private val database = FirebaseDatabase.getInstance().reference.child("itens") // Ajuste conforme necessário

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_listagem)

        val fabEditar: FloatingActionButton = findViewById(R.id.redirectEdit)
        fabEditar.setOnClickListener {
            val intent = Intent(this, TelaEditar::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.rv_lista_dados)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DadosAdapter { item, action ->
            when (action) {
                "editar" -> {
                    Toast.makeText(this, "Para editar: ${item.nome} vá para a pagina de edição", Toast.LENGTH_SHORT).show()
                }
                "excluir" -> {
                    Toast.makeText(this, "Para excluir: ${item.nome} vá para a pagina de edição", Toast.LENGTH_SHORT).show()
                }
            }
        }


        recyclerView.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        carregarDados()
    }

    private fun carregarDados() {
        FirebaseRepository.carregarDados(
            onSuccess = { lista, consumoTotal ->
                adapter.submitList(lista)
                findViewById<TextView>(R.id.tv_consumo_total2).text = "Consumo Total: $consumoTotal kWh"
            },
            onFailure = { errorMessage ->
                Toast.makeText(this, "Erro ao carregar dados: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }


}
