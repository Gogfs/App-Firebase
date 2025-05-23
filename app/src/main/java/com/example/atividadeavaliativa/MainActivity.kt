package com.example.atividadeavaliativa

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.atividadeavaliativa.ui.theme.AtividadeAvaliativaTheme
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AtividadeAvaliativaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppAula()
                }
            }
        }
    }
}

@Composable
fun AppAula() {
    var nome by remember { mutableStateOf("") }
    var sobrenome by remember { mutableStateOf("") }
    var endereco by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    val db = FirebaseFirestore.getInstance()
    var resultados by remember { mutableStateOf(listOf<Map<String, Any>>()) }

    Column(
        modifier = Modifier
            .background(Color.LightGray)
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text("App cadastro", fontFamily = FontFamily.Cursive, fontSize = 30.sp)
            Spacer(modifier = Modifier.padding(8.dp))

            TextField(value = nome, onValueChange = { nome = it }, label = { Text("Nome:") })
            Spacer(modifier = Modifier.padding(8.dp))
            TextField(value = sobrenome, onValueChange = { sobrenome = it }, label = { Text("Sobrenome:") })
            Spacer(modifier = Modifier.padding(8.dp))
            TextField(value = endereco, onValueChange = { endereco = it }, label = { Text("Endereço:") })
            Spacer(modifier = Modifier.padding(8.dp))
            TextField(value = email, onValueChange = { email = it }, label = { Text("E-mail:") })
            Spacer(modifier = Modifier.padding(8.dp))
            TextField(value = senha, onValueChange = { senha = it }, label = { Text("Senha:") })
            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = {
                    val user = hashMapOf(
                        "nome" to nome,
                        "sobrenome" to sobrenome,
                        "endereco" to endereco,
                        "email" to email,
                        "senha" to senha
                    )
                    db.collection("users")
                        .add(user)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                    // Limpa os campos após o cadastro
                    nome = ""
                    sobrenome = ""
                    endereco = ""
                    email = ""
                    senha = ""
                },
                modifier = Modifier.padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cadastrar", fontSize = 20.sp)
            }

            Button(
                onClick = {
                    db.collection("users")
                        .get()
                        .addOnSuccessListener { result ->
                            val listaResultados = mutableListOf<Map<String, Any>>()
                            for (document in result) {
                                listaResultados.add(document.data)
                            }
                            resultados = listaResultados
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents.", exception)
                        }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Consultar", fontSize = 20.sp)
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(top = 16.dp)
        ) {
            items(resultados) { user ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .background(Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text("Nome: ${user["nome"]}", fontWeight = FontWeight.Bold)
                    Text("Sobrenome: ${user["sobrenome"]}")
                    Text("Endereço: ${user["endereco"]}")
                    Text("E-mail: ${user["email"]}")
                    Text("Senha: ${user["senha"]}")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AtividadeAvaliativaTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            AppAula()
        }
    }
}
