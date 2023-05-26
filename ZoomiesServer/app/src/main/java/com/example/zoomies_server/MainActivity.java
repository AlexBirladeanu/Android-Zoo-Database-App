package com.example.zoomies_server;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.zoomies_server.model.database.AppDatabase;
import com.example.zoomies_server.database.dto.AnimalDTO;
import com.example.zoomies_server.database.dto.GeneralDTO;
import com.example.zoomies_server.model.dto.UserDTO;
import com.example.zoomies_server.model.database.entity.UserRole;
import com.example.zoomies_server.model.dto.factory.AnimalDTOFactory;
import com.example.zoomies_server.model.dto.factory.UserDTOFactory;
import com.example.zoomies_server.viewmodel.MainViewModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ServerSocket serverSocket;
    Thread Thread1 = null;
    TextView tvIP, tvPort;
    TextView tvMessages;
    EditText etMessage;
    public static String SERVER_IP = "";
    public static final int SERVER_PORT = 8080;
    String message;

    public static AppDatabase database;
    private MainViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.Companion.getDatabase(this);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.initContext(this);

        tvIP = findViewById(R.id.tvIP);
        tvPort = findViewById(R.id.tvPort);
        tvMessages = findViewById(R.id.tvMessages);
        tvMessages.setMovementMethod(new ScrollingMovementMethod());
        etMessage = findViewById(R.id.etMessage);
        try {
            SERVER_IP = getLocalIpAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Thread1 = new Thread(new Connector());
        Thread1.start();
    }

    private String getLocalIpAddress() throws UnknownHostException {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        assert wifiManager != null;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipInt = wifiInfo.getIpAddress();
        return InetAddress.getByAddress(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(ipInt).array()).getHostAddress();
    }

    private PrintWriter output;
    private BufferedReader input;

    class Connector implements Runnable {
        @Override
        public void run() {
            Socket socket;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Not connected");
                        tvIP.setText("IP: " + SERVER_IP);
                        tvPort.setText("Port: " + String.valueOf(SERVER_PORT));
                    }
                });
                try {
                    socket = serverSocket.accept();
                    output = new PrintWriter(socket.getOutputStream());
                    input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvMessages.setText("Connected" + "\n" + "\n");
                        }
                    });

                    new Thread(new Sender(AppDatabase.Companion.getAnimals(), AppDatabase.Companion.getUsers())).start();

                    new Thread(new Receiver()).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Receiver implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    String message = input.readLine();
                    GeneralDTO transfer = (new Gson().fromJson(message, GeneralDTO.class));
                    String messageToDisplay = "";
                    if (transfer.getUserNameS() != null) {
                        UserDTO userDTO = UserDTOFactory.Companion.getInstance().createDTO(null, "", "", "", "", transfer.getUidS(), transfer.getUserNameS(), transfer.getPasswordS(), transfer.getRoleS(), transfer.getEmailS(), transfer.getPhoneNumberS());
                        if (transfer.getRequestDelete()) {
                            viewModel.deleteUser(userDTO);
                            messageToDisplay = "delete user:\n" + userDTO + "\n" + "\n";
                        } else {
                            viewModel.insertUser(userDTO);
                            messageToDisplay = "insert user:\n" + userDTO + "\n" + "\n";
                        }
                    } else {
                        AnimalDTO animalDTO = AnimalDTOFactory.Companion.getInstance().createDTO(transfer.getAnimalIdS(), transfer.getNameS(), transfer.getSpeciesS(), transfer.getHabitatS(), transfer.getDietS(), null, "", "", UserRole.EMPLOYEE, "", "");
                        if (transfer.getRequestDelete()) {
                            viewModel.deleteAnimal(animalDTO);
                            messageToDisplay = "delete animal:\n" + animalDTO + "\n" + "\n";
                        } else {
                            viewModel.insertAnimal(animalDTO);
                            messageToDisplay = "insert animal:\n" + animalDTO + "\n" + "\n";
                        }
                    }

                    if (message != null) {
                        String finalMessageToDisplay = messageToDisplay;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tvMessages.append("received package:" + finalMessageToDisplay + "\n" + "\n");
                            }
                        });
                    } else {
                        Thread1 = new Thread(new Connector());
                        Thread1.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Sender implements Runnable {
        List<AnimalDTO> animalDTOList;
        List<UserDTO> userDTOList;

        public Sender(List<AnimalDTO> animalDTOList, List<UserDTO> userDTOList) {
            this.animalDTOList = animalDTOList;
            this.userDTOList = userDTOList;
        }

        @Override
        public void run() {
            List<GeneralDTO> usersGeneralDTOS = new ArrayList<>(userDTOList);
            String serializedUsers = new Gson().toJson(usersGeneralDTOS);
            output.println(serializedUsers);

            List<GeneralDTO> animalsGeneralDTOS;
            if (animalDTOList == null) {
                animalsGeneralDTOS = new ArrayList<>();
            } else {
                animalsGeneralDTOS = new ArrayList<>(animalDTOList);
            }

            String serializedAnimals = new Gson().toJson(animalsGeneralDTOS);
            output.println(serializedAnimals);
            output.flush();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessages.append("sent package:" + "animals:\n" + animalDTOList + "\n");
                    tvMessages.append("sent package:" + "users:\n" + userDTOList + "\n");
                }
            });
        }
    }
}
