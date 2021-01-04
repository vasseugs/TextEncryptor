import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TextDecryptor {

    private char[] key; // переменная для хранения ключа, преобразованного в число

    public void decrypt() {
        System.out.println("Введите путь к файлу, который нужно расшифровать.");
        // переменная для хранения пути к файлу
        String fileName = readFromConsole();

        // проверяем этот путь на существование файла
        Path filePath = Paths.get(fileName);
        if(filePath.toFile().exists()) {
            System.out.println("Файл найден. ");
        } else {
            System.out.println("Проверьте правильность пути к файлу.");
            return;
        }

        // ввод ключа в консоль
        System.out.println("Введите восьмизначный ключ шифрования.");
        this.key = readFromConsole().toCharArray();
        System.out.println("Ключ принят.");
        System.out.println("Процедура расшифровки запущена, подождите...");

        /* считываем содержимое текстового файла, преобразовывем в UTF-8 по умолчанию;
        может случиться, что файл в другой кодировке, поэтому перестраховываемся */
        String fileContent = "";
        try {
            fileContent = Files.readString(filePath, StandardCharsets.UTF_8);
        } catch(IOException e) {
            e.printStackTrace();
        }

        // отправляем содержимое на расшифровку и получаем зашифрованное содержимое
        String decryptedContent = decryptingProcedure(fileContent);
        System.out.println("Расшифровка завершена:");
        System.out.println(decryptedContent);
    }

    private String decryptingProcedure(String stringFileContent) {

        //преобразуем содержимое файла в массив символов
        char[] encryptedCharContent = stringFileContent.toCharArray();

        // отправляем массив на расшифровку
        char[] decryptedCharContent = decryptingFunction(encryptedCharContent);

        return new String(decryptedCharContent);
    }

    private char[] decryptingFunction(char[] encryptedContent) {

        char[] decryptedContent; //переменная для расшифрованного содержимого

        /* если длина зашифрованного содержимого кратна ключу, то это значит,
        что последний расшифрованный символ будет указателем конца строки
        и покажет, столько заглушек было добавлено в последнем блоке к изначальному тексту */
        if((encryptedContent.length % key.length) == 0 ) {

            // если длина зашифрованного текста кратна длине ключа

            //расшифровываем текст
            decryptedContent = blockDecrypting(encryptedContent);

            //удаляем пустышки из расшифрованного текста
            decryptedContent = deleteDummies(decryptedContent);

        } else {

            // случай, когда признак конца строки находится в виде лишнего символа

            // удаляем признак конца строки
            encryptedContent = deleteEndOfStringSign(encryptedContent);

            // расшифровываем текст блочным механизмом
            decryptedContent = blockDecrypting(encryptedContent);
        }
        return decryptedContent;
    }

    // метод для чтения данных из консоли
    private String readFromConsole() {
        String consoleData = "";
        try {
            InputStreamReader readFromConsole = new InputStreamReader(System.in);
            BufferedReader bufferedReadFromConsole = new BufferedReader(readFromConsole);

            consoleData = bufferedReadFromConsole.readLine();
            return consoleData;
        } catch(IOException e) {
            System.out.println("Ошибка ввода-вывода");
        }

        return consoleData;
    }


    // метод для расшифровки подготовленного текста блочным механизмом
    private char[] blockDecrypting(char[] encryptedContent) {
        //создаем временное хранилище для дешифруемого участка длиной с ключ
        char[] tempStorage = new char[key.length];

        // создаем пустой массив для расшифрованного содержимого
        char[] decryptedContent = new char[encryptedContent.length];

            /* вычитаем из блоков по 8 символов ключ шифрования
            одна итерация - 8 символов */
        for(int encrIndex = 0; encrIndex < encryptedContent.length; encrIndex += 8) {

            // заполняем временный массив зашифрованным содержимым
            for(int storageIndex = 0; storageIndex < tempStorage.length; storageIndex++) {
                tempStorage[storageIndex] = encryptedContent[encrIndex+storageIndex];
            }

                /* посимвольно вычитаем из содержимого временного массива ключ
                и помещаем результат в итоговый массив */
            for(int resIndex = 0; resIndex < 8; resIndex++) {
                decryptedContent[encrIndex+resIndex] = (char) (tempStorage[resIndex] - key[resIndex]);
            }
        }

        return decryptedContent;
    }

    // метод для удаления пустышек в расшифрованном тексте
    private char[] deleteDummies(char[] decryptedContent) {

        /* выясняем количество пустышек. информацию об этом содержит
            последний расшифрованный символ, переведенный в целочисленное значение */
        int dummiesAmount = decryptedContent[decryptedContent.length - 1];

        // длина нового массива соответствует исходному тексту без пустышек
        char[] clearDecryptedContent = new char[decryptedContent.length - dummiesAmount];

        // переносим расшифрованное содержимое в новый массив
        for(int index = 0; index < clearDecryptedContent.length; index++) {
            clearDecryptedContent[index] = decryptedContent[index];
        }
        return clearDecryptedContent;
    }

    private char[] deleteEndOfStringSign(char[] encryptedContent) {

        //избавляемся от лишнего символа
        char[] clearEncryptedContent = new char[encryptedContent.length - 1];
        // в уменьшенный массив переносим все зашифрованное содержимое, кроме лишнего символа
        for(int index = 0; index < clearEncryptedContent.length; index++) {
            clearEncryptedContent[index] = encryptedContent[index];
        }

        return clearEncryptedContent;
    }

}
