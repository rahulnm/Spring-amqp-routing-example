package hello;

import java.util.concurrent.CountDownLatch;

/**
 * Created by rahul on 2/10/16.
 */

public class ReceiverFor2 {

    private CountDownLatch latch = new CountDownLatch(1);

    public void receiveMessageFor2(String message) {
        System.out.println("Received ****" + message + "****");
        latch.countDown();
    }

    public CountDownLatch getLatch() {
        return latch;
    }

}