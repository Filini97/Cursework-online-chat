package client;

import config.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Client2 {
    static final Config config = Config.getInstance();
    private  static  final String EXITCHAT = "/exit";
    private static Socket clientSocket = null;
    private static BufferedReader inMess;
    private static PrintWriter outMess;
    private static Scanner scannerConsole;

    public static void main(String[] args) throws IOException {
        clientSocket = new Socket(config.getHost(), config.getPort());
        outMess = new PrintWriter(clientSocket.getOutputStream(), true);
        inMess = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        scannerConsole = new Scanner(System.in);

        AtomicBoolean flag = new AtomicBoolean(true);

        // поток принимающий сообщения от сервера и печатающий в консоль
        new Thread(() -> {
            try {
                while (true) {
                    if (!flag.get()) {
                        inMess.close();
                        clientSocket.close();
                        break;
                    }
                    if (inMess.ready()) {
                        String messFormServer = inMess.readLine();
                        System.out.println(messFormServer);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }).start();

        // поток отправляет сообщения на сервер
        new Thread(() -> {
            System.out.println("Напишите своё имя");
            while (true) {
                if (scannerConsole.hasNext()) {
                    String mess = scannerConsole.nextLine(); //берем сообщение клиента с консоли
                    if (mess.equalsIgnoreCase(EXITCHAT)) {
                        outMess.println(mess);
                        scannerConsole.close();
                        outMess.close();
                        flag.set(false);
                        break;
                    }
                    outMess.println(mess); // отправляем серверу
                }
            }
        }).start();
    }
}
