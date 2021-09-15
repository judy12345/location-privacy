package com.smutsx.lbs.LBS.server.socket;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Collection;

import static com.smutsx.lbs.LBS.server.socket.SocketHandler.*;
@Slf4j
@Data
public class ClientSocket implements Runnable {

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private String key;
    private String message;

    @Override
    public void run() {
        while (true){
            try {
                onMessage(this);
                System.out.println(LocalDateTime.now()+"当前设备:"+this.key+" 接收到数据: <<<<<<" + this.message);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (isSocketClosed(this)){
                System.out.println("客户端已关闭,其Key值为："+ this.getKey());
                //关闭对应的服务端资源
                close(this);
                break;
            }
        }
    }




    public void setSocket(Socket sock) {
        this.socket = sock;
    }
    public void setInputStream(DataInputStream input) {
        this.inputStream = input;
    }
    public void setOutputStream(DataOutputStream ouput) {
        this.outputStream = ouput;
    }

    public String getKey() {
        return this.key;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setKey(String s) {
        this.key = s;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void setMessage(String info) {
        this.message = info;
    }

    public Socket getSocket() {
        return  this.socket;
    }
}