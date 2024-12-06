package oblitusnumen.dbproject;

import java.util.Scanner;

public class Console {
    private Scanner scanner;
    private final Main main;

    public Console(Main main) {
        this.main = main;
    }

    public void start() {
        printHelp();
        try (Scanner scanner = new Scanner(System.in)) {
            this.scanner = scanner;
            l:while (scanner.hasNext()) {
                String next = scanner.next();
                switch (next) {
                    case "help" -> printHelp();
                    case "table" -> main.showTable();
                    case "stop" -> {
                        break l;
                    }
                    case "start" -> main.compute();
                    default -> System.out.println("Неизвестная команда. напишите help, чтобы посмотреть доступные команды");
                }
            }
            this.scanner = null;
        }
    }

    private void printHelp() {
        System.out.println("help - помощь");
        System.out.println("stop - завершить программу");
        System.out.println("table - показать таблицу");
        System.out.println("start - начать расчёт");
    }

    public String nextString() {// TODO: 12/5/24
        if (!scanner.hasNext()) throw new RuntimeException("Не удалось прочитать строку");
        System.out.println();
        return scanner.next();
    }

    public int nextInt() {// TODO: 12/5/24
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
