package com.familycore.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.json.JSONArray
import org.json.JSONObject
import java.text.DateFormat
import java.util.Date
import java.util.UUID

private val Ink = Color(0xFF302B3D)
private val Cream = Color(0xFFFFFBF5)
private val Sage = Color(0xFFE8F1E8)

private enum class Screen(val label: String, val symbol: String) {
    Home("Дом", "⌂"), Tasks("Задачи", "✓"), Calendar("Календарь", "▦"),
    Chat("Чат", "♡"), Profile("Профиль", "○")
}
private data class Note(val id: String, val title: String, val body: String, val updatedAt: Long)
private data class FamilyTask(val id: String, val title: String, val done: Boolean, val assignee: String = "", val dueDate: String = "", val priority: String = "Обычный")
private data class FamilyEvent(val id: String, val title: String, val whenText: String)
private data class ShoppingItem(val id: String, val title: String, val bought: Boolean)
private data class PantryItem(val id: String, val title: String, val expiry: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            var themeName by remember { mutableStateOf(LocalStore.loadTheme(context)) }
            val primary = when (themeName) {
                "Шалфей" -> Color(0xFF557B65)
                "Океан" -> Color(0xFF397C91)
                else -> Color(0xFF7967B2)
            }
            val tint = when (themeName) {
                "Шалфей" -> Color(0xFFE8F1E8)
                "Океан" -> Color(0xFFE7F3F7)
                else -> Color(0xFFF0ECFA)
            }
            MaterialTheme(colorScheme = lightColorScheme(
                primary = primary, onPrimary = Color.White, background = Cream,
                surface = Color.White, onSurface = Ink, surfaceVariant = tint,
                secondaryContainer = Sage
            )) {
                Surface(Modifier.fillMaxSize(), color = Cream) {
                    FamilyCoreApp(themeName) { selected ->
                        themeName = selected
                        LocalStore.saveTheme(context, selected)
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyCoreApp(themeName: String, onThemeChanged: (String) -> Unit) {
    val context = LocalContext.current
    var screen by remember { mutableStateOf(Screen.Home) }
    var kitchenOpen by remember { mutableStateOf(false) }
    var roomOpen by remember { mutableStateOf<String?>(null) }
    var notesOpen by remember { mutableStateOf(false) }
    var settingsOpen by remember { mutableStateOf(false) }
    var profileName by remember { mutableStateOf(LocalStore.loadProfileName(context)) }
    var profileBirthday by remember { mutableStateOf(LocalStore.loadProfileBirthday(context)) }
    var profileRole by remember { mutableStateOf(LocalStore.loadProfileRole(context)) }
    val notes = remember { mutableStateListOf<Note>().apply { addAll(LocalStore.loadNotes(context)) } }
    val tasks = remember { mutableStateListOf<FamilyTask>().apply { addAll(LocalStore.loadTasks(context)) } }
    val events = remember { mutableStateListOf<FamilyEvent>().apply { addAll(LocalStore.loadEvents(context)) } }
    val shopping = remember { mutableStateListOf<ShoppingItem>().apply { addAll(LocalStore.loadShopping(context)) } }
    val pantry = remember { mutableStateListOf<PantryItem>().apply { addAll(LocalStore.loadPantry(context)) } }
    var noteTitle by remember { mutableStateOf("") }
    var noteBody by remember { mutableStateOf("") }
    var taskTitle by remember { mutableStateOf("") }
    var taskAssignee by remember { mutableStateOf("") }
    var taskDueDate by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf("Обычный") }
    var eventTitle by remember { mutableStateOf("") }
    var eventWhen by remember { mutableStateOf("") }
    var shoppingTitle by remember { mutableStateOf("") }
    var pantryTitle by remember { mutableStateOf("") }
    var pantryExpiry by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Данные сохраняются на этом устройстве · синхронизации пока нет") }

    Scaffold(
        containerColor = Cream,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                Screen.entries.forEach { item ->
                    NavigationBarItem(
                        selected = screen == item && !kitchenOpen,
                        onClick = { screen = item; kitchenOpen = false; roomOpen = null; notesOpen = false; settingsOpen = false },
                        icon = { Text(item.symbol, style = MaterialTheme.typography.titleMedium) },
                        label = { Text(item.label, maxLines = 1) }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(Modifier.height(3.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(Modifier.size(44.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(15.dp)), contentAlignment = Alignment.Center) {
                    Text("F", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text("FamilyCore", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Ink)
                    Text("Теплее вместе", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(50)) {
                    Text("v${BuildConfig.VERSION_NAME}", Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            HorizontalDivider(color = Color(0xFFE9E3F1))
            when {
                notesOpen -> NotesScreen(
                    notes.filter { (it.title + " " + it.body).contains(search, true) }.sortedByDescending { it.updatedAt },
                    noteTitle, { noteTitle = it }, noteBody, { noteBody = it }, search, { search = it },
                    onAddNote = {
                        if (noteTitle.isNotBlank() || noteBody.isNotBlank()) {
                            notes.add(Note(UUID.randomUUID().toString(), noteTitle.trim().ifBlank { "Без названия" }, noteBody.trim(), System.currentTimeMillis()))
                            LocalStore.saveNotes(context, notes); noteTitle = ""; noteBody = ""; status = "Заметка сохранена"
                        }
                    },
                    onDeleteNote = { item -> notes.remove(item); LocalStore.saveNotes(context, notes); status = "Заметка удалена" },
                    onBack = { notesOpen = false }
                )
                settingsOpen -> SettingsScreen(themeName, onThemeChanged, onBack = { settingsOpen = false })
                roomOpen != null -> RoomScreen(room = roomOpen!!, onBack = { roomOpen = null }, onKitchen = { roomOpen = null; kitchenOpen = true }, onTasks = { roomOpen = null; screen = Screen.Tasks }, onCalendar = { roomOpen = null; screen = Screen.Calendar }, onNotes = { roomOpen = null; notesOpen = true }, onChat = { roomOpen = null; screen = Screen.Chat })
                kitchenOpen -> KitchenScreen(
                    shopping, pantry, shoppingTitle, { shoppingTitle = it }, pantryTitle, { pantryTitle = it },
                    pantryExpiry, { pantryExpiry = it },
                    onAddShopping = {
                        if (shoppingTitle.isNotBlank()) {
                            shopping.add(ShoppingItem(UUID.randomUUID().toString(), shoppingTitle.trim(), false))
                            LocalStore.saveShopping(context, shopping); shoppingTitle = ""; status = "Покупка сохранена"
                        }
                    },
                    onToggleShopping = { item ->
                        val i = shopping.indexOfFirst { it.id == item.id }
                        if (i >= 0) shopping[i] = item.copy(bought = !item.bought)
                        LocalStore.saveShopping(context, shopping); status = "Список покупок сохранён"
                    },
                    onRemoveShopping = { item -> shopping.remove(item); LocalStore.saveShopping(context, shopping); status = "Покупка удалена" },
                    onAddPantry = {
                        if (pantryTitle.isNotBlank()) {
                            pantry.add(PantryItem(UUID.randomUUID().toString(), pantryTitle.trim(), pantryExpiry.ifBlank { "Не указан" }))
                            LocalStore.savePantry(context, pantry); pantryTitle = ""; pantryExpiry = ""; status = "Продукт добавлен в запасы"
                        }
                    },
                    onRemovePantry = { item -> pantry.remove(item); LocalStore.savePantry(context, pantry) },
                    onBack = { kitchenOpen = false }
                )
                screen == Screen.Home -> HomeScreen(
                    tasks.count { !it.done }, events.size, notes.size, shopping.count { !it.bought },
                    onOpenTasks = { screen = Screen.Tasks }, onOpenCalendar = { screen = Screen.Calendar },
                    onOpenNotes = { notesOpen = true }, onOpenKitchen = { kitchenOpen = true },
                    onOpenChat = { screen = Screen.Chat }, onOpenRoom = { roomOpen = it }
                )
                screen == Screen.Tasks -> TasksScreen(
                    tasks, taskTitle, { taskTitle = it }, taskAssignee, { taskAssignee = it }, taskDueDate, { taskDueDate = it }, taskPriority, { taskPriority = it },
                    onAdd = {
                        if (taskTitle.isNotBlank()) {
                            tasks.add(FamilyTask(UUID.randomUUID().toString(), taskTitle.trim(), false, taskAssignee.trim(), taskDueDate.trim(), taskPriority))
                            LocalStore.saveTasks(context, tasks); taskTitle = ""; taskAssignee = ""; taskDueDate = ""; taskPriority = "Обычный"; status = "Задача сохранена"
                        }
                    },
                    onToggle = { item ->
                        val i = tasks.indexOfFirst { it.id == item.id }
                        if (i >= 0) tasks[i] = item.copy(done = !item.done)
                        LocalStore.saveTasks(context, tasks); status = "Статус задачи сохранён"
                    },
                    onRemove = { item -> tasks.remove(item); LocalStore.saveTasks(context, tasks); status = "Задача удалена" }
                )
                screen == Screen.Calendar -> CalendarScreen(
                    events, eventTitle, { eventTitle = it }, eventWhen, { eventWhen = it },
                    onAdd = {
                        if (eventTitle.isNotBlank()) {
                            events.add(FamilyEvent(UUID.randomUUID().toString(), eventTitle.trim(), eventWhen.ifBlank { "Дата не указана" }))
                            LocalStore.saveEvents(context, events); eventTitle = ""; eventWhen = ""; status = "Событие сохранено"
                        }
                    },
                    onRemove = { item -> events.remove(item); LocalStore.saveEvents(context, events); status = "Событие удалено" }
                )
                screen == Screen.Chat -> ChatScreen()
                screen == Screen.Profile -> ProfileScreen(
                    profileName, { profileName = it; LocalStore.saveProfileName(context, it) },
                    profileBirthday, { profileBirthday = it; LocalStore.saveProfileBirthday(context, it) },
                    profileRole, { profileRole = it; LocalStore.saveProfileRole(context, it) },
                    onOpenSettings = { settingsOpen = true }
                )
            }
            Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(3.dp))
        }
    }
}

@Composable
private fun HomeScreen(
    openTasksCount: Int, eventsCount: Int, notesCount: Int, shoppingCount: Int,
    onOpenTasks: () -> Unit, onOpenCalendar: () -> Unit, onOpenNotes: () -> Unit,
    onOpenKitchen: () -> Unit, onOpenChat: () -> Unit, onOpenRoom: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
        item {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Добро пожаловать домой", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Ink)
                    Text("Семейные планы, маленькие дела и важные моменты.", color = Ink)
                    Text("Твой семейный центр начинается здесь.", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item { Text("Сегодня у семьи", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("Задачи", openTasksCount.toString(), "Открыть список", Modifier.weight(1f), onOpenTasks)
                SummaryCard("События", eventsCount.toString(), "К календарю", Modifier.weight(1f), onOpenCalendar)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryCard("Покупки", shoppingCount.toString(), "К списку", Modifier.weight(1f), onOpenKitchen)
                SummaryCard("Заметки", notesCount.toString(), "Мои записи", Modifier.weight(1f), onOpenNotes)
            }
        }
        item { Text("Наш дом", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        item {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Коснись комнаты, чтобы войти", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    AnimatedHouseIllustration()
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoomTile("Кухня", "Покупки", "⌂", Modifier.weight(1f)) { onOpenRoom("Кухня") }
                        RoomTile("Гостиная", "Общение", "♡", Modifier.weight(1f)) { onOpenRoom("Гостиная") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoomTile("Детская", "Задачи", "☆", Modifier.weight(1f)) { onOpenRoom("Детская") }
                        RoomTile("Кабинет", "Заметки", "▤", Modifier.weight(1f)) { onOpenRoom("Кабинет") }
                    }
                    RoomTile("Прихожая", "Быстрые действия и устройства", "⌂", Modifier.fillMaxWidth()) { onOpenRoom("Прихожая") }
                }
            }
        }
        item { Text("Быстрый доступ", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        item { FeatureCard("Общие дела", "Планируй и отмечай выполненное", "✓", onOpenTasks) }
        item { FeatureCard("Календарь", "Храни важные даты и события", "▦", onOpenCalendar) }
        item { FeatureCard("Семейный чат", "Будет доступен после подключения синхронизации", "♡", onOpenChat) }
    }
}

@Composable
private fun AnimatedHouseIllustration() {
    val motion = rememberInfiniteTransition(label = "houseAmbient")
    val glow by motion.animateFloat(0.3f, 0.8f, infiniteRepeatable(tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "windowGlow")
    val floatY by motion.animateFloat(0f, -2f, infiniteRepeatable(tween(3200, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "houseFloat")
    Canvas(Modifier.fillMaxWidth().height(166.dp).offset(y = floatY.dp)) {
        val w = size.width; val h = size.height
        drawRoundRect(Color(0xFFDCD5EE), Offset(w*.08f,h*.76f), Size(w*.84f,h*.14f), androidx.compose.ui.geometry.CornerRadius(30f))
        val left=w*.23f; val right=w*.77f; val top=h*.30f; val bottom=h*.77f
        val wall=Path().apply { moveTo(left,top+h*.12f); lineTo(w*.50f,top-h*.06f); lineTo(right,top+h*.12f); lineTo(right,bottom); lineTo(left,bottom); close() }
        drawPath(wall,Color(0xFFFFF9EF))
        val roof=Path().apply { moveTo(left-w*.055f,top+h*.12f); lineTo(w*.50f,top-h*.17f); lineTo(right+w*.055f,top+h*.12f); close() }
        drawPath(roof,Color(0xFF8A78B8)); drawPath(roof,Color(0xFF6D5A9B),style=Stroke(width=2.dp.toPx()))
        drawLine(Color(0xFFE0D6C9),Offset(w*.50f,top+h*.02f),Offset(w*.50f,bottom),strokeWidth=2.dp.toPx())
        drawLine(Color(0xFFE0D6C9),Offset(left,h*.57f),Offset(right,h*.57f),strokeWidth=2.dp.toPx())
        listOf(Offset(w*.34f,h*.48f),Offset(w*.59f,h*.48f),Offset(w*.34f,h*.65f),Offset(w*.59f,h*.65f)).forEachIndexed { i,p ->
            val c=if(i%2==0) Color(0xFFE6C77C) else Color(0xFFC8DCCB)
            drawRoundRect(c.copy(alpha=if(i%2==0) glow else .9f),p,Size(w*.075f,h*.09f),androidx.compose.ui.geometry.CornerRadius(5f))
            drawRoundRect(Color(0xFFB9A9D7),p,Size(w*.075f,h*.09f),androidx.compose.ui.geometry.CornerRadius(5f),style=Stroke(width=1.2.dp.toPx()))
        }
        drawRoundRect(Color(0xFFB99B83),Offset(w*.455f,h*.63f),Size(w*.09f,h*.14f),androidx.compose.ui.geometry.CornerRadius(5f))
        drawCircle(Color(0xFFFFE7A8),2.dp.toPx(),Offset(w*.525f,h*.70f))
        val path=Path().apply { moveTo(w*.47f,h*.77f); lineTo(w*.40f,h*.91f); lineTo(w*.60f,h*.91f); lineTo(w*.53f,h*.77f); close() }
        drawPath(path,Color(0xFFF6EBDD))
        drawCircle(Color(0xFF9D8AC8).copy(alpha=glow),3.dp.toPx(),Offset(w*.16f,h*.58f))
        drawCircle(Color(0xFF9D8AC8).copy(alpha=.45f),2.dp.toPx(),Offset(w*.85f,h*.54f))
    }
}
@Composable
private fun RoomTile(title: String, subtitle: String, symbol: String, modifier: Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Box(Modifier.size(38.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) {
                Text(symbol, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleLarge)
            }
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6F687A))
        }
    }
}

@Composable
private fun RoomScreen(room: String, onBack: () -> Unit, onKitchen: () -> Unit, onTasks: () -> Unit, onCalendar: () -> Unit, onNotes: () -> Unit, onChat: () -> Unit) {
    val symbol = when (room) { "Кухня" -> "⌂"; "Гостиная" -> "♡"; "Детская" -> "☆"; "Кабинет" -> "▤"; else -> "⌂" }
    val intro = when (room) {
        "Кухня" -> "Всё для продуктов, покупок и приготовления еды."
        "Гостиная" -> "Место для семейного общения и общих воспоминаний."
        "Детская" -> "Здесь можно планировать дела и семейное расписание."
        "Кабинет" -> "Личные записи и документы — с приватностью по умолчанию."
        else -> "Быстрый доступ к семейным действиям и устройствам."
    }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(room, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack) { Text("← Домой") }
        }
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(62.dp).background(Color.White, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                    Text(symbol, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                }
                Text("Добро пожаловать", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(intro)
            }
        }
        when (room) {
            "Кухня" -> Button(onClick = onKitchen, modifier = Modifier.fillMaxWidth()) { Text("Открыть покупки и запасы") }
            "Гостиная" -> {
                Button(onClick = onChat, modifier = Modifier.fillMaxWidth()) { Text("Семейный чат") }
                OutlinedButton(onClick = onNotes, modifier = Modifier.fillMaxWidth()) { Text("Заметки и воспоминания") }
                Text("Чат подключится после настройки серверной синхронизации.", style = MaterialTheme.typography.bodySmall)
            }
            "Детская" -> {
                Button(onClick = onTasks, modifier = Modifier.fillMaxWidth()) { Text("Задачи и домашние дела") }
                OutlinedButton(onClick = onCalendar, modifier = Modifier.fillMaxWidth()) { Text("Расписание и события") }
            }
            "Кабинет" -> {
                Button(onClick = onNotes, modifier = Modifier.fillMaxWidth()) { Text("Открыть заметки") }
                Text("Документы и ИИ-помощник появятся в следующих этапах.", style = MaterialTheme.typography.bodySmall)
            }
            else -> {
                Text("Пока здесь доступны только локальные действия. Сопряжение устройств и сигналы помощи требуют защищённой реализации.")
                Button(onClick = onTasks, modifier = Modifier.fillMaxWidth()) { Text("Посмотреть семейные дела") }
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6F687A))
        }
    }
}

@Composable
private fun FeatureCard(title: String, description: String, symbol: String, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(42.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Text(symbol, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6F687A))
            }
            Text("›", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun TasksScreen(
    tasks: List<FamilyTask>, title: String, onTitle: (String) -> Unit,
    assignee: String, onAssignee: (String) -> Unit, dueDate: String, onDueDate: (String) -> Unit,
    priority: String, onPriority: (String) -> Unit,
    onAdd: () -> Unit, onToggle: (FamilyTask) -> Unit, onRemove: (FamilyTask) -> Unit
) {
    var filter by remember { mutableStateOf("Все") }
    val shown = when (filter) {
        "Активные" -> tasks.filter { !it.done }
        "Готовые" -> tasks.filter { it.done }
        else -> tasks
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Общие дела", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Задачи пока сохраняются на этом телефоне. Семейная синхронизация ещё не подключена.", style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(title, onTitle, Modifier.fillMaxWidth(), label = { Text("Что нужно сделать?") }, singleLine = true)
        OutlinedTextField(assignee, onAssignee, Modifier.fillMaxWidth(), label = { Text("Исполнитель (необязательно)") }, singleLine = true)
        OutlinedTextField(dueDate, onDueDate, Modifier.fillMaxWidth(), label = { Text("Срок, например 20.10.2026") }, singleLine = true)
        Text("Приоритет", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Низкий", "Обычный", "Высокий").forEach { value ->
                if (priority == value) Button(onClick = { onPriority(value) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) { Text(value) }
                else OutlinedButton(onClick = { onPriority(value) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) { Text(value) }
            }
        }
        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank()) { Text("Добавить задачу") }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Все", "Активные", "Готовые").forEach { value ->
                if (filter == value) Button(onClick = { filter = value }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) { Text(value) }
                else OutlinedButton(onClick = { filter = value }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) { Text(value) }
            }
        }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(shown, key = { it.id }) { task ->
                Card(colors = CardDefaults.cardColors(containerColor = if (task.done) Sage else Color.White)) {
                    Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(task.done, { onToggle(task) })
                            Text(task.title, Modifier.weight(1f), fontWeight = FontWeight.Medium)
                            TextButton(onClick = { onRemove(task) }) { Text("Удалить") }
                        }
                        val details = listOfNotNull(
                            task.assignee.takeIf { it.isNotBlank() }?.let { "Кому: $it" },
                            task.dueDate.takeIf { it.isNotBlank() }?.let { "Срок: $it" },
                            "Приоритет: ${task.priority}"
                        )
                        Text(details.joinToString("  ·  "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 12.dp, bottom = 4.dp))
                    }
                }
            }
            if (shown.isEmpty()) item {
                EmptyState(if (filter == "Все") "Здесь появятся семейные задачи." else "В этом фильтре пока пусто.", "Добавь дело выше или выбери другой фильтр.")
            }
        }
    }
}

@Composable
private fun CalendarScreen(events: List<FamilyEvent>, title: String, onTitle: (String) -> Unit, whenText: String, onWhen: (String) -> Unit, onAdd: () -> Unit, onRemove: (FamilyEvent) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text("Семейный календарь", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Локальные события, пока без системных напоминаний.")
        OutlinedTextField(title, onTitle, Modifier.fillMaxWidth(), label = { Text("Название события") }, singleLine = true)
        OutlinedTextField(whenText, onWhen, Modifier.fillMaxWidth(), label = { Text("Дата или время") }, singleLine = true)
        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank()) { Text("Сохранить событие") }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(events, key = { it.id }) { event ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(event.title, fontWeight = FontWeight.SemiBold)
                            Text(event.whenText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        TextButton(onClick = { onRemove(event) }) { Text("Удалить") }
                    }
                }
            }
            if (events.isEmpty()) item { EmptyState("Пока нет событий.", "Добавь день рождения, встречу или семейный план.") }
        }
    }
}

@Composable
private fun KitchenScreen(
    shopping: List<ShoppingItem>, pantry: List<PantryItem>, shoppingTitle: String, onShoppingTitle: (String) -> Unit,
    pantryTitle: String, onPantryTitle: (String) -> Unit, pantryExpiry: String, onPantryExpiry: (String) -> Unit,
    onAddShopping: () -> Unit, onToggleShopping: (ShoppingItem) -> Unit, onRemoveShopping: (ShoppingItem) -> Unit,
    onAddPantry: () -> Unit, onRemovePantry: (PantryItem) -> Unit, onBack: () -> Unit
) {
    var tab by remember { mutableStateOf("Покупки") }
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Кухня", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack) { Text("← Домой") }
        }
        Text("Список покупок, домашние запасы и рецепты.")
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("Покупки", "Запасы", "Рецепты").forEach { name ->
                if (tab == name) Button(onClick = { tab = name }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)) { Text(name) }
                else OutlinedButton(onClick = { tab = name }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)) { Text(name) }
            }
        }
        when (tab) {
            "Покупки" -> {
                OutlinedTextField(shoppingTitle, onShoppingTitle, Modifier.fillMaxWidth(), label = { Text("Добавить продукт") }, singleLine = true)
                Button(onClick = onAddShopping, modifier = Modifier.fillMaxWidth(), enabled = shoppingTitle.isNotBlank()) { Text("Добавить в список") }
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(shopping, key = { it.id }) { item ->
                        Card(colors = CardDefaults.cardColors(containerColor = if (item.bought) Sage else Color.White)) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(item.bought, { onToggleShopping(item) })
                                Text(item.title, Modifier.weight(1f))
                                TextButton(onClick = { onRemoveShopping(item) }) { Text("Убрать") }
                            }
                        }
                    }
                    if (shopping.isEmpty()) item { EmptyState("Список пока пуст.", "Добавь продукты, которые нужно купить.") }
                }
            }
            "Запасы" -> {
                OutlinedTextField(pantryTitle, onPantryTitle, Modifier.fillMaxWidth(), label = { Text("Продукт дома") }, singleLine = true)
                OutlinedTextField(pantryExpiry, onPantryExpiry, Modifier.fillMaxWidth(), label = { Text("Срок годности (необязательно)") }, singleLine = true)
                Button(onClick = onAddPantry, modifier = Modifier.fillMaxWidth(), enabled = pantryTitle.isNotBlank()) { Text("Добавить в запасы") }
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(pantry, key = { it.id }) { item ->
                        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                            Row(Modifier.fillMaxWidth().padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.title, fontWeight = FontWeight.SemiBold)
                                    Text("Срок: ${item.expiry}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                TextButton(onClick = { onRemovePantry(item) }) { Text("Убрать") }
                            }
                        }
                    }
                    if (pantry.isEmpty()) item { EmptyState("Запасы ещё не заполнены.", "Добавь продукты и при желании укажи срок годности.") }
                }
            }
            else -> LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { RecipeCard("Овсяная каша с яблоком", "Овсяные хлопья, молоко или вода, яблоко. Свари хлопья и добавь нарезанное яблоко.") }
                item { RecipeCard("Овощной омлет", "Яйца, помидор, сладкий перец. Нарежь овощи, добавь яйца и готовь до полной готовности.") }
                item { RecipeCard("Тёплый бутерброд с сыром", "Хлеб, сыр, по желанию помидор. Прогрей до расплавления сыра.") }
                item { Text("Автоматическое меню и расчёт ингредиентов пока не подключены.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}

@Composable
private fun RecipeCard(title: String, description: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ChatScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Семейный чат", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Чат пока не подключён", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Для настоящей переписки нужны семейные аккаунты, защищённая синхронизация и доставка сообщений. Здесь нет фиктивных участников или сообщений.")
            }
        }
    }
}

@Composable
private fun ProfileScreen(
    name: String, onName: (String) -> Unit, birthday: String, onBirthday: (String) -> Unit,
    role: String, onRole: (String) -> Unit, onOpenSettings: () -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
        item { Text("Мой профиль", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(22.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.size(64.dp).background(Color.White, RoundedCornerShape(22.dp)), contentAlignment = Alignment.Center) {
                        Text(name.trim().firstOrNull()?.uppercase() ?: "?", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(name.ifBlank { "Имя не указано" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Локальный профиль", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        item { OutlinedTextField(name, onName, Modifier.fillMaxWidth(), label = { Text("Имя") }, singleLine = true) }
        item { OutlinedTextField(birthday, onBirthday, Modifier.fillMaxWidth(), label = { Text("Дата рождения") }, placeholder = { Text("Например, 12.05.1995") }, singleLine = true) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Роль в семье", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Родитель", "Ребёнок", "Другой").forEach { value ->
                        if (role == value) Button(onClick = { onRole(value) }, contentPadding = PaddingValues(horizontal = 9.dp, vertical = 7.dp)) { Text(value) }
                        else OutlinedButton(onClick = { onRole(value) }, contentPadding = PaddingValues(horizontal = 9.dp, vertical = 7.dp)) { Text(value) }
                    }
                }
            }
        }
        item {
            Surface(color = Color.White, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text("Аккаунт не подключён", fontWeight = FontWeight.SemiBold)
                    Text("Профиль хранится только на этом устройстве. Семейная синхронизация пока не работает.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item { OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) { Text("Настройки приложения") } }
    }
}

@Composable
private fun SettingsScreen(themeName: String, onTheme: (String) -> Unit, onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Настройки", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack) { Text("← Назад") }
        }
        Text("Тема оформления")
        listOf("Лаванда", "Шалфей", "Океан").forEach { name ->
            if (themeName == name) Button(onClick = { onTheme(name) }, modifier = Modifier.fillMaxWidth()) { Text(name) }
            else OutlinedButton(onClick = { onTheme(name) }, modifier = Modifier.fillMaxWidth()) { Text(name) }
        }
        Text("Выбор темы сохраняется на этом устройстве.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun NotesScreen(
    notes: List<Note>, title: String, onTitle: (String) -> Unit, body: String, onBody: (String) -> Unit,
    search: String, onSearch: (String) -> Unit, onAddNote: () -> Unit, onDeleteNote: (Note) -> Unit, onBack: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Мои заметки", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack) { Text("← Назад") }
        }
        OutlinedTextField(title, onTitle, Modifier.fillMaxWidth(), label = { Text("Заголовок") }, singleLine = true)
        OutlinedTextField(body, onBody, Modifier.fillMaxWidth(), label = { Text("Текст заметки") }, minLines = 2)
        Button(onClick = onAddNote, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank() || body.isNotBlank()) { Text("Сохранить заметку") }
        OutlinedTextField(search, onSearch, Modifier.fillMaxWidth(), label = { Text("Поиск по заметкам") }, singleLine = true)
        Text("Заметки · ${notes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes, key = { it.id }) { note ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        if (note.body.isNotBlank()) Text(note.body)
                        Text(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(note.updatedAt)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        TextButton(onClick = { onDeleteNote(note) }) { Text("Удалить") }
                    }
                }
            }
            if (notes.isEmpty()) item { EmptyState("Заметок пока нет.", "Создай первую запись выше.") }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(18.dp)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private object LocalStore {
    private const val PREFS = "familycore_local_store"
    private fun readArray(context: Context, key: String): JSONArray = try {
        JSONArray(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(key, "[]") ?: "[]")
    } catch (_: Exception) { JSONArray() }
    private fun saveArray(context: Context, key: String, array: JSONArray) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(key, array.toString()).apply()
    }
    private fun JSONObject.str(key: String): String = optString(key, "")
    fun loadNotes(c: Context): List<Note> {
        val array = readArray(c, "notes")
        return (0 until array.length()).map { i -> array.getJSONObject(i).let { Note(it.str("id"), it.str("title"), it.str("body"), it.optLong("updatedAt")) } }
    }
    fun saveNotes(c: Context, list: List<Note>) = save(c, "notes", list.map { JSONObject().put("id", it.id).put("title", it.title).put("body", it.body).put("updatedAt", it.updatedAt) })
    fun loadTasks(c: Context): List<FamilyTask> = decode(c, "tasks") { FamilyTask(it.str("id"), it.str("title"), it.optBoolean("done"), it.optString("assignee", ""), it.optString("dueDate", ""), it.optString("priority", "Обычный")) }
    fun saveTasks(c: Context, list: List<FamilyTask>) = save(c, "tasks", list.map { JSONObject().put("id", it.id).put("title", it.title).put("done", it.done).put("assignee", it.assignee).put("dueDate", it.dueDate).put("priority", it.priority) })
    fun loadEvents(c: Context): List<FamilyEvent> = decode(c, "events") { FamilyEvent(it.str("id"), it.str("title"), it.str("whenText")) }
    fun saveEvents(c: Context, list: List<FamilyEvent>) = save(c, "events", list.map { JSONObject().put("id", it.id).put("title", it.title).put("whenText", it.whenText) })
    fun loadShopping(c: Context): List<ShoppingItem> = decode(c, "shopping") { ShoppingItem(it.str("id"), it.str("title"), it.optBoolean("bought")) }
    fun saveShopping(c: Context, list: List<ShoppingItem>) = save(c, "shopping", list.map { JSONObject().put("id", it.id).put("title", it.title).put("bought", it.bought) })
    fun loadPantry(c: Context): List<PantryItem> = decode(c, "pantry") { PantryItem(it.str("id"), it.str("title"), it.str("expiry")) }
    fun savePantry(c: Context, list: List<PantryItem>) = save(c, "pantry", list.map { JSONObject().put("id", it.id).put("title", it.title).put("expiry", it.expiry) })
    fun loadProfileName(c: Context): String = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("profile_name", "") ?: ""
    fun saveProfileName(c: Context, value: String) { c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("profile_name", value).apply() }
    fun loadProfileBirthday(c: Context): String = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("profile_birthday", "") ?: ""
    fun saveProfileBirthday(c: Context, value: String) { c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("profile_birthday", value).apply() }
    fun loadProfileRole(c: Context): String = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("profile_role", "Родитель") ?: "Родитель"
    fun saveProfileRole(c: Context, value: String) { c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("profile_role", value).apply() }
    fun loadTheme(c: Context): String = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("theme", "Лаванда") ?: "Лаванда"
    fun saveTheme(c: Context, name: String) { c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("theme", name).apply() }
    private fun <T> decode(c: Context, key: String, convert: (JSONObject) -> T): List<T> {
        val array = readArray(c, key)
        return try { (0 until array.length()).map { convert(array.getJSONObject(it)) } } catch (_: Exception) { emptyList() }
    }
    private fun save(c: Context, key: String, values: List<JSONObject>) {
        val array = JSONArray(); values.forEach { array.put(it) }; saveArray(c, key, array)
    }
}
