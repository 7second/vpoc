package io.vizit.vpoc.jvm.model;

import io.vizit.vpoc.jvm.Monitor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

@Getter
@Component
public class Eden {
    private long capacity = JvmConfig.getEdenSize();
    private AtomicLong allocatedSpace = new AtomicLong(0);
    PriorityQueue<ObjectBO> allocatedObjects = new PriorityQueue<>(Comparator.comparing(ObjectBO::getSize));
    PriorityQueue<ObjectBO> liveObjects = new PriorityQueue<>(Comparator.comparing(ObjectBO::getSize));
    private final Monitor monitor;

    public Eden(Monitor monitor) {
        this.monitor = monitor;
    }

    public synchronized ObjectBO allocate(long id, int size) {
        ObjectBO objectBO = new ObjectBO(id, size);
        allocatedObjects.add(objectBO);
        allocatedSpace.addAndGet(objectBO.getSize());
        monitor.reportNewObject(objectBO);
        return objectBO;
    }

    public boolean available(int size) {
        return allocatedSpace.get() + size < capacity;
    }

    public void sweep() {
        allocatedObjects.clear();
        liveObjects.clear();
        allocatedSpace.set(0);
    }

    public void mark() {
        int count = ThreadLocalRandom.current().nextInt(1, 5);
        for (ObjectBO objectBO : allocatedObjects) {
            liveObjects.add(objectBO);
            monitor.mark(objectBO);
            if (count-- == 0) {
                break;
            }
        }
    }
}