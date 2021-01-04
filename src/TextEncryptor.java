import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TextEncryptor {

    private char[] key; // переменная для хранения ключа шифрования

    // метод для отправки файла на шифрование
    public void encrypt() {

        System.out.println("Введите путь к файлу, который нужно зашифровать.");
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
        System.out.println("Введите ключ шифрования.");
        this.key = readFromConsole().toCharArray();
        System.out.println("Ключ принят.");
        System.out.println("Процедура шифрования запущена, подождите...");


        /* считываем содержимое текстового файла, преобразовывем в UTF-8 по умолчанию;
        может случиться, что файл в другой кодировке, поэтому перестраховываемся */
        String fileContent = "";
        try {
            fileContent = Files.readString(filePath, StandardCharsets.UTF_8);
        } catch(IOException e) {
            e.printStackTrace();
        }

        // отправляем содержимое на шифрование и получаем зашифрованное содержимое
        String encryptedContent = encryptingProcedure(fileContent);

        /* результируем шифрование в файл */
        try {
            Path encodeTo = Paths.get("D:/encrypted.txt");

            if(Files.exists(encodeTo)) {
                Files.writeString(encodeTo,
                        encryptedContent,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE);
            } else {
                Files.writeString(encodeTo,
                        encryptedContent,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW,
                        StandardOpenOption.WRITE);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }

        System.out.println("Шифрование выполнено.");

    }

    //метод для шифрования содержимого файла
    private String encryptingProcedure(String stringFileContent) {
        //преобразуем содержимое файла в массив символов
        char[] charFileContent = stringFileContent.toCharArray();

        // пропускаем содержимое файла через функцию шифрования
        char[] encryptedCharContent = encryptingFunction(charFileContent);

        return new String(encryptedCharContent);
    }

    // функция для шифрования массива символов
    private char[] encryptingFunction(char[] originalContent) {

        char[] encryptedContent; // массив для зашифрованного текста

        if((originalContent.length % key.length) != 0) {

            /* если длина текста не кратна длине ключа, то добавляем
             конце массива нужно колчество символов-заглушек и признак конца
             строки, который при переводе в int показывает, сколько заглушек было
             вставлено
             */

            // делаем длину исходного текста кратной длине ключа
            char[] preparedContent = addDummies(originalContent);

            // применяем к исходному тексту блочное шифрование
            encryptedContent = blockEncrypting(preparedContent);

        } else {
            /* если содержимое файла кратно длине ключа, то шифруем файл
            и добавляем признак конца строки в конце в виде лишнего символа */

            // применяем к тексту блочное шифрование
            encryptedContent = blockEncrypting(originalContent);

            // добавялем признак конца строки
            encryptedContent = addEndOfStringSign(encryptedContent);
        }
        return encryptedContent;
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


    /* этот метод используется, если длина шифруемого текста не кратна
    длине ключа. Он дополняет имеющийся текст пустышками в конце настолько,
    чтобы текст стал кратным длине ключа. В конце текста помещается
    признак конца строки - символ, который при переводе в целочисленное
    значение указывает, сколько пустышек было помещено в исходный текст.
     */
    private char[] addDummies(char[] originalContent) {

        int keyLength = key.length; // длина ключа
        int originalLength = originalContent.length; // длина исходного текста

        /* переменная хранит количество элементов, которые нужно добавить в конец массива,
            чтобы его длина была кратной длине ключа */
        int leftToFill = (keyLength - (originalLength % keyLength));

        // создаем пустой массив по принципу "исходный массив" + количество недостающих элементов
        char[] addedOriginalContent = new char[originalContent.length + leftToFill];

        // заполняем новый массив содержимым исходного массива
        for(int index = 0; index < originalLength; index++) {
            addedOriginalContent[index] = originalContent[index];
        }

        // помещаем в массив признак конца строки с информацией о количестве заглушек
        char dummiesAmount = (char) leftToFill;
        addedOriginalContent[addedOriginalContent.length - 1] = dummiesAmount;

        // вычисляем, от элемента с каким индексом нужно заполнить массив пустышками
        int dummyStartIndex = addedOriginalContent.length - leftToFill;
        //заполняем новый массив в конце пустышками, кроме последнего (leftToFill -2)
        for(int index = 0; index < (leftToFill - 2); index++) {
            // пустышки
            addedOriginalContent[dummyStartIndex + index] = ' ';
            // добавляем признак конца строки с указанием количества пустышек
            addedOriginalContent[addedOriginalContent.length - 1] = (char) leftToFill;
        }
        return addedOriginalContent;
    }

    // метод для блочного шифрования подготовленного к шифрованию текста
    private char[] blockEncrypting(char[] preparedContent) {

        char[] tempStorage = new char[key.length]; //временное хранилище для шифруемого участка текста
        char[] encryptedContent = new char[preparedContent.length]; // хранилище для зашифрованного содержимого

        // складываем блок текста с ключом
        for(int blockIndex = 0; blockIndex < preparedContent.length; blockIndex += key.length) { // одна итерация - 8 символов

            //заполняем временный массив незашифрованным содержимым
            for(int storageIndex = 0; storageIndex < tempStorage.length; storageIndex++) // одна итерация - 1 символ заполняет временный массив
            {
                tempStorage[storageIndex] = preparedContent[blockIndex+storageIndex];
            }

            //складываем временный массив и ключ и помещаем результат в итоговый массив
            for(int sumIndex = 0; sumIndex < key.length; sumIndex++) {
                encryptedContent[blockIndex + sumIndex] = (char) (tempStorage[sumIndex] + key[sumIndex]);
            }
        }

        return encryptedContent;

    }

    /* добавление признака конца строки в зашифрованном тексте,
    если изначально текст был кратным длине ключа
     */
    private char[] addEndOfStringSign(char[] encryptedContent) {

        // увеличенный массив, чтобы вместился признак конца строки
        char[] addedEncryptedContent =  new char[encryptedContent.length + 1];

        //переносим в дополненный массив все зашифрованные элементы
        for(int index = 0; index < encryptedContent.length; index++) {
            addedEncryptedContent[index] = encryptedContent[index];
        }

        //помещаем заглушку в новый массив, ее тоже шифруем
        addedEncryptedContent[addedEncryptedContent.length - 1] = (char) ('*' + key[0]);

        return addedEncryptedContent;
    }




}
