import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class TextDecryptor {

    private char[] key;

    public void decrypt() {
        System.out.print("Enter the path to the file you want to decrypt: ");

        // check if the file exists
        Path filePath = Paths.get(readFromConsole());
        if(filePath.toFile().exists()) {
            System.out.println("File found.");
        } else {
            System.out.println("File not found. Check if the file path is correct.");
            return;
        }

        System.out.print("Enter the path where to decrypt your file: ");
        Path decryptTo = Paths.get(readFromConsole());

        // entering the encryption key
        System.out.println("Enter the encryption key.");
        this.key = readFromConsole().toCharArray();
        System.out.println("Key accepted.");
        System.out.println("Decryption procedure started, please wait...");

        /* reading file contents and transforming into UTF-8 in case if it has
        other encoding */
        String stringFileContent = "";
        try {
            stringFileContent = Files.readString(filePath, StandardCharsets.UTF_8);
        } catch(IOException e) {
            e.printStackTrace();
        }

        // sending the content for decryption and receiving the decrypted content
        String decryptedContent = decryptingProcedure(stringFileContent);
        System.out.println("Decryption finished. Go check decrypted file");

        // writing decrypted content to a file
        try {
            if(Files.exists(decryptTo)) {
                Files.writeString(decryptTo,
                        decryptedContent,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.TRUNCATE_EXISTING,
                        StandardOpenOption.WRITE);
            } else {
                Files.writeString(decryptTo,
                        decryptedContent,
                        StandardCharsets.UTF_8,
                        StandardOpenOption.CREATE_NEW,
                        StandardOpenOption.WRITE);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // a method that manages the entire decryption procedure
    private String decryptingProcedure(String stringFileContent) {

        //transform file contents into char array
        char[] encryptedCharContent = stringFileContent.toCharArray();

        // sending char array to decryption
        char[] decryptedCharContent = decryptingFunction(encryptedCharContent);

        return new String(decryptedCharContent);
    }

    /* decryption procedure carcass. this method chooses option to execute
    depending on the length of the encrypted content and the encryption key */
    private char[] decryptingFunction(char[] encryptedContent) {

        char[] decryptedContent;

        /* if the length of the encrypted content is a multiple of the key,
        then the last decrypted character will represent the end of the string and
        will contain the number of dummy characters were put in the text when
         you convert it into integer */
        if((encryptedContent.length % key.length) == 0 ) {

            // the case when the length of the encrypted content is a multiple of the key

            // decrypting the text
            decryptedContent = blockDecrypting(encryptedContent);

            // deleting dummies from it
            decryptedContent = deleteDummies(decryptedContent);

        } else {

            // the case when the end of string sign is in the form of an extra character

            // deleting the end of string sign
            encryptedContent = deleteEndOfStringSign(encryptedContent);

            // decrypting the text
            decryptedContent = blockDecrypting(encryptedContent);
        }
        return decryptedContent;
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


    // a method to decrypt prepared text
    private char[] blockDecrypting(char[] encryptedContent) {
        //create a temporary storage for the decrypted section with the length of the key
        char[] tempStorage = new char[key.length];

        // empty array for the decrypted content
        char[] decryptedContent = new char[encryptedContent.length];

            /* subtract the encryption key from blocks of encrypted text.
            the size of one iteration equals to the key length  */
        for(int blockIndex = 0; blockIndex < encryptedContent.length; blockIndex += key.length) {

            // filling temporary storage with encrypted content
            for(int storageIndex = 0; storageIndex < tempStorage.length; storageIndex++) {

                /* if the key is entered incorrectly, an error may occur
                 overestimating the index in the temporary storage
                 */
                if ((blockIndex + storageIndex) < encryptedContent.length) {
                    tempStorage[storageIndex] = encryptedContent[blockIndex+storageIndex];
                }
            }

                /* subtract the key from the contents of the temporary array character by character
                 and put the result into the final array */
            for(int storageIndex = 0; storageIndex < tempStorage.length; storageIndex++) {

                /* if the key is entered incorrectly, an error may occur
                 overestimating the index in the encrypted text array
                 */
                if((blockIndex + storageIndex) < encryptedContent.length) {
                    decryptedContent[blockIndex+storageIndex] = (char) (tempStorage[storageIndex] - key[storageIndex]);

                }
            }
        }

        return decryptedContent;
    }

    // a method to delete dummy characters in the decrypted content
    private char[] deleteDummies(char[] decryptedContent) {

        /* find out the number of dummy characters. it is found in the last
        decrypted character converted to integer value */
        int dummiesAmount = decryptedContent[decryptedContent.length - 1];

        // the length of the new array matches the original text length without dummy symbols
        char[] clearDecryptedContent = new char[decryptedContent.length - dummiesAmount];

        // transfer the decrypted content to a new array
        for(int index = 0; index < clearDecryptedContent.length; index++) {
            clearDecryptedContent[index] = decryptedContent[index];
        }
        return clearDecryptedContent;
    }

    // a method to delete the extra character that represents the end of the string
    private char[] deleteEndOfStringSign(char[] encryptedContent) {

        // get rid of extra character
        char[] clearEncryptedContent = new char[encryptedContent.length - 1];
        // transfer encrypted content to a new array, without the extra character
        for(int index = 0; index < clearEncryptedContent.length; index++) {
            clearEncryptedContent[index] = encryptedContent[index];
        }

        return clearEncryptedContent;
    }

}
