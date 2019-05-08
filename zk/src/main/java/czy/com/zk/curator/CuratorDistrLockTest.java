package czy.com.zk.curator;

import java.util.concurrent.TimeUnit;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.RetryNTimes;

import czy.com.zk.lock.MyDistributedLock;

public class CuratorDistrLockTest {
    /** Zookeeper info */
    private static final String ZK_ADDRESS = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
    private static final String ZK_LOCK_PATH = "/zktest01";
    
    public static void main(String[] args) {
        // 1.Connect to zk
        CuratorFramework client = CuratorFrameworkFactory.newClient(
                ZK_ADDRESS,
                new RetryNTimes(10, 5000)
        );
        client.start();
        System.out.println("zk client1 start successfully!");
        for (int i = 0; i < 20; i++) {
            Thread thread = new Thread(() -> {
                doWithLock(client);
            }, "client1 - thread" + i);
            thread.start();
        }
        
        /*while(true) {
            System.out.println("--------------curr  Active Thread Count" + Thread.currentThread().getThreadGroup().activeCount());
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }*/
    }
    
    private static void doWithLock(CuratorFramework client) {
        InterProcessMutex lock = new InterProcessMutex(client, ZK_LOCK_PATH);
        try {
            if (lock.acquire(10 * 1000, TimeUnit.SECONDS)) {
                System.out.println(Thread.currentThread().getName() + " hold lock");
                Thread.sleep(2000L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                lock.release();
                System.out.println(Thread.currentThread().getName() + " release lock");
                //System.out.println("curr  Active Thread Count" + Thread.currentThread().getThreadGroup().activeCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
