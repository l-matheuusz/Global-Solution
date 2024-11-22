package com.example.eicsproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class TelaEditar : AppCompatActivity() {

    private lateinit var adapter: DadosAdapter
    private val database = FirebaseDatabase.getInstance().reference.child("dados")

    private lateinit var etNomeItem: EditText
    private lateinit var etDescricaoItem: EditText
    private lateinit var etConsumoItem: EditText
    private lateinit var btnSalvar: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela_editar)

        val fabListagem: FloatingActionButton = findViewById(R.id.redirectList)
        fabListagem.setOnClickListener {
            val intent = Intent(this, TelaListagem::class.java)
            startActivity(intent)
        }


        etNomeItem = findViewById(R.id.et_nome_item)
        etDescricaoItem = findViewById(R.id.et_descricao_item)
        etConsumoItem = findViewById(R.id.et_consumo_item)
        btnSalvar = findViewById(R.id.btn_salvar)


        val recyclerView: RecyclerView = findViewById(R.id.rv_lista_dados)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = DadosAdapter { item, action ->
            when (action) {
                "editar" -> {
                    preencherFormulario(item)
                }
                "excluir" -> {
                    FirebaseRepository.excluirItem(
                        itemId = item.id,
                        onSuccess = {
                            Toast.makeText(this, "Item excluído com sucesso!", Toast.LENGTH_SHORT).show()
                            carregarDados()
                        },
                        onFailure = { errorMessage ->
                            Toast.makeText(this, "Erro ao excluir: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }

        recyclerView.adapter = adapter

        btnSalvar.setOnClickListener { salvarDados() }


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
            },
            onFailure = { errorMessage ->
                Toast.makeText(this, "Erro ao carregar dados: $errorMessage", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun salvarDados() {
        val nome = etNomeItem.text.toString()
        val descricao = etDescricaoItem.text.toString()
        val consumo = etConsumoItem.text.toString().toDoubleOrNull()


        if (nome.isNotEmpty() && consumo != null) {
            val item = Item(
                id = currentItemId ?: "",
                nome = nome,
                descricao = descricao,
                consumo = consumo
            )

            if (item.id.isNotEmpty()) {
                database.child(item.id).setValue(item).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(this, "Item atualizado!", Toast.LENGTH_SHORT).show()
                        limparFormulario()
                        currentItemId = ""
                        carregarDados()
                    } else {
                        Toast.makeText(this, "Erro ao atualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Para criar novos itens, vá para a página de criação.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun preencherFormulario(item: Item) {
        etNomeItem.setText(item.nome)
        etDescricaoItem.setText(item.descricao)
        etConsumoItem.setText(item.consumo.toString())
        currentItemId = item.id
    }

    private fun excluirItem(item: Item) {
        database.child(item.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Item excluído com sucesso", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao excluir item", Toast.LENGTH_SHORT).show()
            }
    }
    private fun limparFormulario() {
        etNomeItem.text.clear()
        etDescricaoItem.text.clear()
        etConsumoItem.text.clear()
    }
}

private var currentItemId: String = ""  // Variável para armazenar o ID do item em edição
