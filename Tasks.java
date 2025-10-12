import java.util.*;

/**
 * Tasks — содержит сущности (Name, Person, City, Cat), реестр имён и реализацию заданий.
 */
public class Tasks {

    // ---------------------------
    // Вспомогательные методы ввода
    // ---------------------------

    public static int readIntInRange(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v < min || v > max) {
                    System.out.println("Введите число в диапазоне от " + min + " до " + max + ".");
                    continue;
                }
                return v;
            } catch (NumberFormatException ex) {
                System.out.println("Неверный ввод: требуется целое число.");
            }
        }
    }

    public static int readPositiveInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v <= 0) {
                    System.out.println("Введите положительное целое число.");
                    continue;
                }
                return v;
            } catch (NumberFormatException ex) {
                System.out.println("Неверный ввод: требуется целое число.");
            }
        }
    }

    public static String readNonEmptyString(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) {
                System.out.println("Строка не может быть пустой.");
                continue;
            }
            return s;
        }
    }

    // ---------------------------
    // Gender — пол человека
    // ---------------------------

    public enum Gender { MALE, FEMALE }

    // ---------------------------
    // Реестр имён
    // ---------------------------

    public static class NameRegistry {
        private static final Map<String, Name> REG = new LinkedHashMap<>();

        public static void register(String key, Name name) {
            if (key == null || name == null) return;
            REG.put(key, name);
        }

        public static Name get(String key) { return REG.get(key); }

        public static boolean contains(String key) { return REG.containsKey(key); }

        public static void clear() { REG.clear(); }
    }

    // ---------------------------
    // Name — фамилия, имя, отчество
    // ---------------------------

    public static class Name {
        private String givenName;
        private String surname;
        private String patronymic;

        public Name(String givenName) { this(givenName, null, null); }
        public Name(String givenName, String surname) { this(givenName, surname, null); }
        public Name(String givenName, String surname, String patronymic) {
            this.givenName = normalize(givenName);
            this.surname = normalize(surname);
            this.patronymic = normalize(patronymic);
        }

        private static String normalize(String s) { return (s == null || s.trim().isEmpty()) ? null : s.trim(); }

        public String getGivenName() { return givenName; }
        public String getSurname() { return surname; }
        public String getPatronymic() { return patronymic; }

        public void setSurname(String surname) { this.surname = normalize(surname); }
        public void setPatronymic(String patronymic) { this.patronymic = normalize(patronymic); }

        @Override
        public String toString() {
            List<String> parts = new ArrayList<>();
            if (surname != null) parts.add(surname);
            if (givenName != null) parts.add(givenName);
            if (patronymic != null) parts.add(patronymic);
            return String.join(" ", parts);
        }
    }

    // ---------------------------
    // Person — человек
    // ---------------------------

    public static class Person {
        private Name name;
        private int height;
        private Gender gender;
        private Person father;

        // Конструкторы
        public Person(Name name, int height) { this(name, height, null, Gender.MALE); }
        public Person(Name name, int height, Person father) { this(name, height, father, Gender.MALE); }
        public Person(Name name, int height, Person father, Gender gender) {
            this.name = name != null ? name : new Name("");
            this.height = height;
            this.father = father;
            this.gender = gender;
            applyFatherDataIfNeeded();
        }

        public static Person fromNameStringAndHeight(String givenName, int height) {
            return new Person(new Name(givenName), height);
        }

        public static Person fromNameStringHeightAndFather(String givenName, int height, Person father) {
            return new Person(new Name(givenName), height, father);
        }

        public void setFather(Person father) {
            this.father = father;
            applyFatherDataIfNeeded();
        }

        private void applyFatherDataIfNeeded() {
            if (father == null) return;

            // Фамилия: если не задана, берём мужскую форму фамилии отца для мужчин, женскую — для женщин
            if (isNullOrEmpty(this.name.getSurname()) && !isNullOrEmpty(father.name.getSurname())) {
                this.name.setSurname(this.gender == Gender.MALE ? father.name.getSurname() : father.name.getSurname() + "а");
            }

            // Отчество
            if (isNullOrEmpty(this.name.getPatronymic()) && !isNullOrEmpty(father.name.getGivenName())) {
                this.name.setPatronymic(generatePatronymic(father.name.getGivenName(), this.gender));
            }
        }

        private static boolean isNullOrEmpty(String s) { return s == null || s.isEmpty(); }

        private static String generatePatronymic(String fatherGivenName, Gender gender) {
            if (fatherGivenName == null) return null;
            char lastChar = fatherGivenName.charAt(fatherGivenName.length() - 1);
            if (gender == Gender.MALE) {
                if (lastChar == 'й') return fatherGivenName.substring(0, fatherGivenName.length() - 1) + "евич";
                else return fatherGivenName + "ович";
            } else { // FEMALE
                if (lastChar == 'й') return fatherGivenName.substring(0, fatherGivenName.length() - 1) + "евна";
                else return fatherGivenName + "овна";
            }
        }

        public String toStringFull() { return name.toString(); }

        @Override
        public String toString() { return name.toString() + ", " + height; }
    }

    // ---------------------------
    // City
    // ---------------------------

    public static class City {
        private final String name;
        private final Map<City, Integer> neighbors = new LinkedHashMap<>();

        public City(String name) { this.name = (name == null) ? "" : name.trim(); }

        public void addPathTo(City to, int cost) {
            if (to == null || cost < 0) return;
            neighbors.put(to, cost);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            if (neighbors.isEmpty()) sb.append(" -> (нет путей)");
            else {
                sb.append(" -> ");
                List<String> parts = new ArrayList<>();
                for (Map.Entry<City, Integer> e : neighbors.entrySet()) {
                    parts.add(e.getKey().name + ":" + e.getValue());
                }
                sb.append(String.join(", ", parts));
            }
            return sb.toString();
        }
    }

    // ---------------------------
    // Cat
    // ---------------------------

    public static class Cat {
        private final String name;
        public Cat(String name) { this.name = (name == null || name.trim().isEmpty()) ? "(без имени)" : name.trim(); }
        public void meow() { System.out.println(name + ": мяу!"); }
        public void meow(int n) {
            if (n <= 0) { System.out.println(name + ": (молчание)"); return; }
            StringBuilder sb = new StringBuilder();
            sb.append(name).append(": ");
            for (int i = 0; i < n; i++) { sb.append("мяу"); if (i < n - 1) sb.append("-"); }
            sb.append("!");
            System.out.println(sb.toString());
        }
        @Override public String toString() { return "кот: " + name; }
    }

    // ---------------------------
    // Задания
    // ---------------------------

    public static void task1Block1() {
        System.out.println("\n[Блок 1.3] Создание имён.");
        Name cleo = new Name("Клеопатра");
        Name pushkin = new Name("Александр", "Пушкин", "Сергеевич");
        Name mayakov = new Name("Владимир", "Маяковский");

        NameRegistry.register("CLEOPATRA", cleo);
        NameRegistry.register("PUSHKIN_ALEXANDR_SERGEEVICH", pushkin);
        NameRegistry.register("MAYAKOV_VLADIMIR", mayakov);

        System.out.println(cleo);
        System.out.println(pushkin);
        System.out.println(mayakov);
    }

    public static void task2Block2() {
        System.out.println("\n[Блок 2.2] Человек с именем.");
        if (!NameRegistry.contains("CLEOPATRA")) task1Block1();
        Name cleo = NameRegistry.get("CLEOPATRA");
        Name pushkin = NameRegistry.get("PUSHKIN_ALEXANDR_SERGEEVICH");
        Name mayakov = NameRegistry.get("MAYAKOV_VLADIMIR");

        Person p1 = new Person(cleo, 152, null, Gender.FEMALE);
        Person p2 = new Person(pushkin, 167, null, Gender.MALE);
        Person p3 = new Person(mayakov, 189, null, Gender.MALE);

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
    }

    public static void task3Block2() {
        System.out.println("\n[Блок 2.3] Человек с родителем.");
        Name ivanName = new Name("Иван", "Чудов");
        Name petrName = new Name("Пётр", "Чудов");
        Name borisName = new Name("Борис");

        Person ivan = new Person(ivanName, 180, null, Gender.MALE);
        Person petr = new Person(petrName, 175, ivan, Gender.MALE);
        Person boris = new Person(borisName, 170, petr, Gender.MALE);

        System.out.println(ivan);
        System.out.println(petr);
        System.out.println(boris);
    }

    public static void task3Block3() {
        System.out.println("\n[Блок 3.3] Города.");
        City A = new City("A");
        City B = new City("B");
        City C = new City("C");
        City D = new City("D");
        City E = new City("E");
        City F = new City("F");

        A.addPathTo(B, 5); B.addPathTo(A, 5);
        B.addPathTo(C, 3); C.addPathTo(B, 3);
        C.addPathTo(D, 4); D.addPathTo(C, 4);
        A.addPathTo(D, 6); D.addPathTo(A, 6);
        F.addPathTo(E, 2); E.addPathTo(F, 2);
        D.addPathTo(E, 2);
        F.addPathTo(B, 1);
        A.addPathTo(F, 1);

        System.out.println(A); System.out.println(B); System.out.println(C);
        System.out.println(D); System.out.println(E); System.out.println(F);
    }

    public static void task5Block4() {
        System.out.println("\n[Блок 4.5] Создание имён (разные варианты).");
        Name n1 = new Name("Клеопатра");
        Name n2 = new Name("Сергей", "Пушкин", "Сергеевич");
        Name n3 = new Name("Владимир", "Маяковский");
        Name n4 = new Name("Христофор", "Бонифатьевич");

        System.out.println(n1);
        System.out.println(n2);
        System.out.println(n3);
        System.out.println(n4);
    }

    public static void task6Block4() {
        System.out.println("\n[Блок 4.6] Создание людей (разные варианты).");
        Person lev = Person.fromNameStringAndHeight("Лев", 170);
        System.out.println(lev);

        Name pushkinSergey = new Name("Сергей", "Пушкин");
        Person sergey = new Person(pushkinSergey, 168, lev, Gender.MALE);
        System.out.println(sergey);

        Person alex = Person.fromNameStringHeightAndFather("Александр", 167, sergey);
        System.out.println(alex);
    }

    public static void task2Block5(Scanner scanner) {
        System.out.println("\n[Блок 5.2] Кот.");
        Cat barsik = new Cat("Барсик");
        System.out.println("Создан: " + barsik);
        barsik.meow();
        int n = readPositiveInt(scanner, "Сколько раз Барсику мяукнуть? ");
        barsik.meow(n);
    }
}
