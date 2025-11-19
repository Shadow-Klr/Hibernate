# Pruebas Unitarias CRUD - Hibernate Sample

Este documento describe las pruebas unitarias implementadas para todas las operaciones CRUD del proyecto.

## Configuración

Las pruebas utilizan **MySQL en localhost** con una base de datos separada llamada `hibernate_test_db`.

### Configuración de Base de Datos para Testing

- **Base de datos**: `hibernate_test_db` (se crea automáticamente si no existe)
- **Usuario**: `root`
- **Contraseña**: `password`
- **Puerto**: `3306`
- **Estrategia**: `create-drop` (la base de datos se recrea antes de cada ejecución de pruebas)

### Configuración

La configuración de Hibernate para testing se encuentra en:
```
src/test/resources/hibernate.cfg.xml
```

## Ejecución de Pruebas

Para ejecutar todas las pruebas CRUD:

```bash
mvn test -Dtest=StudentCrudTest
```

Para ejecutar todas las pruebas del proyecto:

```bash
mvn test
```

## Pruebas Implementadas

### 📝 CREATE (3 pruebas)

1. **testCreateStudent**: Verifica la creación de un estudiante individual
   - Crea un estudiante
   - Verifica que se genera un ID automáticamente
   - Verifica que el ID es mayor que 0

2. **testCreateMultipleStudents**: Verifica la creación de múltiples estudiantes
   - Crea 3 estudiantes en la misma transacción
   - Verifica que todos tienen IDs generados
   - Verifica que los IDs son únicos

3. **testCreateStudentWithSpecialCharacters**: Verifica el manejo de caracteres especiales
   - Crea un estudiante con acentos y apóstrofes
   - Verifica que los caracteres se guardan correctamente

### 📖 READ (3 pruebas)

4. **testReadStudent**: Verifica la lectura de un estudiante por ID
   - Crea y persiste un estudiante
   - Lee el estudiante por ID
   - Verifica que los datos coinciden

5. **testReadNonExistentStudent**: Verifica el comportamiento al leer un estudiante inexistente
   - Intenta leer un ID que no existe (99999)
   - Verifica que retorna `null`

6. **testReadAllStudents**: Verifica la lectura de todos los estudiantes
   - Crea 3 estudiantes
   - Lee todos usando HQL
   - Verifica que se recuperan los 3

### ✏️ UPDATE (5 pruebas)

7. **testUpdateStudentName**: Verifica la actualización solo del nombre
   - Crea un estudiante
   - Actualiza únicamente el nombre
   - Verifica que solo el nombre cambió

8. **testUpdateStudentAge**: Verifica la actualización solo de la edad
   - Crea un estudiante
   - Actualiza únicamente la edad
   - Verifica que solo la edad cambió

9. **testUpdateStudentNameAndAge**: Verifica la actualización de nombre y edad
   - Crea un estudiante
   - Actualiza ambos campos
   - Verifica que ambos cambiaron

10. **testUpdateNonExistentStudent**: Verifica el comportamiento al actualizar un estudiante inexistente
    - Intenta obtener un estudiante con ID inexistente
    - Verifica que retorna `null`

11. **testUpdateAllAges**: Verifica la actualización masiva de edades
    - Crea 3 estudiantes con diferentes edades
    - Incrementa todas las edades en 1 usando HQL
    - Verifica que todas las edades se incrementaron correctamente

### 🗑️ DELETE (3 pruebas)

12. **testDeleteStudent**: Verifica la eliminación de un estudiante
    - Crea un estudiante
    - Lo elimina
    - Verifica que ya no existe en la base de datos

13. **testDeleteNonExistentStudent**: Verifica el comportamiento al eliminar un estudiante inexistente
    - Intenta obtener un estudiante con ID inexistente
    - Verifica que no lanza excepciones

14. **testDeleteAllStudents**: Verifica la eliminación masiva
    - Crea 3 estudiantes
    - Elimina todos usando HQL DELETE
    - Verifica que la tabla quedó vacía

### 🔍 BÚSQUEDAS Y CONSULTAS (6 pruebas)

15. **testSearchByNamePartial**: Verifica búsqueda por nombre parcial con LIKE
    - Crea estudiantes con nombres diferentes
    - Busca usando LIKE con comodines
    - Verifica que encuentra los correctos

16. **testSearchByAgeRange**: Verifica búsqueda por rango de edad
    - Crea estudiantes con diferentes edades
    - Busca usando BETWEEN
    - Verifica que solo encuentra los del rango

17. **testFilterByAge**: Verifica filtrado por edad
    - Crea estudiantes con diferentes edades
    - Filtra usando operador >
    - Verifica que solo encuentra los mayores

18. **testSortByName**: Verifica ordenamiento por nombre ascendente
    - Crea estudiantes en orden aleatorio
    - Ordena por nombre ASC
    - Verifica el orden correcto

19. **testSortByAgeDescending**: Verifica ordenamiento por edad descendente
    - Crea estudiantes en orden aleatorio
    - Ordena por edad DESC
    - Verifica el orden correcto

20. **testCountStudents**: Verifica el conteo de estudiantes
    - Crea 3 estudiantes
    - Usa COUNT en HQL
    - Verifica que retorna 3

### 🔄 INTEGRIDAD (1 prueba)

21. **testCreateReadUpdateDeleteCycle**: Verifica el ciclo completo CRUD
    - CREATE: Crea un estudiante
    - READ: Lee y verifica sus datos
    - UPDATE: Actualiza nombre y edad
    - DELETE: Elimina el estudiante
    - Verifica que cada operación funciona correctamente en secuencia

## Resultados

✅ **21 pruebas ejecutadas**
✅ **0 fallos**
✅ **0 errores**

## Estructura de las Pruebas

Cada prueba sigue el patrón AAA (Arrange-Act-Assert):

1. **Arrange**: Configuración inicial (crear datos de prueba)
2. **Act**: Ejecutar la operación a probar
3. **Assert**: Verificar los resultados

### Configuración de Testing

- `@BeforeClass`: Inicializa el `SessionFactory` una vez
- `@Before`: Limpia la base de datos antes de cada prueba
- `@After`: Cierra la sesión y hace rollback si es necesario
- `@AfterClass`: Cierra el `SessionFactory` al finalizar

## Operaciones Probadas del Main.java

Todas las operaciones CRUD implementadas en `Main.java` están cubiertas:

- ✅ `createStudent()` - línea 131
- ✅ `readStudent()` - línea 160
- ✅ `updateStudent()` - línea 188
- ✅ `deleteStudent()` - línea 225
- ✅ `refreshTable()` - línea 264 (operación READ ALL)
- ✅ `searchByName()` - línea 288
- ✅ `searchByAgeRange()` - línea 321
- ✅ `filterByAge()` - línea 520
- ✅ `sortStudents()` - línea 371
- ✅ `countStudents()` - línea 426
- ✅ `deleteAllStudents()` - línea 446
- ✅ `updateAllAges()` - línea 479

## Notas Importantes

1. **Aislamiento**: Cada prueba es independiente, la base de datos se limpia antes de cada test
2. **Transacciones**: Cada prueba usa su propia transacción
3. **Base de datos separada**: Se usa `hibernate_test_db` para evitar afectar datos de producción
4. **Estrategia create-drop**: El esquema se recrea automáticamente en cada ejecución

## Requisitos

- MySQL Server corriendo en localhost:3306
- Usuario `root` con contraseña `password` (o ajustar en `src/test/resources/hibernate.cfg.xml`)
- Maven para ejecutar las pruebas
