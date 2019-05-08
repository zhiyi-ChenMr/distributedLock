package czy.com.zk.lock;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

public class MyDistributedLock {

    private ZkClient zkClient;
    
    private String name;
    
    private String currLockPath;
    
    private CountDownLatch countDownLatch;
    
    private static final String PARENT_LOCK_PATH = "/distribute_lock";
    
    public MyDistributedLock(ZkClient zkClient, String name) {
        this.zkClient = zkClient;
        this.name = name;
    }
    
    public void lock() {
        //判断父节点是否存在
        if (!zkClient.exists(PARENT_LOCK_PATH)) {
            try {
                zkClient.createPersistent(PARENT_LOCK_PATH);
            } catch (Exception e) {
                
            }
        }
        //创建当前目录下的临时有序节点
        currLockPath = zkClient.createEphemeralSequential(PARENT_LOCK_PATH + "/", System.currentTimeMillis());
        //校验是否是最小节点
        checkMinNode(currLockPath);
    }
    
    //解锁
    public void unlock() {
        System.out.println("delete : " + currLockPath);
        zkClient.delete(currLockPath);
    }
    
    private boolean checkMinNode(String lockPath) {
        List<String> children = zkClient.getChildren(PARENT_LOCK_PATH);
        Collections.sort(children);
        int index = children.indexOf(lockPath.substring(PARENT_LOCK_PATH.length() + 1));
        if (index == 0) {
            System.out.println(name + ": success");
            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            return true;
        }   else {
            String waitPath = PARENT_LOCK_PATH + "/" + children.get(index - 1);
            //等待前一个节点释放的监听
            waitForLock(waitPath);
            return false;
        }
    }
    
    private void waitForLock(String prev) {
        System.out.println(name + " current path :" + currLockPath + "：fail add listener" + " wait path :" + prev);
        countDownLatch = new CountDownLatch(1);
        zkClient.subscribeDataChanges(prev, new IZkDataListener() {
            
            @Override
            public void handleDataDeleted(String dataPath) throws Exception {
                System.out.println("prev node is done");
                checkMinNode(currLockPath);
                
            }
            
            @Override
            public void handleDataChange(String dataPath, Object data) throws Exception {
                
                
            }
        });
        if (!zkClient.exists(prev)) {
            return;
        }
        try {
            countDownLatch.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        countDownLatch = null;
    }
    
    
}
