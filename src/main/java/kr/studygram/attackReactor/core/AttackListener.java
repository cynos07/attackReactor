package kr.studygram.attackReactor.core;

import kr.studygram.attackReactor.utils.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by cynos07 on 2017-05-15.
 */
public enum AttackListener implements Runnable{
    INSTANCE;
    private Logger logger;
    private Socket sock;
    private InetSocketAddress addr;

    private void initialize() {
        sock = new Socket();
        logger = Logger.getInstance();
    }

    @Override
    public void run() {
        initialize();
        while(true)
        {
            if(isAttack("naver.com") && isAttack("google.com")) {
                logger.log("ERROR", "어택이 유입되었습니다. 지금부터 아이피 변경을 시도합니다.");
                Thread changer = new Thread(MacAddressChanger.getInstance());
                changer.start();
                try {
                    changer.join();
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                logger.log("info", "인터넷이 연결되어있는 상태입니다.");
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isAttack(String site) {
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress(site,80);
        try {
            sock.connect(addr,3000);
            return false;
        } catch (IOException e) {
            return true;
        } finally {
            try {sock.close();}
            catch (IOException e) {}
        }
    }

    public static AttackListener getInstance()
    {
        return INSTANCE;
    }
}
