import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;


/**
 * JAVA非阻塞IO
 * 通过加入线程的概念，让socket server能够在应用层面，
 * 通过非阻塞的方式同时处理多个socket套接字
 */
public class SocketServer4 {

    static {
        BasicConfigurator.configure();
    }

    private static Object xWait = new Object();

    /**
     * 日志
     */
    private static final Log LOGGER = LogFactory.getLog(SocketServer4.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(83);
            serverSocket.setSoTimeout(100);

            while(true){
                //这里JAVA通过JNI请求操作系统，并一直等待操作系统返回结果（或者出错）
                Socket socket = null;

                try{
                    socket = serverSocket.accept();
                }catch (SocketTimeoutException e){
                    //===========================================================
                    //      执行到这里，说明本次accept没有接收到任何数据报文
                    //      主线程在这里就可以做一些事情，记为X
                    //===========================================================
                    synchronized(SocketServer4.xWait){
//                        SocketServer4.LOGGER.info("这次没有从底层接收到任务数据报文，等待10毫秒，模拟事件X的处理时间");
                        SocketServer4.xWait.wait(10);
                    }
                    continue;
                }

                //当然业务处理过程可以交给一个线程（这里可以使用线程池）,并且线程的创建是很耗资源的。
                //最终改变不了.accept()只能一个一个接受socket连接的情况
                SocketServerThread socketServerThread = new SocketServerThread(socket);
                new Thread(socketServerThread).start();
            }
        }catch (Exception e){
            SocketServer4.LOGGER.error(e.getMessage(), e);
        }finally {
            if(serverSocket != null){
                serverSocket.close();
            }
        }
    }

}

class SocketServerThread implements Runnable{

    private static final Log LOGGER = LogFactory.getLog(SocketServerThread.class);

    private Socket socket;

    public SocketServerThread(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        //下面我们收取信息（这里还是阻塞式的,一直等待，直到有数据可以接受）
        InputStream in = null;
        OutputStream out = null;
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
            Integer sourcePort = socket.getPort();
            int maxLen = 2048;
            byte[] contextBytes = new byte[maxLen];
            int realLen;
            StringBuffer message = new StringBuffer();
            //下面我们收取信息（设置成非阻塞方式，这样read信息的时候，又可以做一些其他事情）
            socket.setSoTimeout(10);

            BIORead:while(true){
                try{
                    while((realLen = in.read(contextBytes, 0, maxLen)) != -1){
                        message.append(new String(contextBytes, 0, realLen));
                        /*
                         * 我们假设读取到“over”关键字，
                         * 表示客户端的所有信息在经过若干次传送后，完成
                         * */
                        if(message.indexOf("over") != -1){
                            break BIORead; //跳出到BIORead
                        }
                    }
                }catch (SocketTimeoutException e){
                    //===========================================================
                    //      执行到这里，说明本次read没有接收到任何数据流
                    //      主线程在这里又可以做一些事情，记为Y
                    //===========================================================
//                    SocketServerThread.LOGGER.info("这次没有从底层接收到任务数据报文，等待10毫秒，模拟事件Y的处理时间");
                    continue;
                }

            }

            //下面打印信息
            Long threadId = Thread.currentThread().getId();
            SocketServerThread.LOGGER.info("服务器(线程：" + threadId + ")收到来自于端口：" + sourcePort + "的信息：" + message);
            //下面开始发送信息
            out.write("回发响应信息！".getBytes());

            //关闭
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
