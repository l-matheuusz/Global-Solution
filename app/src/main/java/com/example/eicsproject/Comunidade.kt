package com.example.eicsproject

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.FirebaseDatabase

class Comunidade : Fragment() {

    private lateinit var etNomeItem: EditText
    private lateinit var etDescricaoItem: EditText
    private lateinit var etConsumoItem: EditText
    private lateinit var btnSalvar: Button
    private val database = FirebaseDatabase.getInstance().reference.child("dados")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_comunidade, container, false)

        val fabEditar: FloatingActionButton = view.findViewById(R.id.redirectEditar)
        fabEditar.setOnClickListener {
            val intent = Intent(activity, TelaEditar::class.java)
            startActivity(intent)
        }
        val fabListagem: FloatingActionButton = view.findViewById(R.id.redirectListagem)
        fabListagem.setOnClickListener {
            val intent = Intent(activity, TelaListagem::class.java)
            startActivity(intent)
        }

        etNomeItem = view.findViewById(R.id.et_nome_item)
        etDescricaoItem = view.findViewById(R.id.et_descricao_item)
        etConsumoItem = view.findViewById(R.id.et_consumo_item)
        btnSalvar = view.findViewById(R.id.btn_salvar)

        btnSalvar.setOnClickListener { salvarDados() }


        return view
    }

    private fun salvarDados() {
        val nome = etNomeItem.text.toString()
        val descricao = etDescricaoItem.text.toString()
        val consumo = etConsumoItem.text.toString().toDoubleOrNull()

        if (nome.isNotEmpty() && consumo != null) {
            val newItemRef = database.push()
            val item = Item(
                id = newItemRef.key ?: "",
                nome = nome,
                descricao = descricao,
                consumo = consumo
            )
            newItemRef.setValue(item).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Item salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    limparFormulario()
                } else {
                    Toast.makeText(requireContext(), "Erro ao salvar item!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Preencha os campos obrigatórios!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limparFormulario() {
        etNomeItem.text.clear()
        etDescricaoItem.text.clear()
        etConsumoItem.text.clear()
    }
}
