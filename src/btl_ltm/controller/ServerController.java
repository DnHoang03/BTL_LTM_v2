/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package btl_ltm.controller;

import btl_ltm.dao.RoomDAO;
import btl_ltm.dao.UserDAO;
import btl_ltm.entity.Room;
import btl_ltm.entity.User;
import btl_ltm.entity.UserLogin;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author duckii
 */
public class ServerController {

    private ServerSocket myServer;
    private int serverPort = 8888;
    private UserDAO userDao;
    private static List<Socket> clientSockets = new ArrayList<>();
    private Queue<ObjectOutputStream> findingUser = new LinkedList<ObjectOutputStream>();
    
    public ServerController() {
        userDao = new UserDAO();
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("Server is listening on port " + serverPort);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                // Thêm client vào danh sách
                synchronized (clientSockets) {
                    clientSockets.add(clientSocket);
                }

                // Tạo một luồng mới để xử lý client này
                new ClientHandler(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Lớp này sẽ xử lý mỗi kết nối client
    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectInputStream out;
        private ObjectOutputStream in;
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
        
        @Override
        public void run() {
            try {
                // Khởi tạo các luồng I/O
                out = new ObjectInputStream(clientSocket.getInputStream());
                in = new ObjectOutputStream(clientSocket.getOutputStream());
                
                // Đọc và xử lý yêu cầu từ client
                String eventName = (String) out.readObject();           
                switch (eventName) {
                    case "login":
                        Object o = out.readObject();
                        handleEventLogin(o, in);
                        break;
                    case "register":
                        Object o1 = out.readObject();
                        handleEventRegister(o1, in);
                        break;
                    case "getRanks":
                        getListRanks(in);
                        break;
                    case "findGame":
                        User user = (User)out.readObject();
                        findingUser.add(in);
                        RoomDAO roomDao = new RoomDAO();
                        if(findingUser.size() == 1) {
                            Integer currentRoomID = roomDao.insertRoom();
                            userDao.updateRoomId(user.getId(), (user.getMatch() == null) ? 1:(user.getMatch()+1), currentRoomID);
                            findingUser.peek().writeObject(currentRoomID);
                            findingUser.peek().flush();
                            findingUser.poll();
                        }
                        break;
                    case "endGame":
                        String username = (String)out.readObject();
                        int point = (int)out.readObject();
                        int time = (int)out.readObject();
                        int roomId = (int)out.readObject();
                        handleEventEndgame(username, time, point, roomId, in);
                        break;
                    case "getWinner":
                        int rId = (int)out.readObject();
                        handeEventGetWinner(rId, in);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServerController.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                // Đóng kết nối khi client ngắt kết nối
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                synchronized (clientSockets) {
                    clientSockets.remove(clientSocket);
                }
                System.out.println("Client disconnected: " + clientSocket.getInetAddress());
            }
        }
    }

    private void handleEventLogin(Object o, ObjectOutputStream oos) throws IOException {
        UserLogin user = (UserLogin) o;
        User result = userDao.getUserByUserNamePassword(user.getUser(), user.getPassword());
        System.out.println(result != null ? result : null);
        oos.writeObject(result != null ? result : null);
        oos.flush();
    }

    private void handleEventRegister(Object o, ObjectOutputStream oos) throws IOException {
        UserLogin user = (UserLogin) o;
        User result = userDao.getUserByUserNamePassword(user.getUser(), user.getPassword());
        if (result != null) {
            oos.writeObject(null);
            System.out.println( "Username đã tồn tại!");
            oos.flush();
        } else {
            userDao.InsertUser(user.getUser(), user.getPassword());
            System.out.println("ok");
            oos.writeObject(userDao.getUserByUserNamePassword(user.getUser(), user.getPassword()));
            oos.flush();
        }
    }
    
    private void handleEventEndgame(String username, int time, int point, int roomId, ObjectOutputStream inp) {
        try {
            RoomDAO roomDao = new RoomDAO();
            Room room = roomDao.getRoomById(roomId);
            UserDAO userDao = new UserDAO();
            if((room.getWinner() == null || room.getBestScore() < point)
                || (room.getBestScore() == point && room.getTime() > time)    ) {
                int total = (room.getTotalCompleted()==null)?1:(room.getTotalCompleted()+1);
                roomDao.updateRoom(room.getId(), username, point,time, total);
                room.setBestScore(point);
                room.setTime(time);
                room.setWinner(username);
                room.setTotalCompleted(total);
                if(total == 1) {
                    User user = userDao.getUserByUsername(room.getWinner());
                    userDao.updateScore(user.getId(), (user.getScore() == null)?1:(user.getScore()+1));
                }
            }
            inp.writeObject("end");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void getListRanks(ObjectOutputStream oos) throws IOException {  
        try{
            List<User> result = userDao.GetRanks();
            oos.writeObject(result);
            oos.flush();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void openServer(int portNumber) {
        try {
            myServer = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handeEventGetWinner(int roomId, ObjectOutputStream in) throws IOException {
        RoomDAO roomDao = new RoomDAO();
        Room room = roomDao.getRoomById(roomId);
        in.writeObject(room.getWinner());
        in.flush();
    }
}
