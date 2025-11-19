package com.example;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

public class Main extends JFrame {
    private JTextField txtId, txtName, txtAge;
    private JTable table;
    private DefaultTableModel tableModel;
    private final JButton btnCreate, btnRead, btnUpdate, btnDelete, btnRefresh;
    private final JButton btnSearchName, btnSearchAge, btnSort, btnCount, btnDeleteAll, btnUpdateAll, btnFilterAge;

    public Main() {
        setTitle("CRUD de Estudiantes con Hibernate");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel de formulario
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("ID:"));
        txtId = new JTextField();
        formPanel.add(txtId);

        formPanel.add(new JLabel("Nombre:"));
        txtName = new JTextField();
        formPanel.add(txtName);

        formPanel.add(new JLabel("Edad:"));
        txtAge = new JTextField();
        formPanel.add(txtAge);

        add(formPanel, BorderLayout.NORTH);

        // Tabla para mostrar estudiantes
        String[] columns = {"ID", "Nombre", "Edad"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtAge.setText(tableModel.getValueAt(row, 2).toString());
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de botones - Operaciones básicas
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel basicPanel = new JPanel(new FlowLayout());
        basicPanel.setBorder(BorderFactory.createTitledBorder("Operaciones Básicas"));
        btnCreate = new JButton("Crear");
        btnRead = new JButton("Leer");
        btnUpdate = new JButton("Actualizar");
        btnDelete = new JButton("Eliminar");
        btnRefresh = new JButton("Refrescar");

        btnCreate.addActionListener(e -> createStudent());
        btnRead.addActionListener(e -> readStudent());
        btnUpdate.addActionListener(e -> updateStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnRefresh.addActionListener(e -> refreshTable());

        basicPanel.add(btnCreate);
        basicPanel.add(btnRead);
        basicPanel.add(btnUpdate);
        basicPanel.add(btnDelete);
        basicPanel.add(btnRefresh);
        
        // Panel de búsquedas
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createTitledBorder("Búsquedas"));
        btnSearchName = new JButton("Buscar por Nombre");
        btnSearchAge = new JButton("Buscar por Rango Edad");
        btnFilterAge = new JButton("Filtrar por Edad");
        btnSort = new JButton("Ordenar");
        
        btnSearchName.addActionListener(e -> searchByName());
        btnSearchAge.addActionListener(e -> searchByAgeRange());
        btnFilterAge.addActionListener(e -> filterByAge());
        btnSort.addActionListener(e -> sortStudents());
        
        searchPanel.add(btnSearchName);
        searchPanel.add(btnSearchAge);
        searchPanel.add(btnFilterAge);
        searchPanel.add(btnSort);
        
        // Panel de operaciones masivas
        JPanel massPanel = new JPanel(new FlowLayout());
        massPanel.setBorder(BorderFactory.createTitledBorder("Operaciones Masivas"));
        btnCount = new JButton("Contar Total");
        btnDeleteAll = new JButton("Eliminar Todos");
        btnUpdateAll = new JButton("Incrementar Edades");
        
        btnCount.addActionListener(e -> countStudents());
        btnDeleteAll.addActionListener(e -> deleteAllStudents());
        btnUpdateAll.addActionListener(e -> updateAllAges());
        
        massPanel.add(btnCount);
        massPanel.add(btnDeleteAll);
        massPanel.add(btnUpdateAll);
        
        buttonPanel.add(basicPanel);
        buttonPanel.add(searchPanel);
        buttonPanel.add(massPanel);

        add(buttonPanel, BorderLayout.SOUTH);

        // Cargar datos iniciales
        refreshTable();
    }

    private void createStudent() {
        String name = txtName.getText().trim();
        String ageStr = txtAge.getText().trim();

        if (name.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete nombre y edad.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int age = Integer.parseInt(ageStr);
            Student student;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                student = new Student(name, age);
                session.persist(student);
                tx.commit();
            }

            JOptionPane.showMessageDialog(this, "Estudiante creado con ID: " + student.getId(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            refreshTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La edad debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException | HibernateException ex) {
            JOptionPane.showMessageDialog(this, "Error al crear estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void readStudent() {
        String idStr = txtId.getText().trim();

        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Long id = Long.valueOf(idStr);
            Student student;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                student = session.get(Student.class, id);
            }

            if (student != null) {
                txtName.setText(student.getName());
                txtAge.setText(String.valueOf(student.getAge()));
                JOptionPane.showMessageDialog(this, "Estudiante encontrado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No se encontró estudiante con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException | HibernateException ex) {
            JOptionPane.showMessageDialog(this, "Error al leer estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStudent() {
        String idStr = txtId.getText().trim();
        String name = txtName.getText().trim();
        String ageStr = txtAge.getText().trim();

        if (idStr.isEmpty() || name.isEmpty() || ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Long id = Long.valueOf(idStr);
            int age = Integer.parseInt(ageStr);

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction tx = session.beginTransaction();
                
                Student student = session.get(Student.class, id);
                if (student != null) {
                    student.setName(name);
                    student.setAge(age);
                    session.merge(student);
                    tx.commit();
                    JOptionPane.showMessageDialog(this, "Estudiante actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró estudiante con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID y edad deben ser números válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException | HibernateException ex) {
            JOptionPane.showMessageDialog(this, "Error al actualizar estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteStudent() {
        String idStr = txtId.getText().trim();

        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Long id = Long.valueOf(idStr);

            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar el estudiante con ID " + id + "?", 
                "Confirmar eliminación", 
                JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction tx = session.beginTransaction();
                    
                    Student student = session.get(Student.class, id);
                    if (student != null) {
                        session.remove(student);
                        tx.commit();
                        JOptionPane.showMessageDialog(this, "Estudiante eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "No se encontró estudiante con ID: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El ID debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException | HibernateException ex) {
            JOptionPane.showMessageDialog(this, "Error al eliminar estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);

        try {
            List<Student> students;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Student> query = session.createQuery("FROM Student", Student.class);
                students = query.list();
            }

            for (Student student : students) {
                tableModel.addRow(new Object[]{student.getId(), student.getName(), student.getAge()});
            }
        } catch (HibernateException ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar estudiantes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtAge.setText("");
    }
    
    // 1. Búsqueda por nombre (búsqueda parcial con LIKE)
    private void searchByName() {
        String searchName = JOptionPane.showInputDialog(this, "Ingrese el nombre (o parte del nombre) a buscar:");
        
        if (searchName == null || searchName.trim().isEmpty()) {
            return;
        }
        
        try {
            List<Student> students;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Student> query = session.createQuery(
                        "FROM Student WHERE LOWER(name) LIKE LOWER(:name)", Student.class);
                query.setParameter("name", "%" + searchName + "%");
                students = query.list();
            }
            
            tableModel.setRowCount(0);
            for (Student student : students) {
                tableModel.addRow(new Object[]{student.getId(), student.getName(), student.getAge()});
            }
            
            JOptionPane.showMessageDialog(this, 
                "Se encontraron " + students.size() + " estudiante(s).", 
                "Resultado", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException | HibernateException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al buscar estudiantes: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 2. Búsqueda por rango de edad
    private void searchByAgeRange() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField txtMinAge = new JTextField();
        JTextField txtMaxAge = new JTextField();
        
        panel.add(new JLabel("Edad mínima:"));
        panel.add(txtMinAge);
        panel.add(new JLabel("Edad máxima:"));
        panel.add(txtMaxAge);
        
        int result = JOptionPane.showConfirmDialog(this, panel, 
            "Buscar por Rango de Edad", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int minAge = Integer.parseInt(txtMinAge.getText().trim());
                int maxAge = Integer.parseInt(txtMaxAge.getText().trim());
                
                List<Student> students;
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Query<Student> query = session.createQuery(
                            "FROM Student WHERE age BETWEEN :minAge AND :maxAge", Student.class);
                    query.setParameter("minAge", minAge);
                    query.setParameter("maxAge", maxAge);
                    students = query.list();
                }
                
                tableModel.setRowCount(0);
                for (Student student : students) {
                    tableModel.addRow(new Object[]{student.getId(), student.getName(), student.getAge()});
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Se encontraron " + students.size() + " estudiante(s) entre " + minAge + " y " + maxAge + " años.", 
                    "Resultado", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Las edades deben ser números válidos.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            } catch (HeadlessException | HibernateException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al buscar estudiantes: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // 3. Listar ordenado
    private void sortStudents() {
        String[] options = {"Nombre (A-Z)", "Nombre (Z-A)", "Edad (Ascendente)", "Edad (Descendente)"};
        String choice = (String) JOptionPane.showInputDialog(this,
            "Seleccione el criterio de ordenamiento:",
            "Ordenar Estudiantes",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == null) {
            return;
        }
        
        try {
            List<Student> students;
            try (var session = HibernateUtil.getSessionFactory().openSession()) {
                String hql = "FROM Student ";
                 switch (choice) {
                    case "Nombre (A-Z)" -> hql += "ORDER BY name ASC";
                    case "Nombre (Z-A)" -> hql += "ORDER BY name DESC";
                    case "Edad (Ascendente)" -> hql += "ORDER BY age ASC";
                    case "Edad (Descendente)" -> hql += "ORDER BY age DESC";
                }
            Query<Student> query = session.createQuery(hql, Student.class);
            students = query.list();
            }
            
            tableModel.setRowCount(0);
            for (Student student : students) {
                tableModel.addRow(new Object[]{student.getId(), student.getName(), student.getAge()});
            }
            
            JOptionPane.showMessageDialog(this, 
                "Estudiantes ordenados por: " + choice, 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException | HibernateException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al ordenar estudiantes: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 4. Contar total de estudiantes
    private void countStudents() {
        try {
            Long count;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Long> query = session.createQuery("SELECT COUNT(s) FROM Student s", Long.class);
                count = query.uniqueResult();
            }
            
            JOptionPane.showMessageDialog(this, 
                "Total de estudiantes en la base de datos: " + count, 
                "Conteo Total", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException | HibernateException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al contar estudiantes: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // 5. Eliminar todos los estudiantes
    private void deleteAllStudents() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar TODOS los estudiantes?\nEsta acción no se puede deshacer.",
            "Confirmar Eliminación Masiva",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int deletedCount;
                try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                    Transaction tx = session.beginTransaction();
                    deletedCount = session.createMutationQuery("DELETE FROM Student").executeUpdate();
                    tx.commit();
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Se eliminaron " + deletedCount + " estudiante(s).", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                refreshTable();
            } catch (HeadlessException | HibernateException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar todos los estudiantes: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // 6. Actualización masiva (incrementar edad de todos)
    private void updateAllAges() {
        String input = JOptionPane.showInputDialog(this, 
            "Ingrese la cantidad de años a incrementar (puede ser negativo):");
        
        if (input == null || input.trim().isEmpty()) {
            return;
        }

        int increment = Integer.parseInt(input.trim());

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        Transaction tx = session.beginTransaction();
        
        int updatedCount = session.createMutationQuery(
            "UPDATE Student SET age = age + :increment")
            .setParameter("increment", increment)
            .executeUpdate();
        
        tx.commit();
        session.close();
        
        JOptionPane.showMessageDialog(this, 
            "Se actualizaron " + updatedCount + " estudiante(s).\nEdad incrementada en " + increment + " año(s).", 
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);
        refreshTable();
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, 
            "Debe ingresar un número válido.", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Error al actualizar edades: " + ex.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
    }
    
    // 7. Búsqueda de mayores/menores que cierta edad
    private void filterByAge() {
        String[] options = {"Mayores que", "Menores que", "Igual a"};
        String choice = (String) JOptionPane.showInputDialog(this,
            "Seleccione el tipo de filtro:",
            "Filtrar por Edad",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        if (choice == null) {
            return;
        }
        
        String ageInput = JOptionPane.showInputDialog(this, "Ingrese la edad:");
        
        if (ageInput == null || ageInput.trim().isEmpty()) {
            return;
        }
        
        try {
            int age = Integer.parseInt(ageInput.trim());
            String hql = "FROM Student WHERE ";
            
            switch (choice) {
                case "Mayores que" -> hql += "age > :age";
                case "Menores que" -> hql += "age < :age";
                case "Igual a" -> hql += "age = :age";
            }
            
            List<Student> students;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Student> query = session.createQuery(hql, Student.class);
                query.setParameter("age", age);
                students = query.list();
            }
            
            tableModel.setRowCount(0);
            for (Student student : students) {
                tableModel.addRow(new Object[]{student.getId(), student.getName(), student.getAge()});
            }
            
            JOptionPane.showMessageDialog(this, 
                "Se encontraron " + students.size() + " estudiante(s) " + choice.toLowerCase() + " " + age + " año(s).", 
                "Resultado", 
                JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "La edad debe ser un número válido.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException | HibernateException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al filtrar estudiantes: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
            
            // Cerrar Hibernate al cerrar la ventana
            app.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    HibernateUtil.shutdown();
                }
            });
        });
    }
}
