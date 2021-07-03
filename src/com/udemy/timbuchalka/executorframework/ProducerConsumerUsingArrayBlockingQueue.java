package com.udemy.timbuchalka.executorframework;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ProducerConsumerUsingArrayBlockingQueue {
    public static void main(String[] args) {
        ArrayBlockingQueue<String> buffer = new ArrayBlockingQueue<String>(6);

        ExecutorService executorService = Executors.newFixedThreadPool(4);

        MyProducer3 producer = new MyProducer3(buffer, ThreadColor.ANSI_YELLOW);
        MyConsumer3 consumer1 = new MyConsumer3(buffer, ThreadColor.ANSI_PURPLE);
        MyConsumer3 consumer2 = new MyConsumer3(buffer, ThreadColor.ANSI_CYAN);

        executorService.execute(producer);
        executorService.execute(consumer1);
        executorService.execute(consumer2);

        Future<String> future = executorService.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println(ThreadColor.ANSI_BLUE + "From anonymous Callable class");
                return "This is callable result";
            }
        });
        try {
            future.get();
        } catch (ExecutionException ee) {
            System.out.println("Something went wrong.");
        } catch (InterruptedException ie) {
            System.out.println("Thread interrupted");
        }

        executorService.shutdown();
    }
}

class MyProducer3 implements Runnable {
    private ArrayBlockingQueue<String> buffer;
    private String color;

    public MyProducer3(ArrayBlockingQueue<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    @Override
    public void run() {
        Random random = new Random();
        String[] nums = {"1", "2", "3", "4", "5"};
        for (String num: nums) {
            try {
                System.out.println(color + "Adding..." + num);
                buffer.put(num);

                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException ie) {
                System.out.println("Producer was interrupted");
            }
        }
        System.out.println(color + "Adding EOF and exiting...");
        try {
            buffer.put("EOF");
        } catch (InterruptedException ie) {
        }
    }
}

class MyConsumer3 implements Runnable {
    private ArrayBlockingQueue<String> buffer;
    private String color;

    public MyConsumer3(ArrayBlockingQueue<String> buffer, String color) {
        this.buffer = buffer;
        this.color = color;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (buffer) {
                try {
                    if (buffer.isEmpty()) {
                        continue;
                    }
                    if (buffer.peek().equals("EOF")){
                        System.out.println(color + "Exiting...");
                        break;
                    } else {
                        System.out.println(color + "Removed " + buffer.take());
                    }
                } catch (InterruptedException ie){
                }
            }
        }
    }
}