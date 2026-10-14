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
import androidx.compose.runtime.staticCompositionLocalOf
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
private val LocalAppLanguage = staticCompositionLocalOf { "Русский" }

@Composable
private fun tr(value: String): String = translations[LocalAppLanguage.current]?.get(value) ?: value

private val translations: Map<String, Map<String, String>> = mapOf(
    "English" to mapOf(
        "Дом" to "Home", "Задачи" to "Tasks", "Календарь" to "Calendar", "Чат" to "Chat", "Профиль" to "Profile",
        "Теплее вместе" to "Closer together", "Добро пожаловать домой" to "Welcome home", "Место, где собирается ваша семья" to "A place for your family",
        "Наш дом" to "Our home", "Выбери комнату, чтобы войти" to "Choose a room to enter", "Кухня" to "Kitchen", "Гостиная" to "Living room",
        "Детская" to "Kids’ room", "Кабинет" to "Study", "Прихожая" to "Entryway", "Покупки · запасы" to "Shopping · pantry",
        "Общение · память" to "Chat · memories", "Задачи · распорядок" to "Tasks · routine", "Заметки · идеи" to "Notes · ideas",
        "Быстрые действия и устройства" to "Quick actions and devices", "Домашние дела" to "Household", "Все разделы внизу" to "Sections below",
        "Семейный чат" to "Family chat", "Откроется после подключения защищённой синхронизации" to "Available after secure sync is connected",
        "Мой профиль" to "My profile", "Имя не указано" to "Name not set", "Локальный профиль" to "Local profile", "Имя" to "Name",
        "Дата рождения" to "Date of birth", "Например, 12.05.1995" to "For example, 12/05/1995", "Роль в семье" to "Family role",
        "Родитель" to "Parent", "Ребёнок" to "Child", "Другой" to "Other", "Аккаунт не подключён" to "Account not connected",
        "Профиль хранится только на этом устройстве. Семейная синхронизация пока не работает." to "Your profile is stored on this device. Family sync is not available yet.",
        "Настройки приложения" to "App settings", "Настройки" to "Settings", "← Назад" to "← Back", "Тема оформления" to "Appearance theme",
        "Лаванда" to "Lavender", "Шалфей" to "Sage", "Океан" to "Ocean", "Выбор темы сохраняется на этом устройстве." to "Your theme choice is saved on this device.",
        "Язык приложения" to "App language", "Русский" to "Russian", "English" to "English", "中文（简体）" to "Chinese (Simplified)",
        "Español" to "Spanish", "हिन्दी" to "Hindi", "Obsidian Gold" to "Obsidian Gold", "Тема сохраняется только на этом устройстве." to "Theme is saved on this device.",
        "Выбор языка сохраняется на этом устройстве." to "Language is saved on this device.", "Задача сохранена" to "Task saved", "Событие сохранено" to "Event saved",
        "Заметка сохранена" to "Note saved", "Заметка удалена" to "Note deleted", "Покупка сохранена" to "Shopping item saved", "Продукт добавлен в запасы" to "Item added to pantry",
        "Данные сохраняются на этом устройстве · синхронизации пока нет" to "Data is stored on this device · sync is not available yet",
        "Список покупок сохранён" to "Shopping list saved", "Покупка удалена" to "Shopping item removed", "Статус задачи сохранён" to "Task status saved"
    ),
    "中文（简体）" to mapOf(
        "Дом" to "首页", "Задачи" to "任务", "Календарь" to "日历", "Чат" to "聊天", "Профиль" to "个人资料",
        "Теплее вместе" to "让家人更亲近", "Добро пожаловать домой" to "欢迎回家", "Место, где собирается ваша семья" to "家人相聚的地方",
        "Наш дом" to "我们的家", "Выбери комнату, чтобы войти" to "选择一个房间进入", "Кухня" to "厨房", "Гостиная" to "客厅",
        "Детская" to "儿童房", "Кабинет" to "书房", "Прихожая" to "门厅", "Покупки · запасы" to "购物 · 储物",
        "Общение · память" to "交流 · 回忆", "Задачи · распорядок" to "任务 · 日程", "Заметки · идеи" to "笔记 · 想法",
        "Быстрые действия и устройства" to "快捷操作与设备", "Домашние дела" to "家庭事务", "Все разделы внизу" to "更多栏目在下方",
        "Семейный чат" to "家庭聊天", "Мой профиль" to "我的资料", "Имя не указано" to "未填写姓名", "Локальный профиль" to "本地资料",
        "Имя" to "姓名", "Дата рождения" to "出生日期", "Роль в семье" to "家庭角色", "Родитель" to "家长", "Ребёнок" to "孩子", "Другой" to "其他",
        "Аккаунт не подключён" to "账号尚未连接", "Настройки приложения" to "应用设置", "Настройки" to "设置", "← Назад" to "← 返回",
        "Тема оформления" to "外观主题", "Лаванда" to "薰衣草", "Шалфей" to "鼠尾草", "Океан" to "海洋", "Язык приложения" to "应用语言",
        "Русский" to "俄语", "English" to "英语", "中文（简体）" to "简体中文", "Español" to "西班牙语", "हिन्दी" to "印地语",
        "Выбор темы сохраняется на этом устройстве." to "主题选择保存在本设备上。", "Выбор языка сохраняется на этом устройстве." to "语言选择保存在本设备上。"
    ),
    "Español" to mapOf(
        "Дом" to "Inicio", "Задачи" to "Tareas", "Календарь" to "Calendario", "Чат" to "Chat", "Профиль" to "Perfil",
        "Теплее вместе" to "Más unidos", "Добро пожаловать домой" to "Bienvenido a casa", "Место, где собирается ваша семья" to "Un lugar para toda la familia",
        "Наш дом" to "Nuestro hogar", "Выбери комнату, чтобы войти" to "Elige una habitación", "Кухня" to "Cocina", "Гостиная" to "Sala de estar",
        "Детская" to "Habitación infantil", "Кабинет" to "Despacho", "Прихожая" to "Entrada", "Покупки · запасы" to "Compras · despensa",
        "Общение · память" to "Conversación · recuerdos", "Задачи · распорядок" to "Tareas · rutina", "Заметки · идеи" to "Notas · ideas",
        "Быстрые действия и устройства" to "Acciones rápidas y dispositivos", "Домашние дела" to "Tareas del hogar", "Все разделы внизу" to "Más secciones abajo",
        "Семейный чат" to "Chat familiar", "Мой профиль" to "Mi perfil", "Имя не указано" to "Nombre sin indicar", "Локальный профиль" to "Perfil local",
        "Имя" to "Nombre", "Дата рождения" to "Fecha de nacimiento", "Роль в семье" to "Rol familiar", "Родитель" to "Madre/padre", "Ребёнок" to "Hijo/a", "Другой" to "Otro",
        "Аккаунт не подключён" to "Cuenta no conectada", "Настройки приложения" to "Ajustes de la aplicación", "Настройки" to "Ajustes", "← Назад" to "← Volver",
        "Тема оформления" to "Tema visual", "Лаванда" to "Lavanda", "Шалфей" to "Salvia", "Океан" to "Océano", "Язык приложения" to "Idioma de la aplicación",
        "Русский" to "Ruso", "English" to "Inglés", "中文（简体）" to "Chino simplificado", "Español" to "Español", "हिन्दी" to "Hindi",
        "Выбор темы сохраняется на этом устройстве." to "El tema se guarda en este dispositivo.", "Выбор языка сохраняется на этом устройстве." to "El idioma se guarda en este dispositivo."
    ),
    "हिन्दी" to mapOf(
        "Дом" to "होम", "Задачи" to "कार्य", "Календарь" to "कैलेंडर", "Чат" to "चैट", "Профиль" to "प्रोफ़ाइल",
        "Теплее вместе" to "और करीब", "Добро пожаловать домой" to "घर में आपका स्वागत है", "Место, где собирается ваша семья" to "जहाँ आपका परिवार साथ आता है",
        "Наш дом" to "हमारा घर", "Выбери комнату, чтобы войти" to "प्रवेश के लिए कमरा चुनें", "Кухня" to "रसोई", "Гостиная" to "बैठक कक्ष",
        "Детская" to "बच्चों का कमरा", "Кабинет" to "अध्ययन कक्ष", "Прихожая" to "प्रवेश कक्ष", "Покупки · запасы" to "खरीदारी · भंडार",
        "Общение · память" to "बातचीत · यादें", "Задачи · распорядок" to "कार्य · दिनचर्या", "Заметки · идеи" to "नोट्स · विचार",
        "Быстрые действия и устройства" to "त्वरित कार्य और उपकरण", "Домашние дела" to "घर के काम", "Все разделы внизу" to "अन्य अनुभाग नीचे",
        "Семейный чат" to "परिवार चैट", "Мой профиль" to "मेरी प्रोफ़ाइल", "Имя не указано" to "नाम नहीं दिया गया", "Локальный профиль" to "स्थानीय प्रोफ़ाइल",
        "Имя" to "नाम", "Дата рождения" to "जन्म तिथि", "Роль в семье" to "परिवार में भूमिका", "Родитель" to "माता-पिता", "Ребёнок" to "बच्चा", "Другой" to "अन्य",
        "Аккаунт не подключён" to "खाता कनेक्ट नहीं है", "Настройки приложения" to "ऐप सेटिंग्स", "Настройки" to "सेटिंग्स", "← Назад" to "← वापस",
        "Тема оформления" to "थीम", "Лаванда" to "लैवेंडर", "Шалфей" to "सेज", "Океан" to "समुद्र", "Язык приложения" to "ऐप की भाषा",
        "Русский" to "रूसी", "English" to "अंग्रेज़ी", "中文（简体）" to "सरलीकृत चीनी", "Español" to "स्पेनिश", "हिन्दी" to "हिन्दी",
        "Выбор темы сохраняется на этом устройстве." to "थीम इस डिवाइस पर सहेजी जाती है।", "Выбор языка сохраняется на этом устройстве." to "भाषा इस डिवाइस पर सहेजी जाती है।"
    )
)

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
            var languageName by remember { mutableStateOf(LocalStore.loadLanguage(context)) }
            val primary = when (themeName) {
                "Obsidian Gold" -> Color(0xFFD4AF37)
                "Шалфей" -> Color(0xFF557B65)
                "Океан" -> Color(0xFF397C91)
                else -> Color(0xFF7967B2)
            }
            val goldTheme = themeName == "Obsidian Gold"
            val tint = when (themeName) {
                "Obsidian Gold" -> Color(0xFF242424)
                "Шалфей" -> Color(0xFFE8F1E8)
                "Океан" -> Color(0xFFE7F3F7)
                else -> Color(0xFFF0ECFA)
            }
            CompositionLocalProvider(LocalAppLanguage provides languageName) {
                MaterialTheme(colorScheme = if (goldTheme) darkColorScheme(
                    primary = Color(0xFFD4AF37), onPrimary = Color(0xFF171717), background = Color(0xFF101010),
                    surface = Color(0xFF1B1B1B), onSurface = Color(0xFFF3E7C2), surfaceVariant = tint,
                    secondaryContainer = Color(0xFF302816), onBackground = Color(0xFFF3E7C2)
                ) else lightColorScheme(
                    primary = primary, onPrimary = Color.White, background = Cream,
                    surface = Color.White, onSurface = Ink, surfaceVariant = tint,
                    secondaryContainer = Sage
                )) {
                    Surface(Modifier.fillMaxSize(), color = if (goldTheme) Color(0xFF101010) else Cream) {
                        FamilyCoreApp(themeName, onThemeChanged = { selected ->
                            themeName = selected
                            LocalStore.saveTheme(context, selected)
                        }, languageName = languageName, onLanguageChanged = { selected ->
                            languageName = selected
                            LocalStore.saveLanguage(context, selected)
                        })
                    }
                }
            }
        }
    }
}

@Composable
private fun FamilyCoreApp(themeName: String, onThemeChanged: (String) -> Unit, languageName: String, onLanguageChanged: (String) -> Unit) {
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
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                Screen.entries.forEach { item ->
                    NavigationBarItem(
                        selected = screen == item && !kitchenOpen,
                        onClick = { screen = item; kitchenOpen = false; roomOpen = null; notesOpen = false; settingsOpen = false },
                        icon = { Text(item.symbol, style = MaterialTheme.typography.titleMedium) },
                        label = { Text(tr(item.label), maxLines = 1) }
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
                    Text(tr("F"), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                }
                Column(Modifier.weight(1f)) {
                    Text(tr("FamilyCore"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(tr("Теплее вместе"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                }
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(50)) {
                    Text(tr("v${BuildConfig.VERSION_NAME}"), Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
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
                settingsOpen -> SettingsScreen(themeName, onThemeChanged, languageName, onLanguageChanged, onBack = { settingsOpen = false })
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
    LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 16.dp)) {
        item {
            Column(Modifier.fillMaxWidth().padding(top = 4.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(tr("Добро пожаловать домой"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(tr("Место, где собирается ваша семья"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
        item {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(28.dp)) {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(tr("Наш дом"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(tr("Выбери комнату, чтобы войти"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(tr("✦"), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    AnimatedHouseIllustration()
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        RoomTile("Кухня", "Покупки · запасы", "⌂", Modifier.weight(1f)) { onOpenRoom("Кухня") }
                        RoomTile("Гостиная", "Общение · память", "♡", Modifier.weight(1f)) { onOpenRoom("Гостиная") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                        RoomTile("Детская", "Задачи · распорядок", "☆", Modifier.weight(1f)) { onOpenRoom("Детская") }
                        RoomTile("Кабинет", "Заметки · идеи", "▤", Modifier.weight(1f)) { onOpenRoom("Кабинет") }
                    }
                    RoomTile("Прихожая", "Быстрые действия и устройства", "⌂", Modifier.fillMaxWidth()) { onOpenRoom("Прихожая") }
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(tr("Домашние дела"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(tr("Все разделы внизу"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                SummaryCard("Задачи", openTasksCount.toString(), "Открыть", Modifier.weight(1f), onOpenTasks)
                SummaryCard("События", eventsCount.toString(), "К календарю", Modifier.weight(1f), onOpenCalendar)
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                SummaryCard("Покупки", shoppingCount.toString(), "К списку", Modifier.weight(1f), onOpenKitchen)
                SummaryCard("Заметки", notesCount.toString(), "Мои записи", Modifier.weight(1f), onOpenNotes)
            }
        }
        item { FeatureCard("Семейный чат", "Откроется после подключения защищённой синхронизации", "♡", onOpenChat) }
    }
}

@Composable
private fun AnimatedHouseIllustration() {
    val motion = rememberInfiniteTransition(label = "houseAmbient")
    val glow by motion.animateFloat(0.38f, 0.92f, infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "windowGlow")
    val floatY by motion.animateFloat(0f, -1.5f, infiniteRepeatable(tween(3600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "houseFloat")
    Canvas(Modifier.fillMaxWidth().height(218.dp).offset(y = floatY.dp)) {
        val w = size.width; val h = size.height
        // Soft garden and pathway
        drawOval(Color(0xFFD9D1EA), Offset(w*.07f,h*.77f), Size(w*.86f,h*.15f))
        drawOval(Color(0xFFE7E0F1), Offset(w*.12f,h*.73f), Size(w*.76f,h*.12f))
        val left=w*.19f; val right=w*.81f; val top=h*.28f; val bottom=h*.78f
        // Main walls, slight side plane for depth
        val side=Path().apply { moveTo(right,top+h*.10f); lineTo(right+w*.07f,top+h*.16f); lineTo(right+w*.07f,bottom-h*.02f); lineTo(right,bottom); close() }
        drawPath(side,Color(0xFFE8DCCB))
        drawRect(Color(0xFFFFFAF1),Offset(left,top+h*.10f),Size(right-left,bottom-(top+h*.10f)))
        // Roof with layered eaves and soft shadow
        val shadow=Path().apply { moveTo(left-w*.07f,top+h*.12f); lineTo(w*.50f,top-h*.20f); lineTo(right+w*.07f,top+h*.12f); lineTo(right+w*.04f,top+h*.16f); lineTo(w*.50f,top-h*.15f); lineTo(left-w*.04f,top+h*.16f); close() }
        drawPath(shadow,Color(0xFF6D5A9B).copy(alpha=.16f))
        val roof=Path().apply { moveTo(left-w*.075f,top+h*.12f); lineTo(w*.50f,top-h*.23f); lineTo(right+w*.075f,top+h*.12f); close() }
        drawPath(roof,Color(0xFF8D7ABD))
        drawPath(roof,Color(0xFF6D5A9B),style=Stroke(width=2.4.dp.toPx()))
        drawLine(Color(0xFFB8A6D8),Offset(w*.50f,top-h*.19f),Offset(w*.50f,top+h*.10f),strokeWidth=1.3.dp.toPx())
        // Interior dividers make the rooms legible at a glance
        drawLine(Color(0xFFE0D4C4),Offset(w*.50f,top+h*.10f),Offset(w*.50f,bottom),strokeWidth=3.dp.toPx())
        drawLine(Color(0xFFE0D4C4),Offset(left,h*.55f),Offset(right,h*.55f),strokeWidth=3.dp.toPx())
        // Four distinct room windows / room lights
        val windows=listOf(
            Triple(Offset(w*.29f,h*.43f),Color(0xFFF0D58E),true),
            Triple(Offset(w*.59f,h*.43f),Color(0xFFC9DDCF),false),
            Triple(Offset(w*.29f,h*.61f),Color(0xFFF0D58E),true),
            Triple(Offset(w*.59f,h*.61f),Color(0xFFC9DDCF),false)
        )
        windows.forEachIndexed { i, item ->
            val p=item.first; val base=item.second
            drawRoundRect(Color(0xFFB6A3D1),Offset(p.x-2.dp.toPx(),p.y-2.dp.toPx()),Size(w*.105f+4.dp.toPx(),h*.105f+4.dp.toPx()),androidx.compose.ui.geometry.CornerRadius(7f))
            drawRoundRect(base.copy(alpha=if(item.third) glow else .9f),p,Size(w*.105f,h*.105f),androidx.compose.ui.geometry.CornerRadius(5f))
            drawLine(Color(0xFFFFF9EF),Offset(p.x+w*.052f,p.y),Offset(p.x+w*.052f,p.y+h*.105f),strokeWidth=1.2.dp.toPx())
            drawLine(Color(0xFFFFF9EF),Offset(p.x,p.y+h*.052f),Offset(p.x+w*.105f,p.y+h*.052f),strokeWidth=1.2.dp.toPx())
        }
        // Welcoming door and path
        drawRoundRect(Color(0xFFB69A80),Offset(w*.445f,h*.63f),Size(w*.11f,h*.15f),androidx.compose.ui.geometry.CornerRadius(6f))
        drawCircle(Color(0xFFFFE7A8),2.5.dp.toPx(),Offset(w*.53f,h*.71f))
        val path=Path().apply { moveTo(w*.46f,h*.78f); lineTo(w*.37f,h*.94f); lineTo(w*.63f,h*.94f); lineTo(w*.54f,h*.78f); close() }
        drawPath(path,Color(0xFFF6EBDD))
        // Fireflies / ambient dots
        drawCircle(Color(0xFF9D8AC8).copy(alpha=glow),3.dp.toPx(),Offset(w*.12f,h*.55f))
        drawCircle(Color(0xFF9D8AC8).copy(alpha=.5f),2.dp.toPx(),Offset(w*.87f,h*.50f))
        drawCircle(Color(0xFFE5C87D).copy(alpha=glow),2.dp.toPx(),Offset(w*.79f,h*.68f))
    }
}

@Composable
private fun RoomTile(title: String, subtitle: String, symbol: String, modifier: Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
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
            TextButton(onClick = onBack) { Text(tr("← Домой")) }
        }
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(62.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                    Text(symbol, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                }
                Text(tr("Добро пожаловать"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(intro)
            }
        }
        when (room) {
            "Кухня" -> Button(onClick = onKitchen, modifier = Modifier.fillMaxWidth()) { Text(tr("Открыть покупки и запасы")) }
            "Гостиная" -> {
                Button(onClick = onChat, modifier = Modifier.fillMaxWidth()) { Text(tr("Семейный чат")) }
                OutlinedButton(onClick = onNotes, modifier = Modifier.fillMaxWidth()) { Text(tr("Заметки и воспоминания")) }
                Text(tr("Чат подключится после настройки серверной синхронизации."), style = MaterialTheme.typography.bodySmall)
            }
            "Детская" -> {
                Button(onClick = onTasks, modifier = Modifier.fillMaxWidth()) { Text(tr("Задачи и домашние дела")) }
                OutlinedButton(onClick = onCalendar, modifier = Modifier.fillMaxWidth()) { Text(tr("Расписание и события")) }
            }
            "Кабинет" -> {
                Button(onClick = onNotes, modifier = Modifier.fillMaxWidth()) { Text(tr("Открыть заметки")) }
                Text(tr("Документы и ИИ-помощник появятся в следующих этапах."), style = MaterialTheme.typography.bodySmall)
            }
            else -> {
                Text(tr("Пока здесь доступны только локальные действия. Сопряжение устройств и сигналы помощи требуют защищённой реализации."))
                Button(onClick = onTasks, modifier = Modifier.fillMaxWidth()) { Text(tr("Посмотреть семейные дела")) }
            }
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, subtitle: String, modifier: Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(tr(title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6F687A))
        }
    }
}

@Composable
private fun FeatureCard(title: String, description: String, symbol: String, onClick: () -> Unit) {
    Card(onClick = onClick, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(42.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Text(symbol, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6F687A))
            }
            Text(tr("›"), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
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
        Text(tr("Общие дела"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(tr("Задачи пока сохраняются на этом телефоне. Семейная синхронизация ещё не подключена."), style = MaterialTheme.typography.bodySmall)
        OutlinedTextField(title, onTitle, Modifier.fillMaxWidth(), label = { Text(tr("Что нужно сделать?")) }, singleLine = true)
        OutlinedTextField(assignee, onAssignee, Modifier.fillMaxWidth(), label = { Text(tr("Исполнитель (необязательно)")) }, singleLine = true)
        OutlinedTextField(dueDate, onDueDate, Modifier.fillMaxWidth(), label = { Text(tr("Срок, например 20.10.2026")) }, singleLine = true)
        Text(tr("Приоритет"), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Низкий", "Обычный", "Высокий").forEach { value ->
                if (priority == value) Button(onClick = { onPriority(value) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) { Text(value) }
                else OutlinedButton(onClick = { onPriority(value) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) { Text(value) }
            }
        }
        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank()) { Text(tr("Добавить задачу")) }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Все", "Активные", "Готовые").forEach { value ->
                if (filter == value) Button(onClick = { filter = value }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) { Text(value) }
                else OutlinedButton(onClick = { filter = value }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) { Text(value) }
            }
        }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(shown, key = { it.id }) { task ->
                Card(colors = CardDefaults.cardColors(containerColor = if (task.done) Sage else MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(task.done, { onToggle(task) })
                            Text(task.title, Modifier.weight(1f), fontWeight = FontWeight.Medium)
                            TextButton(onClick = { onRemove(task) }) { Text(tr("Удалить")) }
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
        Text(tr("Семейный календарь"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(tr("Локальные события, пока без системных напоминаний."))
        OutlinedTextField(title, onTitle, Modifier.fillMaxWidth(), label = { Text(tr("Название события")) }, singleLine = true)
        OutlinedTextField(whenText, onWhen, Modifier.fillMaxWidth(), label = { Text(tr("Дата или время")) }, singleLine = true)
        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank()) { Text(tr("Сохранить событие")) }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(events, key = { it.id }) { event ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(event.title, fontWeight = FontWeight.SemiBold)
                            Text(event.whenText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                        TextButton(onClick = { onRemove(event) }) { Text(tr("Удалить")) }
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
            Text(tr("Кухня"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack) { Text(tr("← Домой")) }
        }
        Text(tr("Список покупок, домашние запасы и рецепты."))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            listOf("Покупки", "Запасы", "Рецепты").forEach { name ->
                if (tab == name) Button(onClick = { tab = name }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)) { Text(name) }
                else OutlinedButton(onClick = { tab = name }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)) { Text(name) }
            }
        }
        when (tab) {
            "Покупки" -> {
                OutlinedTextField(shoppingTitle, onShoppingTitle, Modifier.fillMaxWidth(), label = { Text(tr("Добавить продукт")) }, singleLine = true)
                Button(onClick = onAddShopping, modifier = Modifier.fillMaxWidth(), enabled = shoppingTitle.isNotBlank()) { Text(tr("Добавить в список")) }
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(shopping, key = { it.id }) { item ->
                        Card(colors = CardDefaults.cardColors(containerColor = if (item.bought) Sage else MaterialTheme.colorScheme.surface)) {
                            Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(item.bought, { onToggleShopping(item) })
                                Text(item.title, Modifier.weight(1f))
                                TextButton(onClick = { onRemoveShopping(item) }) { Text(tr("Убрать")) }
                            }
                        }
                    }
                    if (shopping.isEmpty()) item { EmptyState("Список пока пуст.", "Добавь продукты, которые нужно купить.") }
                }
            }
            "Запасы" -> {
                OutlinedTextField(pantryTitle, onPantryTitle, Modifier.fillMaxWidth(), label = { Text(tr("Продукт дома")) }, singleLine = true)
                OutlinedTextField(pantryExpiry, onPantryExpiry, Modifier.fillMaxWidth(), label = { Text(tr("Срок годности (необязательно)")) }, singleLine = true)
                Button(onClick = onAddPantry, modifier = Modifier.fillMaxWidth(), enabled = pantryTitle.isNotBlank()) { Text(tr("Добавить в запасы")) }
                LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(pantry, key = { it.id }) { item ->
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                            Row(Modifier.fillMaxWidth().padding(11.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.title, fontWeight = FontWeight.SemiBold)
                                    Text(tr("Срок: ${item.expiry}"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                                TextButton(onClick = { onRemovePantry(item) }) { Text(tr("Убрать")) }
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
                item { Text(tr("Автоматическое меню и расчёт ингредиентов пока не подключены."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
            }
        }
    }
}

@Composable
private fun RecipeCard(title: String, description: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(13.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ChatScreen() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(tr("Семейный чат"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(20.dp)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(tr("Чат пока не подключён"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(tr("Для настоящей переписки нужны семейные аккаунты, защищённая синхронизация и доставка сообщений. Здесь нет фиктивных участников или сообщений."))
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
        item { Text(tr("Мой профиль"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }
        item {
            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(22.dp)) {
                Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.size(64.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(22.dp)), contentAlignment = Alignment.Center) {
                        Text(name.trim().firstOrNull()?.uppercase() ?: "?", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Column {
                        Text(name.ifBlank { "Имя не указано" }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(tr("Локальный профиль"), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        item { OutlinedTextField(name, onName, Modifier.fillMaxWidth(), label = { Text(tr("Имя")) }, singleLine = true) }
        item { OutlinedTextField(birthday, onBirthday, Modifier.fillMaxWidth(), label = { Text(tr("Дата рождения")) }, placeholder = { Text(tr("Например, 12.05.1995")) }, singleLine = true) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(tr("Роль в семье"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Родитель", "Ребёнок", "Другой").forEach { value ->
                        if (role == value) Button(onClick = { onRole(value) }, contentPadding = PaddingValues(horizontal = 9.dp, vertical = 7.dp)) { Text(value) }
                        else OutlinedButton(onClick = { onRole(value) }, contentPadding = PaddingValues(horizontal = 9.dp, vertical = 7.dp)) { Text(value) }
                    }
                }
            }
        }
        item {
            Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.fillMaxWidth().padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(tr("Аккаунт не подключён"), fontWeight = FontWeight.SemiBold)
                    Text(tr("Профиль хранится только на этом устройстве. Семейная синхронизация пока не работает."), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item { OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) { Text(tr("Настройки приложения")) } }
    }
}

@Composable
private fun SettingsScreen(themeName: String, onTheme: (String) -> Unit, languageName: String, onLanguage: (String) -> Unit, onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Настройки"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack) { Text(tr("← Назад")) }
        }
        Text(tr("Тема оформления"))
        listOf("Лаванда", "Шалфей", "Океан", "Obsidian Gold").forEach { name ->
            if (themeName == name) Button(onClick = { onTheme(name) }, modifier = Modifier.fillMaxWidth()) { Text(name) }
            else OutlinedButton(onClick = { onTheme(name) }, modifier = Modifier.fillMaxWidth()) { Text(name) }
        }
        Text(tr("Выбор темы сохраняется на этом устройстве."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        HorizontalDivider()
        Text(tr("Язык приложения"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        listOf("Русский", "English", "中文（简体）", "Español", "हिन्दी").forEach { language ->
            val selected = when (language) { "Русский" -> "Русский"; "English" -> "English"; "中文（简体）" -> "中文（简体）"; "Español" -> "Español"; else -> "हिन्दी" }
            if (languageName == selected) Button(onClick = { onLanguage(selected) }, modifier = Modifier.fillMaxWidth()) { Text(language) }
            else OutlinedButton(onClick = { onLanguage(selected) }, modifier = Modifier.fillMaxWidth()) { Text(language) }
        }
        Text(tr("Выбор языка сохраняется на этом устройстве."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun NotesScreen(
    notes: List<Note>, title: String, onTitle: (String) -> Unit, body: String, onBody: (String) -> Unit,
    search: String, onSearch: (String) -> Unit, onAddNote: () -> Unit, onDeleteNote: (Note) -> Unit, onBack: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(tr("Мои заметки"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack) { Text(tr("← Назад")) }
        }
        OutlinedTextField(title, onTitle, Modifier.fillMaxWidth(), label = { Text(tr("Заголовок")) }, singleLine = true)
        OutlinedTextField(body, onBody, Modifier.fillMaxWidth(), label = { Text(tr("Текст заметки")) }, minLines = 2)
        Button(onClick = onAddNote, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank() || body.isNotBlank()) { Text(tr("Сохранить заметку")) }
        OutlinedTextField(search, onSearch, Modifier.fillMaxWidth(), label = { Text(tr("Поиск по заметкам")) }, singleLine = true)
        Text(tr("Заметки · ${notes.size}"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(notes, key = { it.id }) { note ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(note.title, fontWeight = FontWeight.Bold)
                        if (note.body.isNotBlank()) Text(note.body)
                        Text(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(note.updatedAt)), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        TextButton(onClick = { onDeleteNote(note) }) { Text(tr("Удалить")) }
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
            Text(tr(title), fontWeight = FontWeight.SemiBold)
            Text(tr(subtitle), style = MaterialTheme.typography.bodySmall)
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
    fun loadLanguage(c: Context): String = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString("language", "Русский") ?: "Русский"
    fun saveLanguage(c: Context, name: String) { c.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString("language", name).apply() }
    private fun <T> decode(c: Context, key: String, convert: (JSONObject) -> T): List<T> {
        val array = readArray(c, key)
        return try { (0 until array.length()).map { convert(array.getJSONObject(it)) } } catch (_: Exception) { emptyList() }
    }
    private fun save(c: Context, key: String, values: List<JSONObject>) {
        val array = JSONArray(); values.forEach { array.put(it) }; saveArray(c, key, array)
    }
}
