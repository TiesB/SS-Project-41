/**
 * Created by Ties on 7-1-2016.
 */
package nl.tiesdavid.ssproject.online.clientside;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

//TODO: Remove.
public class TestClient {
    static class Reader extends Thread {
        private BufferedReader in;

        public Reader(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String line = in.readLine();
                    if (!line.equals("")) {
                        System.out.println(line);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                } catch (IOException e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 3339);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream()));

            new Reader(in).start();

            Scanner scanner = new Scanner(System.in);

            String line = scanner.nextLine();
            while (true) {
                if (!line.equals("") && !line.equals("exit")) {
                    out.write(line);
                    out.newLine();
                    out.flush();
                }
                System.out.println(in.readLine());
                line = scanner.nextLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
