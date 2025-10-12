import java.util.Scanner;

/**
 * Main — точка входа, меню для запуска блоков/заданий.
 */
public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("=== Программа: задания по Java (Блоки 1–5) ===");

            while (true) {
                System.out.println("\nГлавное меню:");
                System.out.println("1 — Блок 1 (Имена)");
                System.out.println("2 — Блок 2 (Человек)");
                System.out.println("3 — Блок 3 (Города)");
                System.out.println("4 — Блок 4 (Расширенные имена и люди)");
                System.out.println("5 — Блок 5 (Кот)");
                System.out.println("0 — Выход");

                int block = Tasks.readIntInRange(scanner, "Выберите блок (0–5): ", 0, 5);
                if (block == 0) {
                    System.out.println("Завершение работы. Пока!");
                    break;
                }

                switch (block) {
                    case 1 -> menuBlock1(scanner);
                    case 2 -> menuBlock2(scanner);
                    case 3 -> Tasks.task3Block3();
                    case 4 -> menuBlock4(scanner);
                    case 5 -> Tasks.task2Block5(scanner);
                    default -> System.out.println("Неверный выбор блока.");
                }
            }
        }
    }

    private static void menuBlock1(Scanner scanner) {
        System.out.println("\n--- Блок 1 (Имена) ---");
        System.out.println("3 — Создать три имени (жёстко заданные)");
        int task = Tasks.readIntInRange(scanner, "Выберите задание (3): ", 3, 3);
        if (task == 3) Tasks.task1Block1();
    }

    private static void menuBlock2(Scanner scanner) {
        System.out.println("\n--- Блок 2 (Человек) ---");
        System.out.println("2 — Человек с именем (использует объекты Name из Блока 1)");
        System.out.println("3 — Человек с отцом (формируем отчество и фамилию по отцу)");
        int task = Tasks.readIntInRange(scanner, "Выберите задание (2–3): ", 2, 3);
        if (task == 2) Tasks.task2Block2();
        else Tasks.task3Block2();
    }

    private static void menuBlock4(Scanner scanner) {
        System.out.println("\n--- Блок 4 (Расширенные имена и люди) ---");
        System.out.println("5 — Создание имён (разные варианты)");
        System.out.println("6 — Создание людей (разные варианты)");
        int task = Tasks.readIntInRange(scanner, "Выберите задание (5–6): ", 5, 6);
        if (task == 5) Tasks.task5Block4();
        else Tasks.task6Block4();
    }
}
