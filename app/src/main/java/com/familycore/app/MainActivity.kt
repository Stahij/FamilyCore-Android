package com.familycore.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.util.Date
import java.util.UUID

data class FamilyNote(val id: String, val title: String, val body: String, val updatedAt: Long)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { Surface(Modifier.fillMaxSize()) { FamilyCoreApp() } } }
    }
}

@Composable
private fun FamilyCoreApp() {
    val context = LocalContext.current
    val notes = remember { mutableStateListOf<FamilyNote>().apply { addAll(NoteStore.load(context)) } }
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    var tab by remember { mutableStateOf("notes") }
    var status by remember { mutableStateOf("Данные хранятся на этом устройстве.") }

    fun persist() {
        NoteStore.save(context, notes)
        status = "Сохранено · " + DateFormat.getTimeInstance(DateFormat.SHORT).format(Date())
    }
    val filtered = notes.filter { (it.title + " " + it.body).contains(search, ignoreCase = true) }.sortedByDescending { it.updatedAt }

    Column(Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("FamilyCore", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
        Text("Семейный помощник · локальный MVP")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (tab == "notes") Button(onClick = { tab = "notes" }) { Text("Заметки") } else OutlinedButton(onClick = { tab = "notes" }) { Text("Заметки") }
            if (tab == "ai") Button(onClick = { tab = "ai" }) { Text("Локальный ИИ") } else OutlinedButton(onClick = { tab = "ai" }) { Text("Локальный ИИ") }
        }
        HorizontalDivider()
        if (tab == "notes") {
            Text("Семейные заметки", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            OutlinedTextField(title, { title = it }, Modifier.fillMaxWidth(), label = { Text("Заголовок") }, singleLine = true)
            OutlinedTextField(body, { body = it }, Modifier.fillMaxWidth(), label = { Text("Текст заметки") }, minLines = 2)
            Button(onClick = {
                if (title.isNotBlank() || body.isNotBlank()) {
                    notes.add(FamilyNote(UUID.randomUUID().toString(), title.trim().ifBlank { "Без названия" }, body.trim(), System.currentTimeMillis()))
                    title = ""; body = ""; persist()
                } else status = "Введите заголовок или текст заметки."
            }, modifier = Modifier.fillMaxWidth()) { Text("Сохранить заметку") }
            OutlinedTextField(search, { search = it }, Modifier.fillMaxWidth(), label = { Text("Поиск по заметкам") }, singleLine = true)
            Text("Записей: ${notes.size}", style = MaterialTheme.typography.labelLarge)
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered, key = { it.id }) { note ->
                    Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(note.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            if (note.body.isNotBlank()) Text(note.body)
                            Text(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(note.updatedAt)), style = MaterialTheme.typography.labelSmall)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(onClick = {
                                    title = note.title; body = note.body; notes.remove(note); persist()
                                    status = "Заметка перенесена в форму. Нажми «Сохранить заметку»."
                                }) { Text("Изменить") }
                                OutlinedButton(onClick = { notes.remove(note); persist() }) { Text("Удалить") }
                            }
                        }
                    }
                }
                if (filtered.isEmpty()) item { Text("Пока нет заметок. Создай первую запись выше.", Modifier.padding(vertical = 16.dp)) }
            }
        } else {
            Text("Локальный ИИ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Движок пока не подключён", fontWeight = FontWeight.Bold)
                    Text("Следующий этап — интеграция llama.cpp и отдельная загрузка GGUF-модели.")
                    Text("Кандидат для тестирования: Qwen2.5-1.5B-Instruct GGUF Q4_K_M. Скорость и совместимость нужно проверить на конкретном телефоне.")
                    Text("Эта вкладка — честная заглушка, а не работающий ИИ. Сетевые запросы не выполняются.")
                }
            }
        }
        Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
    }
}

private object NoteStore {
    private const val PREFS = "familycore_local_store"
    private const val KEY_NOTES = "notes_json"
    fun load(context: Context): List<FamilyNote> = try {
        val array = JSONArray(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_NOTES, "[]") ?: "[]")
        (0 until array.length()).map { i ->
            val item = array.getJSONObject(i)
            FamilyNote(item.getString("id"), item.getString("title"), item.getString("body"), item.getLong("updatedAt"))
        }
    } catch (_: Exception) { emptyList() }

    fun save(context: Context, notes: List<FamilyNote>) {
        val array = JSONArray()
        notes.forEach { n -> array.put(JSONObject().apply {
            put("id", n.id); put("title", n.title); put("body", n.body); put("updatedAt", n.updatedAt)
        }) }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_NOTES, array.toString()).apply()
    }
}
