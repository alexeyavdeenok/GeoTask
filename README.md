## Build APK

1. Clone the repo:

```bash
git clone https://github.com/alexeyavdeenok/GeoTask.git
cd geotask
```

2. Run build:

Linux / Mac:

```bash
./gradlew assembleDebug
```

Windows:

```bash
gradlew assembleDebug
```

3. APK will be here:

```
app/build/outputs/apk/debug/app-debug.apk
```
### Руководство пользователя

Интерактивная карта: В приложении доступна карта с метками ваших локаций. Для их корректного отображения необходимо разрешить доступ к местоположению при первом запуске.

Актуальная погода (API): На главном экране автоматически выводятся данные о температуре и погодных условиях. Информация подгружается в реальном времени через открытый API Open-Meteo.

Управление местами: Список всех сохраненных точек с названиями доступен в разделе «Места» для быстрого просмотра ваших геозон.
