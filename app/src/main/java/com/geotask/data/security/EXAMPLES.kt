package com.geotask.data.security

/**
 * Примеры использования слоя шифрования
 *
 * ⚠️ ВАЖНО: Вам НЕ нужно ничего менять в коде!
 * Шифрование происходит автоматически через DI.
 */

/**
 * Пример 1: Сохранение задачи (работает как и раньше)
 * ====================================================
 *
 * class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
 *     fun createTask(title: String, description: String) {
 *         viewModelScope.launch {
 *             val task = Task(title = title, description = description)
 *             repository.insert(task)  // ← Автоматически шифруется!
 *         }
 *     }
 * }
 */

/**
 * Пример 2: Чтение задач (работает как и раньше)
 * ================================================
 *
 * class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
 *     val allTasks = repository.getAllTasks()  // ← Автоматически расшифровывается!
 *
 *     fun onViewCreated() {
 *         allTasks.observe(this) { tasks ->
 *             // Все задачи уже расшифрованы и готовы к использованию
 *             tasks.forEach { task ->
 *                 println("Task: ${task.title}, Desc: ${task.description}")
 *             }
 *         }
 *     }
 * }
 */

/**
 * Пример 3: Обновление задачи (работает как и раньше)
 * ====================================================
 *
 * class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
 *     fun updateTask(task: Task) {
 *         viewModelScope.launch {
 *             repository.update(task)  // ← Автоматически шифруется!
 *         }
 *     }
 * }
 */

/**
 * Пример 4: Сохранение локации (работает как и раньше)
 * =====================================================
 *
 * class LocationViewModel(private val repository: LocationRepository) : ViewModel() {
 *     fun saveLocation(name: String, lat: Double, lon: Double) {
 *         viewModelScope.launch {
 *             val location = Location(name = name, latitude = lat, longitude = lon)
 *             repository.insert(location)  // ← Автоматически шифруется!
 *         }
 *     }
 * }
 */

/**
 * Как это работает под капотом
 * =============================
 *
 * 1. В AppModule.kt мы заменили обычные DAO на SecureDAO:
 *
 *    @Provides
 *    @Singleton
 *    fun provideTaskDao(
 *        db: AppDatabase,
 *        encryptionManager: EncryptionManager
 *    ): TaskDao = SecureTaskDao(db.taskDao(), encryptionManager)
 *
 * 2. SecureTaskDao является "обёрткой" (wrapper) вокруг обычного DAO:
 *
 *    - Когда Repository вызывает dao.insert(task)
 *    - SecureTaskDao перехватывает вызов
 *    - Шифрует чувствительные поля: title, description
 *    - Передаёт зашифрованный объект в обычный DAO
 *    - Обычный DAO сохраняет в БД
 *
 * 3. При чтении всё работает в обратном порядке:
 *
 *    - Обычный DAO читает зашифрованные данные из БД
 *    - SecureTaskDao перехватывает результат
 *    - Расшифровывает данные
 *    - Возвращает расшифрованный объект в Repository
 *
 * Архитектура:
 *
 *   ┌──────────────┐
 *   │ Repository   │
 *   └──────┬───────┘
 *          │
 *          ↓
 *   ┌──────────────┐     ┌─────────────────────────┐
 *   │ SecureTaskDao├────→│ EncryptionManager       │
 *   │              │     │ (шифрует/расшифровывает)│
 *   └──────┬───────┘     └─────────────────────────┘
 *          │
 *          ↓
 *   ┌──────────────┐
 *   │  TaskDao     │
 *   │ (обычный)    │
 *   └──────┬───────┘
 *          │
 *          ↓
 *   ┌──────────────┐
 *   │  Room БД     │
 *   └──────────────┘
 */

// На случай, если потребуется использовать EncryptionManager напрямую
// (например, для собственных нужд):

/**
 * Пример 5: Использование EncryptionManager напрямую (редко)
 * ===========================================================
 *
 * Если вам нужно зашифровать что-то за пределами DAO, вы можете:
 *
 * @HiltViewModel
 * class SomeViewModel @Inject constructor(
 *     private val encryptionManager: EncryptionManager
 * ) : ViewModel() {
 *
 *     fun encryptSensitiveData() {
 *         val plaintext = "sensitive data"
 *         val encrypted = encryptionManager.encrypt(plaintext)
 *         println("Encrypted: $encrypted")
 *
 *         val decrypted = encryptionManager.decrypt(encrypted)
 *         println("Decrypted: $decrypted")
 *     }
 * }
 *
 * ⚠️ Обычно вам НЕ нужно это делать!
 * Используйте встроенный слой безопасности вместо этого.
 */
