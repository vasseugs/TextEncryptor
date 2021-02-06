import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Run {

    public static void main(String[] args) throws InterruptedException {

        System.out.println("Welcome to the text encryptor.");
        System.out.println("Choose the option:");
        System.out.println("1. Encrypt the file");
        System.out.println("2. Decrypt the file");

        String optionToExecute = readFromConsole();

        // run encryptor or decryptor depending on chose option
        if(optionToExecute.equals("1")) {
            TextEncryptor textEncryptor = new TextEncryptor();
            textEncryptor.encrypt();
        } else if (optionToExecute.equals("2")) {
            TextDecryptor textDecryptor = new TextDecryptor();
            textDecryptor.decrypt();
        } else {
            System.out.println("You have chosen non existing option. Try again.");
        }
    }

    // this method reads a line from console
    static String readFromConsole() {
        String optionToExecute = "";

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
