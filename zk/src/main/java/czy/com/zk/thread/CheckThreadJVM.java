package czy.com.zk.thread;

import java.util.ArrayList;
import java.util.List;

public class CheckThreadJVM {
    
    public static void main(String[] args) {
        while(true) {
            System.out.println(Thread.currentThread().getThreadGroup().activeCount());
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
       /* for (String string : getThreadGroup(getRootThreadGroup())) {
            System.out.println(string);
        }*/
    }

    
    /**
    * 获取根线程组
    * 
    * @return
    */
    public static ThreadGroup getRootThreadGroup() {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        while(true) {
            if (rootGroup.getParent() != null) {
                rootGroup = rootGroup.getParent();
            } else {
                break;
            }
        }
        return rootGroup;
    }
    
    /**
    * 获取线程组中的线程名
    * 
    * @param group
    * @return
    */
    public static List<String> getThreadsName(ThreadGroup threadGroup) {
        List<String> threadList = new ArrayList<>();
        Thread[] threads = new Thread[threadGroup.activeCount()];
        int count = threadGroup.enumerate(threads, false);
        for (int i = 0; i < count; i++) {
            threadList.add(threadGroup.getName() + "线程组: " + threads[i].getName());
        }
        return threadList;
    }
    
    /**
    * 获取根数组下的子数组
    * 
    * @param group
    * @return
    */
    public static List<String> getThreadGroup(ThreadGroup group) {
        List<String> threadList = getThreadsName(group);
        ThreadGroup[] threads = new ThreadGroup[group.activeGroupCount()];
        int count = group.enumerate(threads, false);
        for (int i = 0; i < count; i++) {
        threadList.addAll(getThreadsName(threads[i]));
        }
        return threadList;

    }
}



