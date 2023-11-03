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
    static final Config config = Config.getInstance(); // Получение конфигурации сервера
    private static Map<Integer, User> users = new HashMap<>(); // Коллекция для хранения подключенных пользователей
    private static final Logger LOGGER = Logger.getLogger(); // Получение экземпляра логгера
    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(config.getPort())) {
            LOGGER.log("Start server"); // Запись в лог о запуске сервера
            System.out.println("Start server");
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println(("К чату подключился новый участник с портом: " + clientSocket.getPort()));
                    new Thread(() -> {
                        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) { // канал записи в сокет
                            User user = new User(clientSocket, out); // Создание объекта пользователя
                            users.put(clientSocket.getPort(), user); // Добавление пользователя в коллекцию
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            String name = in.readLine(); // Чтение имени пользователя
                            user.setName(name);// Присвоение имени для user
                            LOGGER.log("К чату подключился новый пользователь по имени " + '"' + user + '"' + " с портом номер: " + clientSocket.getPort());
                            sendMessToAll("К чату подключился новый пользователь: " + user);
                            waitMessAndSend(clientSocket, user.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
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
        }
    }

    public static void waitMessAndSend(Socket clientSocket, String name) {
        try (Scanner inMess = new Scanner(clientSocket.getInputStream())) {
            while (true) {
                if (inMess.hasNext()) {
                    String mess = inMess.nextLine();
                    switch (mess) {
                        case ("/exit"):
                            LOGGER.log("Пользователь по имени " + '"' + name + '"' + " (" + clientSocket.getPort() +")" + " покинул чат.");
                            sendMessToAll("Пользователь " + '"' + name + '"' + " покинул чат.");
                            break;
                        default:
                            LOGGER.log("Сообщение от пользователя " + '"' + name + '"' + " (" + clientSocket.getPort() +")" + ": " + mess);
                            sendMessToAll('"' + name + '"' + ": " + mess);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
