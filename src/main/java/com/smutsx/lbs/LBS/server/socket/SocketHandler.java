package com.smutsx.lbs.LBS.server.socket;

import com.alibaba.druid.util.StringUtils;
import com.smutsx.lbs.LBS.controller.LbsProfileController;
import com.smutsx.lbs.LBS.mapper.WaypointMapper;
import com.smutsx.lbs.LBS.server.AlgorithmSelect;
import com.smutsx.lbs.LBSApplication;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Date;

import static com.smutsx.lbs.LBS.server.socket.SocketPool.add;
import static com.smutsx.lbs.LBS.server.socket.SocketPool.remove;
@Slf4j
public class SocketHandler {


    /**
     * 将连接的Socket注册到Socket池中
     * @param socket
     * @return
     */
    public static ClientSocket register(Socket socket){
        ClientSocket clientSocket = new ClientSocket();
        clientSocket.setSocket(socket);
        try {
            clientSocket.setInputStream(new DataInputStream(socket.getInputStream()));
            clientSocket.setOutputStream(new DataOutputStream(socket.getOutputStream()));
            Date date = new Date();

            clientSocket.setKey(date.toString());
            add(clientSocket);
            return clientSocket;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 向指定客户端发送信息
     * @param clientSocket
     * @param message
     */

    public static void sendMessage(ClientSocket clientSocket, String message){
        try {
            System.out.println("发送消息到客户端  : >>>>>" + message);
            clientSocket.getOutputStream().write(message.getBytes("utf-8"));
            //clientSocket.getOutputStream().writeUTF(message);
        } catch (IOException e) {
            System.out.println("发送信息异常：{}"+e);
            close(clientSocket);
        }
    }

    /**
     * 获取指定客户端的上传信息
     * @param clientSocket
     * @return
     */
    public static void onMessage(ClientSocket clientSocket){
        byte[] keyByte = new byte[1024];
        byte[] msgByte = new byte[1];
        String message;
        LbsProfileController temp  = (LbsProfileController) LBSApplication.getBean(LbsProfileController.class);
        try {
            // 第一次先发送序列号
            if(StringUtils.isEmpty(clientSocket.getKey())) {
                clientSocket.getInputStream().read(keyByte);
                clientSocket.setKey(new String(keyByte, "UTF-8"));
                clientSocket.setMessage(clientSocket.getKey());
                temp.getsendMessage(clientSocket.getMessage());
                message= temp.getsendMessage(clientSocket.getMessage()).toString();
            }else {
                String info = "";
                while (true) {
                    if (clientSocket.getInputStream().available() > 0) {
                        clientSocket.getInputStream().read(msgByte);
                        String tempStr = HexEcodeUtil.ByteArrayToHexStr(msgByte);
                        info += tempStr;

                        //已经读完
                        if (clientSocket.getInputStream().available() == 0) {
                            //重置,不然每次收到的数据都会累加起来
                            clientSocket.setMessage(new String(HexEcodeUtil.HexStrToByteArray(info)));
                            message= temp.getsendMessage(clientSocket.getMessage()).toString();
                            break;
                        }
                    }
                }
                //return clientSocket.getMessage();
            }
            sendMessage(clientSocket,message);

        } catch (IOException e) {
            e.printStackTrace();
            close(clientSocket);
        }
        //return null;
    }

    /**
     * 指定Socket资源回收
     * @param clientSocket
     */
    public static void close(ClientSocket clientSocket){
        System.out.println("进行资源回收");
        if (clientSocket != null){
            System.out.println("开始回收socket相关资源，其Key为"+ clientSocket.getKey());
            remove(clientSocket.getKey());
            Socket socket = clientSocket.getSocket();
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
            } catch (IOException e) {
                System.out.println("关闭输入输出流异常，"+e);
            }finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("关闭socket异常"+ e);
                }
            }
        }
    }


    /**
     * 发送数据包，判断数据连接状态
     * @param clientSocket
     * @return
     */
    public static boolean isSocketClosed(ClientSocket clientSocket){
        try {
            System.out.println("关闭");
            clientSocket.getSocket().sendUrgentData(1);
            return false;
        } catch (IOException e) {
            return true;
        }
    }
}
