import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Run {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Добро пожаловать в шифратор.");
        System.out.println("Выберите опцию:");
        System.out.println("1. Зашифровать файл");
        System.out.println("2. Расшифровать файл");

        // считываем ввод с консоли
        String optionToExecute = readFromConsole();

        //запуск шифратора или дешифратора
        if(optionToExecute.equals("1")) {
            TextEncryptor textEncryptor = new TextEncryptor();
            textEncryptor.encrypt();

        } else if (optionToExecute.equals("2")) {
            TextDecryptor textDecryptor = new TextDecryptor();
            textDecryptor.decrypt();
        } else {
            System.out.println("Попробуйте еще раз.");
        }

    }

    // метод считывает введенную в консоль строку
    static String readFromConsole() {
        String optionToExecute = null;

        try {
            InputStreamReader readFromConsole = new InputStreamReader(System.in);
            BufferedReader bufferedReadFromConsole = new BufferedReader(readFromConsole);

            optionToExecute = bufferedReadFromConsole.readLine();
            return optionToExecute;
        } catch(IOException e) {
            System.out.println(e);
        }
        return optionToExecute;
    }

}
