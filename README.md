# Hibernate Sample

Proyecto de ejemplo que demuestra el uso de **Hibernate ORM** para interactuar con una base de datos MySQL mediante Java.

## ¿Qué es Hibernate?

**Hibernate** es un framework ORM (Object-Relational Mapping) para Java que facilita la interacción entre aplicaciones orientadas a objetos y bases de datos relacionales. Permite:

- **Mapear clases Java a tablas de base de datos** sin escribir SQL manualmente
- **Gestionar automáticamente** las operaciones CRUD (Create, Read, Update, Delete)
- **Generar automáticamente** el esquema de la base de datos a partir de las clases
- **Abstraer las diferencias** entre distintos motores de bases de datos

## Estructura del Proyecto

```
Hibernate_Sample/
├── pom.xml                          # Configuración de Maven y dependencias
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── Main.java            # Punto de entrada de la aplicación
│   │   │   ├── Student.java         # Entidad JPA que representa un estudiante
│   │   │   └── HibernateUtil.java   # Utilidad para gestionar la SessionFactory
│   │   └── resources/
│   │       └── hibernate.cfg.xml    # Configuración de Hibernate
│   └── test/
│       └── java/com/example/
│           └── AppTest.java
└── target/                          # Archivos compilados (generados)
```

## ¿Qué hace el Main?

El archivo `Main.java` es el punto de entrada de la aplicación. Su función es:

1. **Abrir una sesión de Hibernate** utilizando `HibernateUtil`
2. **Iniciar una transacción** de base de datos
3. **Crear dos objetos `Student`**:
   - Juan Pérez, 20 años
   - María Gómez, 22 años
4. **Persistir los estudiantes** en la base de datos con `session.persist()`
5. **Confirmar la transacción** con `tx.commit()`
6. **Mostrar los IDs** generados automáticamente por la base de datos
7. **Cerrar la conexión** con `HibernateUtil.shutdown()`

```java
// Ejemplo simplificado
Student student1 = new Student();
student1.setName("Juan Pérez");
student1.setAge(20);

session.persist(student1);  // Se guarda en la BD
tx.commit();                 // Se confirma la transacción
```

## Acceso a la Base de Datos

### Configuración de Conexión

El acceso a la base de datos se configura en `src/main/resources/hibernate.cfg.xml`:

```xml
<property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/hibernate_db?createDatabaseIfNotExist=true</property>
<property name="hibernate.connection.username">root</property>
<property name="hibernate.connection.password">password</property>
```

**Parámetros de conexión:**
- **Host:** localhost
- **Puerto:** 3306 (puerto por defecto de MySQL)
- **Base de datos:** hibernate_db (se crea automáticamente si no existe)
- **Usuario:** root
- **Contraseña:** password

### Gestión de Sesiones

La clase `HibernateUtil` es responsable de:
- Crear la **SessionFactory** (fábrica de sesiones) al iniciar
- Proporcionar acceso a sesiones de Hibernate
- Cerrar correctamente la conexión al finalizar

```java
SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
Session session = sessionFactory.openSession();
```

### Entidad Student

La clase `Student.java` es una **entidad JPA** que se mapea a una tabla en la base de datos:

- `@Entity`: Indica que esta clase es una entidad de base de datos
- `@Id`: Marca el campo como clave primaria
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`: El ID se genera automáticamente

**Tabla generada en MySQL:**
```sql
CREATE TABLE Student (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    age INT
);
```

## Requisitos Previos

- **Java 22** o superior
- **Maven 3.x**
- **MySQL 8.x** en ejecución
- Base de datos MySQL con:
  - Usuario: `root`
  - Contraseña: `password`
  - (O modificar `hibernate.cfg.xml` con tus credenciales)

## Instalación y Ejecución

### 1. Clonar o descargar el proyecto

```bash
cd /Users/alex/Coding_Workbench/Hibernate_Sample
```

### 2. Asegurar que MySQL está en ejecución

```bash
# macOS
brew services start mysql

# O iniciar manualmente
mysql.server start
```

### 3. Compilar el proyecto

```bash
mvn clean compile
```

### 4. Ejecutar la aplicación

```bash
mvn exec:java
```

**Salida esperada:**
```
Hibernate: insert into Student (age, name) values (?, ?)
Estudiante guardado con ID: 1
Estudiante guardado con ID: 2
```

## Dependencias Principales

- **Hibernate Core 6.6.0** - Framework ORM
- **MySQL Connector 8.4.0** - Driver JDBC para MySQL
- **HSQLDB 2.7.1** - Base de datos en memoria (opcional, para testing)
- **JPA API 2.2** - API estándar de persistencia

## Configuración de Hibernate

Propiedades importantes en `hibernate.cfg.xml`:

| Propiedad | Valor | Descripción |
|-----------|-------|-------------|
| `hibernate.dialect` | `MySQLDialect` | Dialecto SQL específico de MySQL |
| `hibernate.hbm2ddl.auto` | `update` | Actualiza automáticamente el esquema de la BD |
| `show_sql` | `true` | Muestra las consultas SQL en consola |
| `format_sql` | `true` | Formatea las consultas SQL para mejor legibilidad |

### Valores de `hbm2ddl.auto`:
- **create**: Crea el esquema, destruyendo datos previos
- **create-drop**: Crea el esquema y lo elimina al cerrar
- **update**: Actualiza el esquema sin eliminar datos
- **validate**: Valida el esquema sin modificarlo
- **none**: No hace nada

## Verificar los Datos en MySQL

```bash
mysql -u root -p
```

```sql
USE hibernate_db;
SELECT * FROM Student;
```

**Resultado:**
```
+----+--------------+-----+
| id | name         | age |
+----+--------------+-----+
|  1 | Juan Pérez   |  20 |
|  2 | María Gómez  |  22 |
+----+--------------+-----+
```

## Conceptos Clave de Hibernate

### Session
Representa una conexión con la base de datos y gestiona el ciclo de vida de las entidades.

### Transaction
Agrupa operaciones que deben completarse de forma atómica (todas o ninguna).

### SessionFactory
Fábrica de sesiones, costosa de crear (se crea una vez y se reutiliza).

### Entidad
Clase Java que se mapea a una tabla de base de datos mediante anotaciones JPA.

## Comandos Útiles

```bash
# Compilar
mvn clean compile

# Ejecutar
mvn exec:java

# Ejecutar tests
mvn test

# Empaquetar JAR
mvn package
```

## Licencia

Proyecto de ejemplo con fines educativos.
