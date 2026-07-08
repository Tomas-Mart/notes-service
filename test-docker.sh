#!/bin/bash

BASE_URL="http://localhost:8181/api/notes"

echo "========================================="
echo "Тестирование API в Docker контейнере"
echo "========================================="

# 1. Создание
echo -e "\n✅ 1. Создание заметки..."
curl -X POST $BASE_URL \
  -H "Content-Type: application/json" \
  -d '{"title":"Docker тест","content":"Работает в контейнере","tags":["docker","success"]}'

# 2. Получение всех
echo -e "\n\n✅ 2. Получение всех заметок..."
curl $BASE_URL

# 3. Фильтрация
echo -e "\n\n✅ 3. Фильтрация по тегу 'docker'..."
curl "$BASE_URL?tag=docker"

# 4. Получение по ID
echo -e "\n\n✅ 4. Получение заметки по ID 1..."
curl $BASE_URL/1

# 5. Обновление
echo -e "\n\n✅ 5. Обновление заметки..."
curl -X PUT $BASE_URL/1 \
  -H "Content-Type: application/json" \
  -d '{"title":"Обновлено в Docker","content":"Новое содержимое","tags":["docker","updated"]}'

# 6. Удаление
echo -e "\n\n✅ 6. Удаление заметки..."
curl -X DELETE $BASE_URL/1

# 7. Проверка после удаления
echo -e "\n\n✅ 7. Все заметки после удаления..."
curl $BASE_URL

echo -e "\n\n========================================="
echo "✅ Все тесты пройдены успешно!"
echo "========================================="
