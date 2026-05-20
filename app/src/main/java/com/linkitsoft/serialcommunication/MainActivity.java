package com.linkitsoft.serialcommunication;

import android.os.Bundle;
import android.serialport.SerialPort;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG =
            "COMMANDS_SERIAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        // New Thread because serial communication can block freeze UI thread
        new Thread(() -> {

            try {

                Log.d(TAG, "Opening serial port...");

                SerialPort serialPort =
                        new SerialPort(
                                new File("/dev/ttyS0"),
                                9600
                        );

                Log.d(TAG, "Serial port opened");

                InputStream inputStream =
                        serialPort.getInputStream();

                OutputStream outputStream =
                        serialPort.getOutputStream();

                Log.d(TAG, "Streams acquired");

                /*
                 * CLEAR OLD UART DATA
                 */

                while (inputStream.available() > 0) {

                    inputStream.read();
                }

                Log.d(TAG,
                        "Old UART buffer cleared");

                /*
                 * CMD = 214
                 * queryTempAndDoorState
                 */

                int cmdCode = 214;

                byte address = 0x01;

                byte[] data = new byte[0];

                int dataLen = data.length;

                /*
                 * PACKET FORMAT:
                 *
                 * [ADDRESS]
                 * [0]
                 * [LENGTH]
                 * [CMD]
                 * [DATA]
                 * [CHECKSUM]
                 */

                byte[] packet =
                        new byte[dataLen + 5];

                packet[0] = address;

                packet[1] = 0x00;

                packet[2] =
                        (byte) (dataLen + 1);

                packet[3] =
                        (byte) cmdCode;

                /*
                 * COPY DATA PAYLOAD
                 */

                if (dataLen > 0) {

                    System.arraycopy(
                            data,
                            0,
                            packet,
                            4,
                            dataLen
                    );
                }

                /*
                 * XOR CHECKSUM
                 */

                int checksum = packet[0];

                for (int i = 1;
                     i <= dataLen + 3;
                     i++) {

                    checksum ^= packet[i];
                }

                packet[dataLen + 4] =
                        (byte) checksum;

                /*
                 * PRINT SENT PACKET
                 */

                StringBuilder sentPacket =
                        new StringBuilder();

                for (byte b : packet) {

                    sentPacket.append(
                            String.format(
                                    "%02X ",
                                    b
                            )
                    );
                }

                Log.d(TAG,
                        "Sending packet: "
                                + sentPacket);

                /*
                 * SEND PACKET
                 */

                outputStream.write(packet);

                outputStream.flush();

                Log.d(TAG,
                        "Packet sent");

                /*
                 * MCU PROCESSING DELAY
                 */

                Thread.sleep(100);

                /*
                 * WAIT FOR MCU RESPONSE
                 */

                int count = 0;

                while (count < 20) {

                    if (inputStream.available() > 0) {

                        byte[] buffer =
                                new byte[1024];

                        int size =
                                inputStream.read(buffer);

                        Log.d(TAG,
                                "Received bytes: "
                                        + size);

                        StringBuilder response =
                                new StringBuilder();

                        for (int i = 0;
                             i < size;
                             i++) {

                            response.append(
                                    String.format(
                                            "%02X ",
                                            buffer[i]
                                    )
                            );
                        }

                        Log.d(TAG,
                                "Response: "
                                        + response);

                        break;
                    }

                    Log.d(TAG,
                            "Waiting for MCU response...");

                    Thread.sleep(100);

                    count++;
                }

                if (count >= 20) {

                    Log.d(TAG,
                            "MCU response timeout");
                }

            } catch (Exception e) {

                Log.e(TAG,
                        "Serial communication error",
                        e);
            }

        }).start();
    }
}
//Flow
/*
Android App
↓
UART (ttyS0)
↓
MCU
↓
Hardware Action
↓
MCU Response
↓
UART
↓
Android App*/
