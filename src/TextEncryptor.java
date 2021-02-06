import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TextEncryptor {

    private char[] key;

    // a method to send the file to encryption
    public void encrypt() {

        System.out.print("Enter the file path: ");

        // checking if the file exists
        Path filePath = Paths.get(readFromConsole());
        if(filePath.toFile().exists()) {
            System.out.println("File found.");
        } else {
            System.out.println("File not found. Check if the file path is correct.");
            return;
        }

        System.out.print("Enter the path where to encrypt your file: ");
        Path encodeTo = Paths.get(readFromConsole());

        // entering the key
        System.out.println("Enter the encryption key.");
        this.key = readFromConsole().toCharArray();
        System.out.println("Key accepted.");
        System.out.println("Encryption procedure started, please wait...");

        /* reading file contents and transforming into UTF-8 in case if it has
        other encoding */
        String fileContent = "";
        try {
            fileContent = Files.readString(filePath, StandardCharsets.UTF_8);
        } catch(IOException e) {
            e.printStackTrace();
        }

        // sending the content for encryption and receiving the encrypted content
        String encryptedContent = encryptingProcedure(fileContent);

        //writing encrypted content to a file
        try {
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

        System.out.println("Encryption completed.");

    }

    //a method that manages the entire decryption procedure
    private String encryptingProcedure(String stringFileContent) {
        //transform file contents into char array
        char[] charFileContent = stringFileContent.toCharArray();

        // sending char array to encryption
        char[] encryptedCharContent = encryptingFunction(charFileContent);

        return new String(encryptedCharContent);
    }

    /* encryption procedure carcass. this method chooses option to execute
    depending on the length of the encrypted content and the encryption key */
    private char[] encryptingFunction(char[] originalContent) {

        char[] encryptedContent;

        if((originalContent.length % key.length) != 0) {

            /* if the text length is not a multiple of the key length,
            then add the required number of dummy characters at the end of
            the array and a the end of string sign which, being
            converted to an integer, tells how many
            dummy characters were put
            */

            // making content multiple to key length by adding dummy characters
            char[] preparedContent = addDummies(originalContent);

            // encrypting prepared content
            encryptedContent = blockEncrypting(preparedContent);

        } else {
            /* if the content of the file is a multiple of the key length,
            then we encrypt the file and add an end of string sign
            at the end as an extra character */

            // encrypting the content
            encryptedContent = blockEncrypting(originalContent);

            // adding the end of string sign
            encryptedContent = addEndOfStringSign(encryptedContent);
        }
        return encryptedContent;
    }

    // a method to read lines from console
    private String readFromConsole() {
        String consoleData = "";
        try {
            InputStreamReader readFromConsole = new InputStreamReader(System.in);
            BufferedReader bufferedReadFromConsole = new BufferedReader(readFromConsole);

            consoleData = bufferedReadFromConsole.readLine();
            return consoleData;
        } catch(IOException e) {
            System.out.println("I/O Error");
        }

        return consoleData;
    }


    /* we use this method if the length of encrypting text is not multiple to
    key length. The method adds required number of dummy characters at the end of string
    to make it multiple to the key length. At the end of string we put
    the end of string sign - a dummy character that, being converted to integer,
    tells how many dummy characters were put.
     */
    private char[] addDummies(char[] originalContent) {

        int keyLength = key.length;
        int originalLength = originalContent.length; // the original text length

        /* this variable contains the number of dummy characters to add at the
        end of the array to make it multiple to key length
        */
        int leftToFill = (keyLength - (originalLength % keyLength));

        //creating an empty array from the original + number of
        // dummies left to add
        char[] addedOriginalContent = new char[originalContent.length + leftToFill];

        // fill the new array with the contents of the original array
        for(int index = 0; index < originalLength; index++) {
            addedOriginalContent[index] = originalContent[index];
        }

        // putting to the array the end of string sign that contains
        // information of number of added dummies
        char dummiesAmount = (char) leftToFill;
        addedOriginalContent[addedOriginalContent.length - 1] = dummiesAmount;

        // calculate from the element with what index
        // the array should be filled with dummies
        int dummyStartIndex = addedOriginalContent.length - leftToFill;
        // filling the end of new array with dummies, except the
        // end of sting sing (leftToFill -2)
        for(int index = 0; index < (leftToFill - 2); index++) {
            // dummies are space symbols
            addedOriginalContent[dummyStartIndex + index] = ' ';
            // adding the end of string sign that contains a number of dummies
            addedOriginalContent[addedOriginalContent.length - 1] = (char) leftToFill;
        }
        return addedOriginalContent;
    }

    // a method for encrypting prepared text
    private char[] blockEncrypting(char[] preparedContent) {

        char[] tempStorage = new char[key.length]; // temporal storage for the part of text being encrypted
        char[] encryptedContent = new char[preparedContent.length]; // storage for the encrypted content

        // add a block of text with a key
        for(int blockIndex = 0; blockIndex < preparedContent.length; blockIndex += key.length) {

            // filling temporal storage with the block of original content
            for(int storageIndex = 0; storageIndex < tempStorage.length; storageIndex++) // одна итерация - 1 символ заполняет временный массив
            {
                tempStorage[storageIndex] = preparedContent[blockIndex+storageIndex];
            }

            // adding temporal storage and the key and putting the result to the final array
            for(int sumIndex = 0; sumIndex < key.length; sumIndex++) {
                encryptedContent[blockIndex + sumIndex] = (char) (tempStorage[sumIndex] + key[sumIndex]);
            }
        }

        return encryptedContent;
    }

    // a method to end of string sign
    private char[] addEndOfStringSign(char[] encryptedContent) {

        // augmented array. the last index is for the end of string sign
        char[] addedEncryptedContent =  new char[encryptedContent.length + 1];

        // transfer encrypted content to the augmented array
        for(int index = 0; index < encryptedContent.length; index++) {
            addedEncryptedContent[index] = encryptedContent[index];
        }

        // putting the end of string sign and also encrypt it
        addedEncryptedContent[addedEncryptedContent.length - 1] = (char) ('*' + key[0]);

        return addedEncryptedContent;
    }
}
