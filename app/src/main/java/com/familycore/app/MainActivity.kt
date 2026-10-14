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
import androidx.compose.ui.graphics.Brush
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
private fun tr(value: String): String = translations[LocalAppLanguage.current]?.get(value) ?: extraTranslations[LocalAppLanguage.current]?.get(value) ?: value

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


private val extraTranslations: Map<String, Map<String, String>> = mapOf(
    "English" to mapOf(
        "← Домой" to "← Home", "Добро пожаловать" to "Welcome", "Открыть покупки и запасы" to "Open shopping and pantry",
        "Заметки и воспоминания" to "Notes and memories", "Чат подключится после настройки серверной синхронизации." to "Chat will be available after server sync is configured.",
        "Задачи и домашние дела" to "Tasks and chores", "Расписание и события" to "Schedule and events", "Открыть заметки" to "Open notes",
        "Документы и ИИ-помощник появятся в следующих этапах." to "Documents and the AI assistant will arrive in a later update.",
        "Пока здесь доступны только локальные действия. Сопряжение устройств и сигналы помощи требуют защищённой реализации." to "Only local actions are available here for now. Device pairing and help alerts require a secure implementation.",
        "Посмотреть семейные дела" to "View family tasks", "Общие дела" to "Shared tasks",
        "Задачи пока сохраняются на этом телефоне. Семейная синхронизация ещё не подключена." to "Tasks are saved on this phone for now. Family sync is not connected yet.",
        "Что нужно сделать?" to "What needs to be done?", "Исполнитель (необязательно)" to "Assigned to (optional)", "Срок, например 20.10.2026" to "Due date, e.g. 20/10/2026",
        "Приоритет" to "Priority", "Низкий" to "Low", "Обычный" to "Normal", "Высокий" to "High", "Добавить задачу" to "Add task",
        "Удалить" to "Delete", "Кому" to "Assigned to", "Срок" to "Due", "Все" to "All", "Активные" to "Active", "Готовые" to "Completed",
        "Здесь появятся семейные задачи." to "Family tasks will appear here.", "В этом фильтре пока пусто." to "Nothing in this filter yet.",
        "Добавь дело выше или выбери другой фильтр." to "Add a task above or choose another filter.",
        "Семейный календарь" to "Family calendar", "Локальные события, пока без системных напоминаний." to "Local events; system reminders are not available yet.",
        "Название события" to "Event title", "Дата или время" to "Date or time", "Сохранить событие" to "Save event",
        "Пока нет событий." to "No events yet.", "Добавь день рождения, встречу или семейный план." to "Add a birthday, meeting, or family plan.",
        "Список покупок, домашние запасы и рецепты." to "Shopping list, pantry, and recipes.", "События" to "Events", "Открыть" to "Open",
        "К календарю" to "Go to calendar", "Покупки" to "Shopping", "К списку" to "Open list", "Заметки" to "Notes", "Мои записи" to "My notes",
        "Запасы" to "Pantry", "Рецепты" to "Recipes", "Добавить продукт" to "Add item", "Добавить в список" to "Add to list", "Убрать" to "Remove",
        "Список пока пуст." to "The list is empty for now.", "Добавь продукты, которые нужно купить." to "Add items you need to buy.",
        "Продукт дома" to "Item in pantry", "Срок годности (необязательно)" to "Expiry date (optional)", "Добавить в запасы" to "Add to pantry",
        "Запасы ещё не заполнены." to "Your pantry is empty for now.", "Добавь продукты и при желании укажи срок годности." to "Add pantry items and optionally enter expiry dates.",
        "Овсяная каша с яблоком" to "Apple oatmeal", "Овсяные хлопья, молоко или вода, яблоко. Свари хлопья и добавь нарезанное яблоко." to "Oats, milk or water, and an apple. Cook the oats and add the chopped apple.",
        "Овощной омлет" to "Vegetable omelette", "Яйца, помидор, сладкий перец. Нарежь овощи, добавь яйца и готовь до полной готовности." to "Eggs, tomato, and bell pepper. Chop the vegetables, add the eggs, and cook through.",
        "Тёплый бутерброд с сыром" to "Warm cheese sandwich", "Хлеб, сыр, по желанию помидор. Прогрей до расплавления сыра." to "Bread, cheese, and optionally tomato. Heat until the cheese melts.",
        "Автоматическое меню и расчёт ингредиентов пока не подключены." to "Automatic meal planning and ingredient calculations are not available yet.",
        "Чат пока не подключён" to "Chat is not connected yet.",
        "Для настоящей переписки нужны семейные аккаунты, защищённая синхронизация и доставка сообщений. Здесь нет фиктивных участников или сообщений." to "Real messaging requires family accounts, secure sync, and message delivery. No fake participants or messages are shown here.",
        "Мои заметки" to "My notes", "Заголовок" to "Title", "Текст заметки" to "Note text", "Сохранить заметку" to "Save note",
        "Поиск по заметкам" to "Search notes", "Заметок пока нет." to "No notes yet.", "Создай первую запись выше." to "Create your first note above.",
        "Без названия" to "Untitled", "Заметка сохранена" to "Note saved", "Заметка удалена" to "Note deleted", "Покупка сохранена" to "Shopping item saved",
        "Список покупок сохранён" to "Shopping list saved", "Покупка удалена" to "Shopping item removed", "Продукт добавлен в запасы" to "Item added to pantry",
        "Задача сохранена" to "Task saved", "Статус задачи сохранён" to "Task status saved", "Задача удалена" to "Task deleted",
        "Событие сохранено" to "Event saved", "Событие удалено" to "Event deleted", "Данные сохраняются на этом устройстве · синхронизации пока нет" to "Data is saved on this device · sync is not available yet",
        "Не указан" to "Not specified", "Дата не указана" to "Date not specified", "Имя не указано" to "Name not set",
        "Всё для продуктов, покупок и приготовления еды." to "Everything for groceries, shopping, and cooking.",
        "Место для семейного общения и общих воспоминаний." to "A place for family conversations and shared memories.",
        "Здесь можно планировать дела и семейное расписание." to "Plan chores and family schedules here.",
        "Личные записи и документы — с приватностью по умолчанию." to "Personal notes and documents, private by default.",
        "Быстрый доступ к семейным действиям и устройствам." to "Quick access to family actions and devices.",
        "Откроется после подключения защищённой синхронизации" to "Available after secure sync is connected",
        "Например, 12.05.1995" to "For example, 12/05/1995",
        "Родитель" to "Parent", "Ребёнок" to "Child", "Другой" to "Other",
        "Лаванда" to "Lavender", "Шалфей" to "Sage", "Океан" to "Ocean"
    ),
    "中文（简体）" to mapOf(
        "← Домой" to "← 首页", "Добро пожаловать" to "欢迎", "Открыть покупки и запасы" to "打开购物清单和储物柜",
        "Заметки и воспоминания" to "笔记与回忆", "Чат подключится после настройки серверной синхронизации." to "配置服务器同步后即可使用聊天。",
        "Задачи и домашние дела" to "任务与家务", "Расписание и события" to "日程与活动", "Открыть заметки" to "打开笔记",
        "Документы и ИИ-помощник появятся в следующих этапах." to "文档和 AI 助手将在后续版本推出。",
        "Пока здесь доступны только локальные действия. Сопряжение устройств и сигналы помощи требуют защищённой реализации。" to "目前这里只提供本地操作。设备配对和求助提醒需要安全实现。",
        "Пока здесь доступны только локальные действия. Сопряжение устройств и сигналы помощи требуют защищённой реализации." to "目前这里只提供本地操作。设备配对和求助提醒需要安全实现。",
        "Посмотреть семейные дела" to "查看家庭任务", "Общие дела" to "共同任务",
        "Задачи пока сохраняются на этом телефоне. Семейная синхронизация ещё не подключена." to "任务目前保存在这部手机上，家庭同步尚未启用。",
        "Что нужно сделать?" to "需要做什么？", "Исполнитель (необязательно)" to "负责人（可选）", "Срок, например 20.10.2026" to "截止日期，例如 2026/10/20",
        "Приоритет" to "优先级", "Низкий" to "低", "Обычный" to "普通", "Высокий" to "高", "Добавить задачу" to "添加任务",
        "Удалить" to "删除", "Кому" to "负责人", "Срок" to "期限", "Все" to "全部", "Активные" to "进行中", "Готовые" to "已完成",
        "Здесь появятся семейные задачи." to "家庭任务会显示在这里。", "В этом фильтре пока пусто." to "此筛选条件下暂无内容。",
        "Добавь дело выше или выбери другой фильтр." to "请在上方添加任务，或选择其他筛选条件。",
        "Семейный календарь" to "家庭日历", "Локальные события, пока без системных напоминаний." to "本地活动；系统提醒功能尚未启用。",
        "Название события" to "活动名称", "Дата или время" to "日期或时间", "Сохранить событие" to "保存活动",
        "Пока нет событий." to "暂时没有活动。", "Добавь день рождения, встречу или семейный план." to "添加生日、会面或家庭计划。",
        "Список покупок, домашние запасы и рецепты." to "购物清单、家庭储物和食谱。", "События" to "活动", "Открыть" to "打开",
        "К календарю" to "前往日历", "Покупки" to "购物", "К списку" to "查看清单", "Заметки" to "笔记", "Мои записи" to "我的记录",
        "Запасы" to "储物", "Рецепты" to "食谱", "Добавить продукт" to "添加物品", "Добавить в список" to "加入清单", "Убрать" to "移除",
        "Список пока пуст." to "清单暂时为空。", "Добавь продукты, которые нужно купить." to "添加需要购买的物品。",
        "Продукт дома" to "储物物品", "Срок годности (необязательно)" to "保质期（可选）", "Добавить в запасы" to "添加到储物",
        "Запасы ещё не заполнены." to "储物清单为空。", "Добавь продукты и при желании укажи срок годности." to "添加物品，并可选择填写保质期。",
        "Овсяная каша с яблоком" to "苹果燕麦粥", "Овсяные хлопья, молоко или вода, яблоко. Свари хлопья и добавь нарезанное яблоко." to "燕麦、牛奶或水、苹果。煮好燕麦后加入切好的苹果。",
        "Овощной омлет" to "蔬菜煎蛋", "Яйца, помидор, сладкий перец. Нарежь овощи, добавь яйца и готовь до полной готовности." to "鸡蛋、番茄和甜椒。切好蔬菜，加入鸡蛋并充分煎熟。",
        "Тёплый бутерброд с сыром" to "热芝士三明治", "Хлеб, сыр, по желанию помидор. Прогрей до расплавления сыра." to "面包、芝士，可选番茄。加热至芝士融化。",
        "Автоматическое меню и расчёт ингредиентов пока не подключены." to "自动菜单和食材用量计算尚未启用。",
        "Чат пока не подключён" to "聊天功能尚未连接。",
        "Для настоящей переписки нужны семейные аккаунты, защищённая синхронизация и доставка сообщений. Здесь нет фиктивных участников или сообщений." to "真正的聊天需要家庭账号、安全同步和消息传递。这里不会显示虚构的参与者或消息。",
        "Мои заметки" to "我的笔记", "Заголовок" to "标题", "Текст заметки" to "笔记内容", "Сохранить заметку" to "保存笔记",
        "Поиск по заметкам" to "搜索笔记", "Заметок пока нет." to "暂时没有笔记。", "Создай первую запись выше." to "请在上方创建第一条笔记。",
        "Без названия" to "无标题", "Заметка сохранена" to "笔记已保存", "Заметка удалена" to "笔记已删除", "Покупка сохранена" to "购物项已保存",
        "Список покупок сохранён" to "购物清单已保存", "Покупка удалена" to "购物项已移除", "Продукт добавлен в запасы" to "物品已加入储物",
        "Задача сохранена" to "任务已保存", "Статус задачи сохранён" to "任务状态已保存", "Задача удалена" to "任务已删除",
        "Событие сохранено" to "活动已保存", "Событие удалено" to "活动已删除", "Данные сохраняются на этом устройстве · синхронизации пока нет" to "数据保存在本设备上 · 尚未启用同步",
        "Не указан" to "未填写", "Дата не указана" to "未填写日期", "Имя не указано" to "未填写姓名",
        "Всё для продуктов, покупок и приготовления еды." to "管理食品、购物和烹饪所需的一切。",
        "Место для семейного общения и общих воспоминаний." to "家人交流和分享回忆的地方。",
        "Здесь можно планировать дела и семейное расписание." to "在这里安排家务和家庭日程。",
        "Личные записи и документы — с приватностью по умолчанию." to "个人笔记和文档，默认注重隐私。",
        "Быстрый доступ к семейным действиям и устройствам." to "快速访问家庭操作和设备。",
        "Откроется после подключения защищённой синхронизации" to "安全同步启用后可使用",
        "Например, 12.05.1995" to "例如：1995/05/12",
        "Родитель" to "家长", "Ребёнок" to "孩子", "Другой" to "其他",
        "Лаванда" to "薰衣草", "Шалфей" to "鼠尾草", "Океан" to "海洋"
    ),
    "Español" to mapOf(
        "← Домой" to "← Inicio", "Добро пожаловать" to "Bienvenido", "Открыть покупки и запасы" to "Abrir compras y despensa",
        "Заметки и воспоминания" to "Notas y recuerdos", "Чат подключится после настройки серверной синхронизации." to "El chat estará disponible cuando se configure la sincronización del servidor.",
        "Задачи и домашние дела" to "Tareas del hogar", "Расписание и события" to "Horario y eventos", "Открыть заметки" to "Abrir notas",
        "Документы и ИИ-помощник появятся в следующих этапах." to "Los documentos y el asistente de IA llegarán en una actualización futura.",
        "Пока здесь доступны только локальные действия. Сопряжение устройств и сигналы помощи требуют защищённой реализации." to "Por ahora solo hay acciones locales. La vinculación de dispositivos y las alertas de ayuda requieren una implementación segura.",
        "Посмотреть семейные дела" to "Ver tareas familiares", "Общие дела" to "Tareas compartidas",
        "Задачи пока сохраняются на этом телефоне. Семейная синхронизация ещё не подключена." to "Por ahora, las tareas se guardan en este teléfono. La sincronización familiar aún no está conectada.",
        "Что нужно сделать?" to "¿Qué hay que hacer?", "Исполнитель (необязательно)" to "Responsable (opcional)", "Срок, например 20.10.2026" to "Fecha límite, p. ej. 20/10/2026",
        "Приоритет" to "Prioridad", "Низкий" to "Bajo", "Обычный" to "Normal", "Высокий" to "Alto", "Добавить задачу" to "Añadir tarea",
        "Удалить" to "Eliminar", "Кому" to "Responsable", "Срок" to "Fecha límite", "Все" to "Todas", "Активные" to "Activas", "Готовые" to "Completadas",
        "Здесь появятся семейные задачи." to "Aquí aparecerán las tareas familiares.", "В этом фильтре пока пусто." to "Este filtro aún está vacío.",
        "Добавь дело выше или выбери другой фильтр." to "Añade una tarea arriba o elige otro filtro.",
        "Семейный календарь" to "Calendario familiar", "Локальные события, пока без системных напоминаний." to "Eventos locales; los recordatorios del sistema aún no están disponibles.",
        "Название события" to "Nombre del evento", "Дата или время" to "Fecha u hora", "Сохранить событие" to "Guardar evento",
        "Пока нет событий." to "Todavía no hay eventos.", "Добавь день рождения, встречу или семейный план." to "Añade un cumpleaños, una reunión o un plan familiar.",
        "Список покупок, домашние запасы и рецепты." to "Lista de compras, despensa y recetas.", "События" to "Eventos", "Открыть" to "Abrir",
        "К календарю" to "Ir al calendario", "Покупки" to "Compras", "К списку" to "Ver lista", "Заметки" to "Notas", "Мои записи" to "Mis notas",
        "Запасы" to "Despensa", "Рецепты" to "Recetas", "Добавить продукт" to "Añadir artículo", "Добавить в список" to "Añadir a la lista", "Убрать" to "Quitar",
        "Список пока пуст." to "La lista está vacía por ahora.", "Добавь продукты, которые нужно купить." to "Añade los productos que necesitas comprar.",
        "Продукт дома" to "Artículo en la despensa", "Срок годности (необязательно)" to "Fecha de caducidad (opcional)", "Добавить в запасы" to "Añadir a la despensa",
        "Запасы ещё не заполнены." to "La despensa aún está vacía.", "Добавь продукты и при желании укажи срок годности." to "Añade productos e indica su fecha de caducidad si quieres.",
        "Овсяная каша с яблоком" to "Avena con manzana", "Овсяные хлопья, молоко или вода, яблоко. Свари хлопья и добавь нарезанное яблоко." to "Avena, leche o agua y una manzana. Cocina la avena y añade la manzana cortada.",
        "Овощной омлет" to "Tortilla de verduras", "Яйца, помидор, сладкий перец. Нарежь овощи, добавь яйца и готовь до полной готовности." to "Huevos, tomate y pimiento. Corta las verduras, añade los huevos y cocina bien.",
        "Тёплый бутерброд с сыром" to "Sándwich caliente de queso", "Хлеб, сыр, по желанию помидор. Прогрей до расплавления сыра." to "Pan, queso y tomate opcional. Calienta hasta que el queso se derrita.",
        "Автоматическое меню и расчёт ингредиентов пока не подключены." to "La planificación automática de menús y el cálculo de ingredientes aún no están disponibles.",
        "Чат пока не подключён" to "El chat aún no está conectado.",
        "Для настоящей переписки нужны семейные аккаунты, защищённая синхронизация и доставка сообщений. Здесь нет фиктивных участников или сообщений." to "La mensajería real requiere cuentas familiares, sincronización segura y entrega de mensajes. Aquí no se muestran participantes ni mensajes ficticios.",
        "Мои заметки" to "Mis notas", "Заголовок" to "Título", "Текст заметки" to "Texto de la nota", "Сохранить заметку" to "Guardar nota",
        "Поиск по заметкам" to "Buscar notas", "Заметок пока нет." to "Todavía no hay notas.", "Создай первую запись выше." to "Crea tu primera nota arriba.",
        "Без названия" to "Sin título", "Заметка сохранена" to "Nota guardada", "Заметка удалена" to "Nota eliminada", "Покупка сохранена" to "Artículo guardado",
        "Список покупок сохранён" to "Lista de compras guardada", "Покупка удалена" to "Artículo eliminado", "Продукт добавлен в запасы" to "Artículo añadido a la despensa",
        "Задача сохранена" to "Tarea guardada", "Статус задачи сохранён" to "Estado de la tarea guardado", "Задача удалена" to "Tarea eliminada",
        "Событие сохранено" to "Evento guardado", "Событие удалено" to "Evento eliminado", "Данные сохраняются на этом устройстве · синхронизации пока нет" to "Los datos se guardan en este dispositivo · aún no hay sincronización",
        "Не указан" to "No especificado", "Дата не указана" to "Fecha no indicada", "Имя не указано" to "Nombre sin indicar",
        "Всё для продуктов, покупок и приготовления еды." to "Todo para alimentos, compras y cocina.",
        "Место для семейного общения и общих воспоминаний." to "Un lugar para conversar en familia y compartir recuerdos.",
        "Здесь можно планировать дела и семейное расписание." to "Aquí puedes planificar tareas y horarios familiares.",
        "Личные записи и документы — с приватностью по умолчанию." to "Notas y documentos personales, privados por defecto.",
        "Быстрый доступ к семейным действиям и устройствам." to "Acceso rápido a acciones y dispositivos familiares.",
        "Откроется после подключения защищённой синхронизации" to "Disponible cuando se active la sincronización segura",
        "Например, 12.05.1995" to "Por ejemplo, 12/05/1995",
        "Родитель" to "Madre/padre", "Ребёнок" to "Hijo/a", "Другой" to "Otro",
        "Лаванда" to "Lavanda", "Шалфей" to "Salvia", "Океан" to "Océano"
    ),
    "हिन्दी" to mapOf(
        "← Домой" to "← होम", "Добро пожаловать" to "स्वागत है", "Открыть покупки и запасы" to "खरीदारी और भंडार खोलें",
        "Заметки и воспоминания" to "नोट्स और यादें", "Чат подключится после настройки серверной синхронизации." to "सर्वर सिंक सेट होने के बाद चैट उपलब्ध होगी।",
        "Задачи и домашние дела" to "कार्य और घरेलू काम", "Расписание и события" to "समय-सारणी और कार्यक्रम", "Открыть заметки" to "नोट्स खोलें",
        "Документы и ИИ-помощник появятся в следующих этапах." to "दस्तावेज़ और AI सहायक भविष्य के अपडेट में आएँगे।",
        "Пока здесь доступны только локальные действия. Сопряжение устройств и сигналы помощи требуют защищённой реализации." to "अभी यहाँ केवल स्थानीय सुविधाएँ उपलब्ध हैं। डिवाइस जोड़ने और सहायता अलर्ट के लिए सुरक्षित कार्यान्वयन चाहिए।",
        "Посмотреть семейные дела" to "परिवार के कार्य देखें", "Общие дела" to "साझा कार्य",
        "Задачи пока сохраняются на этом телефоне. Семейная синхронизация ещё не подключена." to "अभी कार्य इसी फ़ोन पर सहेजे जाते हैं। परिवार सिंक अभी जुड़ा नहीं है।",
        "Что нужно сделать?" to "क्या करना है?", "Исполнитель (необязательно)" to "जिम्मेदार व्यक्ति (वैकल्पिक)", "Срок, например 20.10.2026" to "अंतिम तिथि, जैसे 20/10/2026",
        "Приоритет" to "प्राथमिकता", "Низкий" to "कम", "Обычный" to "सामान्य", "Высокий" to "उच्च", "Добавить задачу" to "कार्य जोड़ें",
        "Удалить" to "हटाएँ", "Кому" to "जिम्मेदार", "Срок" to "अंतिम तिथि", "Все" to "सभी", "Активные" to "सक्रिय", "Готовые" to "पूर्ण",
        "Здесь появятся семейные задачи." to "परिवार के कार्य यहाँ दिखाई देंगे।", "В этом фильтре пока пусто." to "इस फ़िल्टर में अभी कुछ नहीं है।",
        "Добавь дело выше или выбери другой фильтр." to "ऊपर कार्य जोड़ें या दूसरा फ़िल्टर चुनें।",
        "Семейный календарь" to "परिवार कैलेंडर", "Локальные события, пока без системных напоминаний." to "स्थानीय कार्यक्रम; सिस्टम रिमाइंडर अभी उपलब्ध नहीं हैं।",
        "Название события" to "कार्यक्रम का नाम", "Дата или время" to "तारीख या समय", "Сохранить событие" to "कार्यक्रम सहेजें",
        "Пока нет событий." to "अभी कोई कार्यक्रम नहीं है।", "Добавь день рождения, встречу или семейный план." to "जन्मदिन, मुलाक़ात या परिवार की योजना जोड़ें।",
        "Список покупок, домашние запасы и рецепты." to "खरीदारी सूची, घरेलू भंडार और रेसिपी।", "События" to "कार्यक्रम", "Открыть" to "खोलें",
        "К календарю" to "कैलेंडर पर जाएँ", "Покупки" to "खरीदारी", "К списку" to "सूची खोलें", "Заметки" to "नोट्स", "Мои записи" to "मेरे नोट्स",
        "Запасы" to "भंडार", "Рецепты" to "रेसिपी", "Добавить продукт" to "वस्तु जोड़ें", "Добавить в список" to "सूची में जोड़ें", "Убрать" to "हटाएँ",
        "Список пока пуст." to "सूची अभी खाली है।", "Добавь продукты, которые нужно купить." to "खरीदने वाली चीज़ें जोड़ें।",
        "Продукт дома" to "भंडार की वस्तु", "Срок годности (необязательно)" to "समाप्ति तिथि (वैकल्पिक)", "Добавить в запасы" to "भंडार में जोड़ें",
        "Запасы ещё не заполнены." to "भंडार अभी खाली है।", "Добавь продукты и при желании укажи срок годности." to "वस्तुएँ जोड़ें और चाहें तो समाप्ति तिथि लिखें।",
        "Овсяная каша с яблоком" to "सेब वाला ओटमील", "Овсяные хлопья, молоко или вода, яблоко. Свари хлопья и добавь нарезанное яблоко." to "ओट्स, दूध या पानी और सेब लें। ओट्स पकाएँ और कटा हुआ सेब मिलाएँ।",
        "Овощной омлет" to "सब्ज़ियों का ऑमलेट", "Яйца, помидор, сладкий перец. Нарежь овощи, добавь яйца и готовь до полной готовности." to "अंडे, टमाटर और शिमला मिर्च लें। सब्ज़ियाँ काटें, अंडे डालें और अच्छी तरह पकाएँ।",
        "Тёплый бутерброд с сыром" to "गर्म चीज़ सैंडविच", "Хлеб, сыр, по желанию помидор. Прогрей до расплавления сыра." to "ब्रेड, चीज़ और चाहें तो टमाटर लें। चीज़ पिघलने तक गर्म करें।",
        "Автоматическое меню и расчёт ингредиентов пока не подключены." to "स्वचालित मेनू और सामग्री की गणना अभी उपलब्ध नहीं है।",
        "Чат пока не подключён" to "चैट अभी जुड़ी नहीं है।",
        "Для настоящей переписки нужны семейные аккаунты, защищённая синхронизация и доставка сообщений. Здесь нет фиктивных участников или сообщений." to "वास्तविक चैट के लिए परिवार खाते, सुरक्षित सिंक और संदेश सेवा चाहिए। यहाँ नकली सदस्य या संदेश नहीं दिखाए जाते।",
        "Мои заметки" to "मेरे नोट्स", "Заголовок" to "शीर्षक", "Текст заметки" to "नोट का पाठ", "Сохранить заметку" to "नोट सहेजें",
        "Поиск по заметкам" to "नोट्स खोजें", "Заметок пока нет." to "अभी कोई नोट नहीं है।", "Создай первую запись выше." to "ऊपर अपना पहला नोट बनाएँ।",
        "Без названия" to "बिना शीर्षक", "Заметка сохранена" to "नोट सहेजा गया", "Заметка удалена" to "नोट हटाया गया", "Покупка сохранена" to "खरीदारी आइटम सहेजा गया",
        "Список покупок сохранён" to "खरीदारी सूची सहेजी गई", "Покупка удалена" to "खरीदारी आइटम हटाया गया", "Продукт добавлен в запасы" to "वस्तु भंडार में जोड़ी गई",
        "Задача сохранена" to "कार्य सहेजा गया", "Статус задачи сохранён" to "कार्य की स्थिति सहेजी गई", "Задача удалена" to "कार्य हटाया गया",
        "Событие сохранено" to "कार्यक्रम सहेजा गया", "Событие удалено" to "कार्यक्रम हटाया गया", "Данные сохраняются на этом устройстве · синхронизации пока нет" to "डेटा इस डिवाइस पर सहेजा जाता है · सिंक अभी उपलब्ध नहीं है",
        "Не указан" to "निर्दिष्ट नहीं", "Дата не указана" to "तारीख नहीं दी गई", "Имя не указано" to "नाम नहीं दिया गया",
        "Всё для продуктов, покупок и приготовления еды." to "खाद्य सामग्री, खरीदारी और खाना बनाने के लिए सब कुछ।",
        "Место для семейного общения и общих воспоминаний." to "परिवार की बातचीत और साझा यादों के लिए जगह।",
        "Здесь можно планировать дела и семейное расписание." to "यहाँ काम और परिवार का समय तय करें।",
        "Личные записи и документы — с приватностью по умолчанию." to "निजी नोट्स और दस्तावेज़, डिफ़ॉल्ट रूप से गोपनीय।",
        "Быстрый доступ к семейным действиям и устройствам." to "परिवार के कार्यों और डिवाइस तक त्वरित पहुँच।",
        "Откроется после подключения защищённой синхронизации" to "सुरक्षित सिंक चालू होने पर उपलब्ध होगा",
        "Например, 12.05.1995" to "उदाहरण: 12/05/1995",
        "Родитель" to "माता-पिता", "Ребёнок" to "बच्चा", "Другой" to "अन्य",
        "Лаванда" to "लैवेंडर", "Шалфей" to "सेज", "Океан" to "समुद्र"
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
                    secondaryContainer = Color(0xFF302816), onBackground = Color(0xFFF3E7C2),
                    outline = Color(0xFF8C6F1F), outlineVariant = Color(0xFF493A18)
                ) else lightColorScheme(
                    primary = primary, onPrimary = Color.White, background = Cream,
                    surface = Color.White, onSurface = Ink, surfaceVariant = tint,
                    secondaryContainer = Sage
                )) {
                    Surface(
                        Modifier.fillMaxSize().then(if (goldTheme) Modifier.background(Brush.verticalGradient(listOf(Color(0xFF070707), Color(0xFF19160F), Color(0xFF080808)))) else Modifier),
                        color = if (goldTheme) Color.Transparent else Cream
                    ) {
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
        containerColor = if (themeName == "Obsidian Gold") Color.Transparent else MaterialTheme.colorScheme.background,
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
                    Text("v${BuildConfig.VERSION_NAME}", Modifier.padding(horizontal = 10.dp, vertical = 6.dp), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
                    themeName == "Obsidian Gold", tasks.count { !it.done }, events.size, notes.size, shopping.count { !it.bought },
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
            Text(tr(status), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(3.dp))
        }
    }
}

@Composable
private fun HomeScreen(
    goldTheme: Boolean, openTasksCount: Int, eventsCount: Int, notesCount: Int, shoppingCount: Int,
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
                    AnimatedHouseIllustration(goldTheme)
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
private fun AnimatedHouseIllustration(goldTheme: Boolean) {
    val motion = rememberInfiniteTransition(label = "houseAmbient")
    val glow by motion.animateFloat(0.38f, 0.92f, infiniteRepeatable(tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "windowGlow")
    val floatY by motion.animateFloat(0f, -1.5f, infiniteRepeatable(tween(3600, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "houseFloat")
    Canvas(Modifier.fillMaxWidth().height(218.dp).offset(y = floatY.dp)) {
        val w = size.width; val h = size.height
        // Soft garden and pathway
        drawOval(if (goldTheme) Color(0xFF171717) else Color(0xFFD9D1EA), Offset(w*.07f,h*.77f), Size(w*.86f,h*.15f))
        drawOval(if (goldTheme) Color(0xFF202020) else Color(0xFFE7E0F1), Offset(w*.12f,h*.73f), Size(w*.76f,h*.12f))
        val left=w*.19f; val right=w*.81f; val top=h*.28f; val bottom=h*.78f
        // Main walls, slight side plane for depth
        val side=Path().apply { moveTo(right,top+h*.10f); lineTo(right+w*.07f,top+h*.16f); lineTo(right+w*.07f,bottom-h*.02f); lineTo(right,bottom); close() }
        drawPath(side,if (goldTheme) Color(0xFF171717) else Color(0xFFE8DCCB))
        drawRect(if (goldTheme) Color(0xFF242424) else Color(0xFFFFFAF1),Offset(left,top+h*.10f),Size(right-left,bottom-(top+h*.10f)))
        // Roof with layered eaves and soft shadow
        val shadow=Path().apply { moveTo(left-w*.07f,top+h*.12f); lineTo(w*.50f,top-h*.20f); lineTo(right+w*.07f,top+h*.12f); lineTo(right+w*.04f,top+h*.16f); lineTo(w*.50f,top-h*.15f); lineTo(left-w*.04f,top+h*.16f); close() }
        drawPath(shadow,(if (goldTheme) Color(0xFFD4AF37) else Color(0xFF6D5A9B)).copy(alpha=.16f))
        val roof=Path().apply { moveTo(left-w*.075f,top+h*.12f); lineTo(w*.50f,top-h*.23f); lineTo(right+w*.075f,top+h*.12f); close() }
        drawPath(roof,if (goldTheme) Color(0xFF0B0B0B) else Color(0xFF8D7ABD))
        drawPath(roof,if (goldTheme) Color(0xFFD4AF37) else Color(0xFF6D5A9B),style=Stroke(width=2.4.dp.toPx()))
        drawLine(if (goldTheme) Color(0xFF8C6F1F) else Color(0xFFB8A6D8),Offset(w*.50f,top-h*.19f),Offset(w*.50f,top+h*.10f),strokeWidth=1.3.dp.toPx())
        // Interior dividers make the rooms legible at a glance
        drawLine(if (goldTheme) Color(0xFF5B4A20) else Color(0xFFE0D4C4),Offset(w*.50f,top+h*.10f),Offset(w*.50f,bottom),strokeWidth=3.dp.toPx())
        drawLine(if (goldTheme) Color(0xFF5B4A20) else Color(0xFFE0D4C4),Offset(left,h*.55f),Offset(right,h*.55f),strokeWidth=3.dp.toPx())
        // Four distinct room windows / room lights
        val windows=listOf(
            Triple(Offset(w*.29f,h*.43f),if (goldTheme) Color(0xFFFFD86B) else Color(0xFFF0D58E),true),
            Triple(Offset(w*.59f,h*.43f),if (goldTheme) Color(0xFFD4AF37) else Color(0xFFC9DDCF),false),
            Triple(Offset(w*.29f,h*.61f),if (goldTheme) Color(0xFFFFD86B) else Color(0xFFF0D58E),true),
            Triple(Offset(w*.59f,h*.61f),if (goldTheme) Color(0xFFD4AF37) else Color(0xFFC9DDCF),false)
        )
        windows.forEachIndexed { i, item ->
            val p=item.first; val base=item.second
            drawRoundRect(if (goldTheme) Color(0xFF8C6F1F) else Color(0xFFB6A3D1),Offset(p.x-2.dp.toPx(),p.y-2.dp.toPx()),Size(w*.105f+4.dp.toPx(),h*.105f+4.dp.toPx()),androidx.compose.ui.geometry.CornerRadius(7f))
            drawRoundRect(base.copy(alpha=if(item.third) glow else .9f),p,Size(w*.105f,h*.105f),androidx.compose.ui.geometry.CornerRadius(5f))
            drawLine(if (goldTheme) Color(0xFFFFE8A3) else Color(0xFFFFF9EF),Offset(p.x+w*.052f,p.y),Offset(p.x+w*.052f,p.y+h*.105f),strokeWidth=1.2.dp.toPx())
            drawLine(if (goldTheme) Color(0xFFFFE8A3) else Color(0xFFFFF9EF),Offset(p.x,p.y+h*.052f),Offset(p.x+w*.105f,p.y+h*.052f),strokeWidth=1.2.dp.toPx())
        }
        // Welcoming door and path
        drawRoundRect(if (goldTheme) Color(0xFF8C6F1F) else Color(0xFFB69A80),Offset(w*.445f,h*.63f),Size(w*.11f,h*.15f),androidx.compose.ui.geometry.CornerRadius(6f))
        drawCircle(Color(0xFFFFE7A8),2.5.dp.toPx(),Offset(w*.53f,h*.71f))
        val path=Path().apply { moveTo(w*.46f,h*.78f); lineTo(w*.37f,h*.94f); lineTo(w*.63f,h*.94f); lineTo(w*.54f,h*.78f); close() }
        drawPath(path,if (goldTheme) Color(0xFF191919) else Color(0xFFF6EBDD))
        // Fireflies / ambient dots
        drawCircle((if (goldTheme) Color(0xFFD4AF37) else Color(0xFF9D8AC8)).copy(alpha=glow),3.dp.toPx(),Offset(w*.12f,h*.55f))
        drawCircle((if (goldTheme) Color(0xFFD4AF37) else Color(0xFF9D8AC8)).copy(alpha=.5f),2.dp.toPx(),Offset(w*.87f,h*.50f))
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
            Text(tr(title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(tr(subtitle), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            Text(tr(room), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            TextButton(onClick = onBack) { Text(tr("← Домой")) }
        }
        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(62.dp).background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                    Text(symbol, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                }
                Text(tr("Добро пожаловать"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(tr(intro))
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
            Text(tr(subtitle), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text(tr(title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(tr(description), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                if (priority == value) Button(onClick = { onPriority(value) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) { Text(tr(value)) }
                else OutlinedButton(onClick = { onPriority(value) }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)) { Text(tr(value)) }
            }
        }
        Button(onClick = onAdd, modifier = Modifier.fillMaxWidth(), enabled = title.isNotBlank()) { Text(tr("Добавить задачу")) }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Все", "Активные", "Готовые").forEach { value ->
                if (filter == value) Button(onClick = { filter = value }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) { Text(tr(value)) }
                else OutlinedButton(onClick = { filter = value }, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) { Text(tr(value)) }
            }
        }
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(shown, key = { it.id }) { task ->
                Card(colors = CardDefaults.cardColors(containerColor = if (task.done) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.fillMaxWidth().padding(10.dp), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(task.done, { onToggle(task) })
                            Text(task.title, Modifier.weight(1f), fontWeight = FontWeight.Medium)
                            TextButton(onClick = { onRemove(task) }) { Text(tr("Удалить")) }
                        }
                        val details = listOfNotNull(
                            task.assignee.takeIf { it.isNotBlank() }?.let { "${tr("Кому")}: $it" },
                            task.dueDate.takeIf { it.isNotBlank() }?.let { "${tr("Срок")}: $it" },
                            "${tr("Приоритет")}: ${tr(task.priority)}"
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
                if (tab == name) Button(onClick = { tab = name }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)) { Text(tr(name)) }
                else OutlinedButton(onClick = { tab = name }, contentPadding = PaddingValues(horizontal = 10.dp, vertical = 7.dp)) { Text(tr(name)) }
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
                                    Text("${tr("Срок")}: ${item.expiry}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
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
            Text(tr(title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(tr(description), style = MaterialTheme.typography.bodyMedium)
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
                        Text(name.ifBlank { tr("Имя не указано") }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
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
                        if (role == value) Button(onClick = { onRole(value) }, contentPadding = PaddingValues(horizontal = 9.dp, vertical = 7.dp)) { Text(tr(value)) }
                        else OutlinedButton(onClick = { onRole(value) }, contentPadding = PaddingValues(horizontal = 9.dp, vertical = 7.dp)) { Text(tr(value)) }
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
            if (themeName == name) Button(onClick = { onTheme(name) }, modifier = Modifier.fillMaxWidth()) { Text(tr(name)) }
            else OutlinedButton(onClick = { onTheme(name) }, modifier = Modifier.fillMaxWidth()) { Text(tr(name)) }
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
        Text("${tr("Заметки")} · ${notes.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
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
