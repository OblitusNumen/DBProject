package oblitusnumen.dbproject;

import java.util.Scanner;

public class Console {
    private final Main main;
    private Scanner scanner;

    public Console(Main main) {
        this.main = main;
    }

    public void start() {
        printHelp();
        try (Scanner scanner = new Scanner(System.in)) {
            this.scanner = scanner;
            l:
            while (scanner.hasNext()) {
                String next = scanner.next();
                switch (next) {
                    case "help" -> printHelp();
                    case "table" -> main.showTable();
                    case "stop" -> {
                        break l;
                    }
                    case "data" -> main.openData();
                    case "start" -> main.compute();
                    default ->
                            System.out.println("Неизвестная команда. напишите help, чтобы посмотреть доступные команды");
                }
            }
            this.scanner = null;
        }
    }

    private void printHelp() {
        System.out.println("help - помощь");
        System.out.println("stop - завершить программу");
        System.out.println("table - показать таблицу");
        System.out.println("data - показать таблицу c расчётами");
        System.out.println("start - начать расчёт");
    }

    public String nextString() {
        if (!scanner.hasNext()) throw new RuntimeException("Не удалось прочитать строку");
        System.out.println();
        return scanner.next();
    }

    @SafeVarargs
    public final <T> T getChoice(String msg, T... options) {
        return options[getChoiceIndex(msg, options)];
    }

    @SafeVarargs
    public final <T> int getChoiceIndex(String msg, T... options) {
        System.out.println(msg);
        int i;
        for (i = 0; i < options.length; i++) {
            T option = options[i];
            System.out.println((i + 1) + ". " + option);
        }
        while (true) {
            i = nextInt();
            if (i <= options.length && i >= 1) {
                return i - 1;
            }
            System.out.println("Выбранный вариант отсутствует. Попробуйте ещё раз.");
        }
    }

    public int nextInt() {
        while (!scanner.hasNextInt()) {
            System.out.println();
            scanner.next();
            System.out.println("Не удалось считать число");
        }
        System.out.println();
        return scanner.nextInt();
    }

    public double nextDouble() {
        while (!scanner.hasNextDouble()) {
            System.out.println();
            scanner.next();
            System.out.println("Не удалось считать число");
        }
        System.out.println();
        return scanner.nextDouble();
    }
}
