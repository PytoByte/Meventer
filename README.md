# Meventer
Meventer - приложение для тех, кто ценит своё время\
Это приложения является моим отностельно крупным проектом\
Репозиторий сервера: [форк PytoByte/Meventer_Server](https://github.com/PytoByte/Meventer_Server) или [оригинал penakelex/Meventer_Server](https://github.com/penakelex/Meventer_Server)

## Суть приложения
Из введения к проекту:
>**Цель проекта:** Разработать мобильное приложение под операционную систему «Android» для людей, которые хотят интересно провести время в компании, обществе людей.
>
>**Актуальность проекта:** Человек - социальное существо, поэтому одной из наших самых главных естественных потребностей является общение. С помощью современных технологий коммуникация стала проще, но зачастую она не выходит за рамки интернета, следовательно человек не получает того опыта, что получил бы при реальном общении. Приложение "Meventer" предлагает решение этой проблемы, помогая пользователям находить единомышленников и организовывать события в реальном мире.
>
>**Возможности практического применения:** Данное приложение открывает дверь в мир новых возможностей, знакомств и незабываемых впечатлений. Пользователи могут легко находить интересующие их события с помощью функций поиска и сортировки. В случае отсутствия подходящего мероприятия пользователи могут создать свое собственное, указав описание, тип, время и место проведения, а также, при необходимости, ограничения по возрасту. Для того, чтобы понять можно ли доверять организатору, достаточно просто посмотреть на его рейтинг, сформированный оценками других пользователей. Перед мероприятием и во время его проведения доступен чат, в котором участники могут обсудить детали события и другие моменты. Также в любой момент пользователь может начать личный чат с другим человеком.
>
>Потенциально, наше приложение способно принести доход благодаря платным мероприятиям


## Технологии
**Kotlin** – высокоуровневый язык программирования общего применения, который является надстройкой над Java и разработан JetBrains – русской компанией разработчиков. Язык поддерживается компанией Google и используется в качестве основного языка для разработки приложений на Android в официальной SDK – Android Studio.  

**Jetpack Compose** — это набор инструментов для построения современных UI (пользовательских интерфейсов) в Android‑приложениях. В основе технологии Jetpack Compose – декларативный подход, разработка происходит без xml-layouts (макетов).  

**Ktor** — это асинхронная платформа для создания микросервисов, веб-приложений и многого другого, написанная на Kotlin. 

**Hilt** — это библиотека для внедрения зависимостей, разработанная Google, и предназначена специально для Android-проектов. Она основана на Dagger и предоставляет упрощенный и автоматизированный подход к внедрению зависимостей.  

[**Compose-destinations**](https://github.com/raamcosta/compose-destinations) – библиотека KSP, которая обрабатывает аннотации и генерирует код, использующий официальную навигацию Jetpack Compose. Она скрывает сложный и шаблонный код, который в противном случае пришлось бы писать.

## Демонстрация

### Профиль
<img src="/screenshots/profile/1.png" alt="main_profile" width="250"/> <img src="/screenshots/profile/2.png" alt="edit_profile" width="250"/> <img src="/screenshots/profile/3.png" alt="edit_password" width="250"/>
1. Главное окно 2. Изменение профиля 3. Изменение пароля

### Мероприятия
<img src="/screenshots/event/1.png" alt="main_event" width="250"/> <img src="/screenshots/event/2.png" alt="create_event" width="250"/>
1. Главное окно 2. Создание мероприятия

<img src="/screenshots/event/3.png" alt="edit_event" width="250"/> <img src="/screenshots/event/4.png" alt="display_event" width="250"/>

3. Изменение мероприятия 4. Окно мероприятия

### Чаты
<img src="/screenshots/chat/1.png" alt="edit_event" width="250"/> <img src="/screenshots/chat/2.png" alt="display_event" width="250"/>
1. Главное окно 2. Окно чата

## Проблемы
- Малый опыт в разработке, привёл к трудностям с поддержкой проекта
- В чатах куча багов, мне не удалось всё исправить
- Использование самоподписанного сертификата привело к тому, что [Coil](https://github.com/coil-kt/coil) и download manager не могли получить данные с сервера, так что пришлось создавать их аналоги самостоятельно. Качество реализации сомнительно
- Приложение много весит, сильно засоряет память. Такого точно не должно было быть
