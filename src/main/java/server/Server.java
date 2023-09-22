package server;

import config.Config;
import log.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Server {
    static final Config config = Config.getInstance();
    private static Map<Integer, User> users = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger();
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {
            LOGGER.log("Start server");
            System.out.println("Start server");
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOGGER.log("К чату подключился новый участник с портом: " + clientSocket.getPort());
                    System.out.println(("К чату подключился новый участник с портом: " + clientSocket.getPort()));
                    new Thread(() -> {
                        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) { // канал записи в сокет
                            User user = new User(clientSocket, out);
                            users.put(clientSocket.getPort(), user);
                            //присвоение имени для user
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            String name = in.readLine();
                            user.setName(name);
                            LOGGER.log("К чату подключился новый пользователь: " + user);
                            sendMessToAll("К чату подключился новый пользователь: " + user);
                            waitMessAndSend(clientSocket, user.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close(); // закрываем сокет клиента
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized void sendMessToAll(String mess) {
        for (Map.Entry<Integer, User> entry : users.entrySet()) {
            entry.getValue().sendMsg(mess);
            LOGGER.log("Отправлено новое сообщение");
            System.out.println("Отправлено новое сообщение");
        }
    }

    public static void waitMessAndSend(Socket clientSocket, String name) {
        try (Scanner inMess = new Scanner(clientSocket.getInputStream())) {
            while (true) {
                if (inMess.hasNext()) {
                    String mess = inMess.nextLine();
                    switch (mess) {
                        default:
                            LOGGER.log('"' + name + '"' + ": " + mess);
                            sendMessToAll('"' + name + '"' + ": " + mess);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
