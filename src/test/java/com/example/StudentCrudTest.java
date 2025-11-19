package com.example;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

/**
 * Pruebas unitarias para operaciones CRUD de Student con MySQL
 */
public class StudentCrudTest {
    private static SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;

    @BeforeClass
    public static void setupClass() {
        // Configurar SessionFactory para testing
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    @Before
    public void setup() {
        session = sessionFactory.openSession();
        transaction = session.beginTransaction();
        
        // Limpiar la tabla antes de cada prueba
        session.createMutationQuery("DELETE FROM Student").executeUpdate();
        transaction.commit();
        
        // Iniciar nueva transacción para el test
        transaction = session.beginTransaction();
    }

    @After
    public void tearDown() {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @AfterClass
    public static void tearDownClass() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    // ===== PRUEBAS CREATE =====

    @Test
    public void testCreateStudent() {
        // Crear un estudiante
        Student student = new Student("Juan Pérez", 20);
        session.persist(student);
        transaction.commit();

        // Verificar que se creó correctamente
        assertNotNull("El ID no debe ser nulo después de persistir", student.getId());
        assertTrue("El ID debe ser mayor que 0", student.getId() > 0);
    }

    @Test
    public void testCreateMultipleStudents() {
        // Crear múltiples estudiantes
        Student student1 = new Student("Ana García", 22);
        Student student2 = new Student("Carlos López", 25);
        Student student3 = new Student("María Rodríguez", 19);

        session.persist(student1);
        session.persist(student2);
        session.persist(student3);
        transaction.commit();

        // Verificar que todos tienen ID
        assertNotNull(student1.getId());
        assertNotNull(student2.getId());
        assertNotNull(student3.getId());
        
        // Verificar que los IDs son únicos
        assertNotEquals(student1.getId(), student2.getId());
        assertNotEquals(student2.getId(), student3.getId());
    }

    @Test
    public void testCreateStudentWithSpecialCharacters() {
        // Crear estudiante con caracteres especiales
        Student student = new Student("José Ángel O'Brien", 30);
        session.persist(student);
        transaction.commit();

        // Verificar que se guardó correctamente
        assertNotNull(student.getId());
        
        // Recuperar y verificar
        session.clear();
        Student retrieved = session.get(Student.class, student.getId());
        assertEquals("José Ángel O'Brien", retrieved.getName());
    }

    // ===== PRUEBAS READ =====

    @Test
    public void testReadStudent() {
        // Crear un estudiante
        Student student = new Student("Pedro Sánchez", 23);
        session.persist(student);
        transaction.commit();
        Long studentId = student.getId();

        // Limpiar caché
        session.clear();

        // Leer el estudiante
        Student retrieved = session.get(Student.class, studentId);

        // Verificar
        assertNotNull("El estudiante debe existir", retrieved);
        assertEquals("Pedro Sánchez", retrieved.getName());
        assertEquals(23, retrieved.getAge());
        assertEquals(studentId, retrieved.getId());
    }

    @Test
    public void testReadNonExistentStudent() {
        // Intentar leer un estudiante que no existe
        Student retrieved = session.get(Student.class, 99999L);

        // Verificar que retorna null
        assertNull("Debe retornar null para un ID inexistente", retrieved);
    }

    @Test
    public void testReadAllStudents() {
        // Crear varios estudiantes
        session.persist(new Student("Estudiante 1", 20));
        session.persist(new Student("Estudiante 2", 21));
        session.persist(new Student("Estudiante 3", 22));
        transaction.commit();

        // Leer todos los estudiantes
        Query<Student> query = session.createQuery("FROM Student", Student.class);
        List<Student> students = query.list();

        // Verificar
        assertEquals("Debe haber 3 estudiantes", 3, students.size());
    }

    // ===== PRUEBAS UPDATE =====

    @Test
    public void testUpdateStudentName() {
        // Crear un estudiante
        Student student = new Student("Nombre Original", 25);
        session.persist(student);
        transaction.commit();
        Long studentId = student.getId();

        // Actualizar el nombre
        transaction = session.beginTransaction();
        Student toUpdate = session.get(Student.class, studentId);
        toUpdate.setName("Nombre Actualizado");
        session.merge(toUpdate);
        transaction.commit();

        // Verificar actualización
        session.clear();
        Student updated = session.get(Student.class, studentId);
        assertEquals("Nombre Actualizado", updated.getName());
        assertEquals(25, updated.getAge()); // La edad no debe cambiar
    }

    @Test
    public void testUpdateStudentAge() {
        // Crear un estudiante
        Student student = new Student("Laura Martínez", 20);
        session.persist(student);
        transaction.commit();
        Long studentId = student.getId();

        // Actualizar la edad
        transaction = session.beginTransaction();
        Student toUpdate = session.get(Student.class, studentId);
        toUpdate.setAge(21);
        session.merge(toUpdate);
        transaction.commit();

        // Verificar actualización
        session.clear();
        Student updated = session.get(Student.class, studentId);
        assertEquals("Laura Martínez", updated.getName()); // El nombre no debe cambiar
        assertEquals(21, updated.getAge());
    }

    @Test
    public void testUpdateStudentNameAndAge() {
        // Crear un estudiante
        Student student = new Student("Roberto Gómez", 28);
        session.persist(student);
        transaction.commit();
        Long studentId = student.getId();

        // Actualizar nombre y edad
        transaction = session.beginTransaction();
        Student toUpdate = session.get(Student.class, studentId);
        toUpdate.setName("Roberto García");
        toUpdate.setAge(29);
        session.merge(toUpdate);
        transaction.commit();

        // Verificar actualización
        session.clear();
        Student updated = session.get(Student.class, studentId);
        assertEquals("Roberto García", updated.getName());
        assertEquals(29, updated.getAge());
    }

    @Test
    public void testUpdateNonExistentStudent() {
        // Intentar actualizar un estudiante que no existe
        Student nonExistent = session.get(Student.class, 99999L);
        
        // Verificar que no existe
        assertNull("El estudiante no debe existir", nonExistent);
        transaction.commit();
    }

    @Test
    public void testUpdateAllAges() {
        // Crear estudiantes
        session.persist(new Student("Estudiante 1", 20));
        session.persist(new Student("Estudiante 2", 25));
        session.persist(new Student("Estudiante 3", 30));
        transaction.commit();

        // Actualización masiva: incrementar todas las edades en 1
        session.clear();
        transaction = session.beginTransaction();
        int updatedCount = session.createMutationQuery(
            "UPDATE Student SET age = age + :increment")
            .setParameter("increment", 1)
            .executeUpdate();
        transaction.commit();

        // Verificar
        assertEquals("Deben actualizarse 3 estudiantes", 3, updatedCount);
        
        // Verificar que las edades se incrementaron
        session.clear();
        Query<Student> query = session.createQuery("FROM Student ORDER BY age", Student.class);
        List<Student> students = query.list();
        assertEquals(21, students.get(0).getAge());
        assertEquals(26, students.get(1).getAge());
        assertEquals(31, students.get(2).getAge());
    }

    // ===== PRUEBAS DELETE =====

    @Test
    public void testDeleteStudent() {
        // Crear un estudiante
        Student student = new Student("Estudiante a Eliminar", 24);
        session.persist(student);
        transaction.commit();
        Long studentId = student.getId();

        // Eliminar el estudiante
        transaction = session.beginTransaction();
        Student toDelete = session.get(Student.class, studentId);
        assertNotNull("El estudiante debe existir antes de eliminar", toDelete);
        session.remove(toDelete);
        transaction.commit();

        // Verificar que fue eliminado
        session.clear();
        Student deleted = session.get(Student.class, studentId);
        assertNull("El estudiante debe haber sido eliminado", deleted);
    }

    @Test
    public void testDeleteNonExistentStudent() {
        // Intentar eliminar un estudiante que no existe
        Student nonExistent = session.get(Student.class, 99999L);
        
        // Verificar que no existe
        assertNull("El estudiante no debe existir", nonExistent);
        
        // No debe lanzar excepción
        transaction.commit();
    }

    @Test
    public void testDeleteAllStudents() {
        // Crear varios estudiantes
        session.persist(new Student("Estudiante 1", 20));
        session.persist(new Student("Estudiante 2", 21));
        session.persist(new Student("Estudiante 3", 22));
        transaction.commit();

        // Eliminar todos
        transaction = session.beginTransaction();
        int deletedCount = session.createMutationQuery("DELETE FROM Student").executeUpdate();
        transaction.commit();

        // Verificar
        assertEquals("Deben eliminarse 3 estudiantes", 3, deletedCount);
        
        // Verificar que la tabla está vacía
        Query<Long> countQuery = session.createQuery("SELECT COUNT(s) FROM Student s", Long.class);
        Long count = countQuery.uniqueResult();
        assertEquals("No debe haber estudiantes", Long.valueOf(0), count);
    }

    // ===== PRUEBAS DE BÚSQUEDA =====

    @Test
    public void testSearchByNamePartial() {
        // Crear estudiantes
        session.persist(new Student("Juan Carlos", 20));
        session.persist(new Student("María Juan", 21));
        session.persist(new Student("Pedro López", 22));
        transaction.commit();

        // Buscar por nombre parcial
        Query<Student> query = session.createQuery(
            "FROM Student WHERE LOWER(name) LIKE LOWER(:name)", Student.class);
        query.setParameter("name", "%juan%");
        List<Student> results = query.list();

        // Verificar
        assertEquals("Debe encontrar 2 estudiantes", 2, results.size());
    }

    @Test
    public void testSearchByAgeRange() {
        // Crear estudiantes
        session.persist(new Student("Estudiante 1", 18));
        session.persist(new Student("Estudiante 2", 22));
        session.persist(new Student("Estudiante 3", 25));
        session.persist(new Student("Estudiante 4", 30));
        transaction.commit();

        // Buscar por rango de edad (20-26)
        Query<Student> query = session.createQuery(
            "FROM Student WHERE age BETWEEN :minAge AND :maxAge", Student.class);
        query.setParameter("minAge", 20);
        query.setParameter("maxAge", 26);
        List<Student> results = query.list();

        // Verificar
        assertEquals("Debe encontrar 2 estudiantes", 2, results.size());
    }

    @Test
    public void testFilterByAge() {
        // Crear estudiantes
        session.persist(new Student("Estudiante 1", 20));
        session.persist(new Student("Estudiante 2", 25));
        session.persist(new Student("Estudiante 3", 30));
        transaction.commit();

        // Filtrar mayores de 22
        Query<Student> query = session.createQuery(
            "FROM Student WHERE age > :age", Student.class);
        query.setParameter("age", 22);
        List<Student> results = query.list();

        // Verificar
        assertEquals("Debe encontrar 2 estudiantes", 2, results.size());
    }

    @Test
    public void testSortByName() {
        // Crear estudiantes
        session.persist(new Student("Carlos", 20));
        session.persist(new Student("Ana", 21));
        session.persist(new Student("Beatriz", 22));
        transaction.commit();

        // Ordenar por nombre ascendente
        Query<Student> query = session.createQuery(
            "FROM Student ORDER BY name ASC", Student.class);
        List<Student> results = query.list();

        // Verificar orden
        assertEquals("Ana", results.get(0).getName());
        assertEquals("Beatriz", results.get(1).getName());
        assertEquals("Carlos", results.get(2).getName());
    }

    @Test
    public void testSortByAgeDescending() {
        // Crear estudiantes
        session.persist(new Student("Estudiante 1", 20));
        session.persist(new Student("Estudiante 2", 25));
        session.persist(new Student("Estudiante 3", 22));
        transaction.commit();

        // Ordenar por edad descendente
        Query<Student> query = session.createQuery(
            "FROM Student ORDER BY age DESC", Student.class);
        List<Student> results = query.list();

        // Verificar orden
        assertEquals(25, results.get(0).getAge());
        assertEquals(22, results.get(1).getAge());
        assertEquals(20, results.get(2).getAge());
    }

    @Test
    public void testCountStudents() {
        // Crear estudiantes
        session.persist(new Student("Estudiante 1", 20));
        session.persist(new Student("Estudiante 2", 21));
        session.persist(new Student("Estudiante 3", 22));
        transaction.commit();

        // Contar estudiantes
        Query<Long> query = session.createQuery("SELECT COUNT(s) FROM Student s", Long.class);
        Long count = query.uniqueResult();

        // Verificar
        assertEquals("Debe haber 3 estudiantes", Long.valueOf(3), count);
    }

    // ===== PRUEBAS DE INTEGRIDAD =====

    @Test
    public void testCreateReadUpdateDeleteCycle() {
        // CREATE
        Student student = new Student("Ciclo Completo", 20);
        session.persist(student);
        transaction.commit();
        Long studentId = student.getId();
        assertNotNull("El estudiante debe tener un ID", studentId);

        // READ
        session.clear();
        transaction = session.beginTransaction();
        Student read = session.get(Student.class, studentId);
        assertNotNull("El estudiante debe existir", read);
        assertEquals("Ciclo Completo", read.getName());

        // UPDATE
        read.setName("Ciclo Actualizado");
        read.setAge(21);
        session.merge(read);
        transaction.commit();

        session.clear();
        transaction = session.beginTransaction();
        Student updated = session.get(Student.class, studentId);
        assertEquals("Ciclo Actualizado", updated.getName());
        assertEquals(21, updated.getAge());

        // DELETE
        session.remove(updated);
        transaction.commit();

        session.clear();
        Student deleted = session.get(Student.class, studentId);
        assertNull("El estudiante debe haber sido eliminado", deleted);
    }
}
