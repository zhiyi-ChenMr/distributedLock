package czy.com.zk.lock.test;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import czy.com.zk.lock.MyDistributedLock;

public class MyDistributedLockTest {
    public static void main(String[] args) {
       ZkClient zkClient = new ZkClient("127.0.0.1:2181", 5 * 10000);
       
       for (int i = 0; i < 20; i++) {
           String name = "thread" + i;
           Thread thread = new Thread(() -> {
               MyDistributedLock myDistributedLock = new MyDistributedLock(zkClient, name);
               myDistributedLock.lock();
               try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
               myDistributedLock.unlock();
           });
           thread.start();
       }
    }
}
