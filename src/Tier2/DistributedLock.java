package Tier2;

import java.util.*;
        import java.util.concurrent.*;

class LockRecord {
    String lockId;
    String ownerId;
    long expiryTime;

    LockRecord(String lockId, String ownerId, long expiryTime) {
        this.lockId = lockId;
        this.ownerId = ownerId;
        this.expiryTime = expiryTime;
    }
}

class LockService {
    private final Map<String, LockRecord> locks = new ConcurrentHashMap<>();
    private final long leaseMillis;

    LockService(long leaseMillis) {
        this.leaseMillis = leaseMillis;
    }

    public synchronized boolean acquireLock(String lockId, String ownerId) {
        long now = System.currentTimeMillis();
        LockRecord record = locks.get(lockId);

        if (record == null || record.expiryTime < now) {
            locks.put(lockId,
                    new LockRecord(lockId, ownerId, now + leaseMillis));
            return true;
        }
        return false;
    }

    public synchronized boolean renewLock(String lockId, String ownerId) {
        LockRecord record = locks.get(lockId);
        if (record != null && record.ownerId.equals(ownerId)) {
            record.expiryTime = System.currentTimeMillis() + leaseMillis;
            return true;
        }
        return false;
    }

    public synchronized boolean releaseLock(String lockId, String ownerId) {
        LockRecord record = locks.get(lockId);
        if (record != null && record.ownerId.equals(ownerId)) {
            locks.remove(lockId);
            return true;
        }
        return false;
    }
}

class Client {
    private final String clientId;
    private final LockService lockService;

    Client(String clientId, LockService lockService) {
        this.clientId = clientId;
        this.lockService = lockService;
    }

    void doWork(String lockId) {
        if (lockService.acquireLock(lockId, clientId)) {
            try {
                // critical section
            } finally {
                lockService.releaseLock(lockId, clientId);
            }
        }
    }
}
