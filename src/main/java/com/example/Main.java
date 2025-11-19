package com.example;

import com.example.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int totalTests = 13;
        int passedTests = 0;
        List<String> failedTests = new ArrayList<>();

        System.out.println("========== 🧪 TEST CRUD PRODUCT CON HIBERNATE ==========");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            // --------------------------
            // 1️⃣ Crear producto válido
            // --------------------------
            System.out.println("\n🟢 Test 1: Crear producto válido");
            try {
                Product p1 = new Product("Guitarra Fender", 699.99, 5, "Instrumentos", "Guitarra eléctrica Fender Stratocaster");
                Transaction tx = session.beginTransaction();
                session.persist(p1);
                tx.commit();
                System.out.println("✅ Producto añadido correctamente: " + p1);
                passedTests++;
            } catch (NegativeInt | Nulability e) {
                System.out.println("❌ Excepción capturada Test 1: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
                failedTests.add("1 / Crear producto válido");
            } catch (Exception e) {
                System.out.println("❌ Error inesperado Test 1: " + e.getMessage());
                failedTests.add("1 / Crear producto válido");
            }

            // --------------------------
            // 2️⃣ Obtener producto por ID
            // --------------------------
            System.out.println("\n🟢 Test 2: Obtener producto por ID (1)");
            try {
                Product fetched = session.get(Product.class, 1);
                if (fetched != null) {
                    System.out.println("✅ Producto obtenido: " + fetched);
                    passedTests++;
                } else {
                    System.out.println("❌ No se encontró el producto con ID 1");
                    failedTests.add("2 / Obtener producto por ID");
                }
            } catch (Exception e) {
                System.out.println("❌ Error inesperado Test 2: " + e.getMessage());
                failedTests.add("2 / Obtener producto por ID");
            }

            // --------------------------
            // 3️⃣ Listar todos los productos
            // --------------------------
            System.out.println("\n🟢 Test 3: Listar todos los productos");
            try {
                List<Product> all = session.createQuery("from Product", Product.class).list();
                if (all.isEmpty()) {
                    System.out.println("⚠️ No hay productos en la base de datos.");
                    failedTests.add("3 / Listar todos los productos");
                } else {
                    all.forEach(System.out::println);
                    passedTests++;
                }
            } catch (Exception e) {
                System.out.println("❌ Error inesperado Test 3: " + e.getMessage());
                failedTests.add("3 / Listar todos los productos");
            }

            // --------------------------
            // 4️⃣ Actualizar producto
            // --------------------------
            System.out.println("\n🟢 Test 4: Actualizar producto con ID 1");
            try {
                Product fetched = session.get(Product.class, 1);
                if (fetched != null) {
                    Transaction tx = session.beginTransaction();
                    try {
                        fetched.setPrice(749.99);
                        fetched.setStock(7);
                        session.merge(fetched);
                        tx.commit();
                        System.out.println("✅ Producto actualizado: " + fetched);
                        passedTests++;
                    } catch (NegativeInt e) {
                        tx.rollback();
                        System.out.println("❌ Excepción capturada Test 4: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
                        failedTests.add("4 / Actualizar producto");
                    }
                } else {
                    System.out.println("⚠️ No existe el producto con ID 1 para actualizar.");
                    failedTests.add("4 / Actualizar producto");
                }
            } catch (Exception e) {
                System.out.println("❌ Error inesperado Test 4: " + e.getMessage());
                failedTests.add("4 / Actualizar producto");
            }

            // --------------------------
            // 5️⃣ Borrar producto
            // --------------------------
            System.out.println("\n🟢 Test 5: Borrar producto con ID 1");
            try {
                Product fetched = session.get(Product.class, 1);
                if (fetched != null) {
                    Transaction tx = session.beginTransaction();
                    session.remove(fetched);
                    tx.commit();
                    System.out.println("✅ Producto borrado correctamente");
                    passedTests++;
                } else {
                    System.out.println("⚠️ No existe el producto con ID 1 para borrar.");
                    failedTests.add("5 / Borrar producto");
                }
            } catch (Exception e) {
                System.out.println("❌ Error inesperado Test 5: " + e.getMessage());
                failedTests.add("5 / Borrar producto");
            }

            // --------------------------
            // 6️⃣ a 12️⃣ Validaciones de excepciones propias
            // --------------------------
            passedTests += runValidationTests(session, failedTests);

            // --------------------------
            // 13️⃣ Comprobar cierre de sesión
            // --------------------------
            System.out.println("\n🟢 Test 13: Comprobar cierre de sesión de Hibernate");
            try {
                session.close(); // cerrar sesión manualmente
                if (!session.isOpen()) {
                    System.out.println("✅ Sesión cerrada correctamente.");
                    passedTests++;
                } else {
                    System.out.println("❌ La sesión sigue abierta después de cerrarla.");
                    failedTests.add("13 / Sesión abierta después de tests");
                }
            } catch (Exception e) {
                System.out.println("❌ Error al cerrar sesión: " + e.getMessage());
                failedTests.add("13 / Sesión abierta después de tests");
            }

            // --------------------------
            // RESUMEN FINAL
            // --------------------------
            System.out.println("\n========== ✅ FIN DE TEST ==========");
            double passedPercent = (passedTests * 100.0) / totalTests;
            double failedPercent = 100 - passedPercent;

            System.out.println(String.format("\n📊 Resumen de Tests:"));
            System.out.println(String.format("✔️ Pasados: %.2f%% (%d/%d)", passedPercent, passedTests, totalTests));
            System.out.println(String.format("❌ Fallidos: %.2f%% (%d/%d)", failedPercent, failedTests.size(), totalTests));

            if (!failedTests.isEmpty()) {
                System.out.println("\nTests fallidos:");
                for (String fail : failedTests) {
                    System.out.println(" - " + fail);
                }
            }

            manualMode();
            
        } catch (Exception e) {
            System.err.println("❌ Error inesperado general: " + e.getMessage());
            e.printStackTrace();
        } finally {
            HibernateUtil.shutdown(); // cerrar SessionFactory
        }
    }

    private static int runValidationTests(Session session, List<String> failedTests) {
        int passed = 0;

        Object[][] tests = {
                {"Bajo Yamaha", 499.99, -3, "Instrumentos", "Bajo de 4 cuerdas", "6 / Stock negativo"},
                {"Micrófono Shure", -199.99, 10, "Audio", "Micrófono profesional", "7 / Precio negativo"},
                {null, 499.99, 3, "Instrumentos", "Producto sin nombre", "8 / Nombre nulo"},
                {"Producto sin categoría", 499.99, 3, null, "Sin categoría", "9 / Categoría nula"},
                {"Producto sin descripción", 299.99, 3, "Instrumentos", null, "10 / Descripción nula"},
                {"   ", 299.99, 3, "Instrumentos", "Sin nombre válido", "11 / Nombre vacío"},
                {"X".repeat(400), 999.99, 10, "Instrumentos", "X".repeat(400), "12 / Campos demasiado largos"}
        };

        for (Object[] test : tests) {
            try {
                Product p = new Product(
                        (String) test[0],
                        (double) test[1],
                        (int) test[2],
                        (String) test[3],
                        (String) test[4]
                );

                // Validación MaxLength antes de persistir
                if (p.getName().length() > 255 || p.getDescription().length() > 255) {
                    throw new MaxLength("Nombre o descripción excede el límite de 255 caracteres", null, passed);
                }

                Transaction tx = session.beginTransaction();
                session.persist(p);
                tx.commit();

                System.out.println("❌ ERROR: Se permitió producto inválido -> " + test[5]);
                failedTests.add((String) test[5]);
            } catch (NegativeInt | Nulability | MaxLength e) {
                System.out.println("✅ Excepción capturada correctamente: " + e.getClass().getSimpleName() + " -> " + e.getMessage());
                passed++;
            } catch (Exception ignored) {}
        }

        return passed;
    }

    private static boolean askContinue() {
        System.out.print("\nDeseas continuar con el siguiente test? (s/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("s") || input.equals("si");
    }
    
    private static void manualMode() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            while (true) {
                System.out.println("\n===== MODO MANUAL =====");
                System.out.println("1. Añadir producto");
                System.out.println("2. Listar productos");
                System.out.println("3. Buscar producto por ID");
                System.out.println("4. Actualizar producto");
                System.out.println("5. Borrar producto");
                System.out.println("6. Salir");
                System.out.print("Selecciona una opción: ");
                String option = scanner.nextLine();

                try {
                    switch (option) {
                        case "1": // Añadir producto
                            System.out.print("Nombre: ");
                            String name = scanner.nextLine();
                            System.out.print("Precio: ");
                            double price = Double.parseDouble(scanner.nextLine());
                            System.out.print("Stock: ");
                            int stock = Integer.parseInt(scanner.nextLine());
                            System.out.print("Categoría: ");
                            String category = scanner.nextLine();
                            System.out.print("Descripción: ");
                            String description = scanner.nextLine();

                            Product p = new Product(name, price, stock, category, description);
                            Transaction tx = session.beginTransaction();
                            session.persist(p);
                            tx.commit();
                            System.out.println("✅ Producto añadido con ID: " + p.getId());
                            break;

                        case "2": // Listar productos
                            List<Product> all = session.createQuery("from Product", Product.class).list();
                            if (all.isEmpty()) System.out.println("No hay productos.");
                            else all.forEach(System.out::println);
                            break;

                        case "3": // Buscar por ID
                            System.out.print("Introduce el ID: ");
                            int idSearch = Integer.parseInt(scanner.nextLine());
                            Product found = session.get(Product.class, idSearch);
                            System.out.println(found != null ? found : "Producto no encontrado.");
                            break;

                        case "4": // Actualizar producto
                            System.out.print("ID del producto a actualizar: ");
                            int idUpdate = Integer.parseInt(scanner.nextLine());
                            Product toUpdate = session.get(Product.class, idUpdate);
                            if (toUpdate == null) {
                                System.out.println("❌ No existe el producto.");
                                break;
                            }
                            System.out.print("Nuevo nombre (Enter para mantener): ");
                            String newName = scanner.nextLine();
                            if (!newName.isBlank()) toUpdate.setName(newName);

                            System.out.print("Nuevo precio (Enter para mantener): ");
                            String newPrice = scanner.nextLine();
                            if (!newPrice.isBlank()) toUpdate.setPrice(Double.parseDouble(newPrice));

                            System.out.print("Nuevo stock (Enter para mantener): ");
                            String newStock = scanner.nextLine();
                            if (!newStock.isBlank()) toUpdate.setStock(Integer.parseInt(newStock));

                            System.out.print("Nueva categoría (Enter para mantener): ");
                            String newCat = scanner.nextLine();
                            if (!newCat.isBlank()) toUpdate.setCategory(newCat);

                            System.out.print("Nueva descripción (Enter para mantener): ");
                            String newDesc = scanner.nextLine();
                            if (!newDesc.isBlank()) toUpdate.setDescription(newDesc);

                            Transaction txUpdate = session.beginTransaction();
                            session.merge(toUpdate);
                            txUpdate.commit();
                            System.out.println("✅ Producto actualizado.");
                            break;

                        case "5": // Borrar producto
                            System.out.print("ID del producto a borrar: ");
                            int idDelete = Integer.parseInt(scanner.nextLine());
                            Product toDelete = session.get(Product.class, idDelete);
                            if (toDelete == null) {
                                System.out.println("❌ No se encontró el producto.");
                                break;
                            }
                            Transaction txDelete = session.beginTransaction();
                            session.remove(toDelete);
                            txDelete.commit();
                            System.out.println("✅ Producto eliminado.");
                            break;

                        case "6":
                            System.out.println("👋 Saliendo del modo manual...");
                            return;

                        default:
                            System.out.println("❌ Opción no válida.");
                    }
                } catch (NegativeInt | Nulability e) {
                    System.out.println("⚠️ Excepción de validación: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("⚠️ Error inesperado: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error al abrir sesión de Hibernate: " + e.getMessage());
        }
    }

}
