# 📦 Remote Sync

> **Sync your Git changes to a remote server over SSH – automatically.**

Remote Sync — это плагин для IntelliJ IDEA, который отслеживает изменения в локальной Git-репозитории и синхронизирует их с удалённым сервером по SSH.

---

## 🚀 Возможности

- 📤 Автоматическая отправка новых и изменённых файлов
- 🗑️ Удаление удалённых файлов с сервера
- 🛠️ Настройка SSH (пользователь, ключ или пароль, хост и путь)
- 🌿 Поддержка любых Git-веток
- 💡 Интуитивный UI прямо в IntelliJ

---

## 🖼️ Интерфейс

### 🌙 Тёмная тема
![Remote Sync – Dark Theme](docs/images/remote-sync-dark.png)

### ☀️ Светлая тема
![Remote Sync – Light Theme](docs/images/remote-sync-light.png)

Плагин располагается в нижней панели инструментов. Просто укажи настройки и нажми **Save & Sync** — всё остальное произойдёт автоматически.

---

## ⚙️ Установка

1. Перейди в **Settings → Plugins**
2. Открой вкладку **Marketplace** и найди **Remote Sync**
3. Нажми **Install** и перезапусти IDE

Или установи вручную:

- Скачай `.zip` с [JetBrains Marketplace](https://plugins.jetbrains.com/)
- Перейди в **Settings → Plugins → ⚙ → Install Plugin from Disk**

---

## 🔧 Настройка

После установки:

1. Открой вкладку **Remote Sync** в нижней панели IDE
2. Заполни поля:
   - `Username` — имя пользователя SSH
   - `IP` — IP-адрес сервера
   - `Password` или `Private Key Path`
   - `Git Remote Path` — например, `git@github.com:user/project.git`
   - `Git Branch` — ветка, которую нужно отслеживать
3. Нажми **Save & Sync** — плагин выполнит сравнение и синхронизацию

---

## 📂 Исходный код

Исходники доступны на GitHub:  
🔗 [https://github.com/Ozsfag/remote-sync-plugin](https://github.com/Ozsfag/remote-sync-plugin)

---

## 📜 Лицензия

Лицензия: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)

---

## ✉️ Обратная связь

Сообщения об ошибках и предложения:
[Открыть issue на GitHub](https://github.com/Ozsfag/remote-sync-plugin/issues)

---
