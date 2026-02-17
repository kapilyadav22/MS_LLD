package ManageDatabaseService;

import java.util.*;

// ===== ENUMS =====
enum DBStatus {
    CREATING, RUNNING, FAILED
}

enum NodeRole {
    PRIMARY, REPLICA
}

enum HealthStatus {
    HEALTHY, UNHEALTHY
}

// ===== CORE ENTITIES =====
class DBNode {
    String nodeId;
    NodeRole role;
    HealthStatus healthStatus;

    DBNode(String nodeId, NodeRole role) {
        this.nodeId = nodeId;
        this.role = role;
        this.healthStatus = HealthStatus.HEALTHY;
    }
}

class DatabaseInstance {
    String dbId;
    DBStatus status;
    DBNode primaryNode;
    List<DBNode> replicas = new ArrayList<>();

    DatabaseInstance(String dbId) {
        this.dbId = dbId;
        this.status = DBStatus.CREATING;
    }
}

// ===== SERVICES =====
class BackupService {
    void takeBackup(DatabaseInstance db) {
        System.out.println("Taking backup for DB: " + db.dbId);
    }
}

class ReplicationManager {
    void addReplica(DatabaseInstance db, DBNode replica) {
        db.replicas.add(replica);
        System.out.println("Replica added: " + replica.nodeId);
    }

    DBNode promoteReplica(DatabaseInstance db) {
        if (db.replicas.isEmpty()) return null;
        DBNode newPrimary = db.replicas.remove(0);
        newPrimary.role = NodeRole.PRIMARY;
        db.primaryNode = newPrimary;
        System.out.println("Replica promoted to PRIMARY: " + newPrimary.nodeId);
        return newPrimary;
    }
}

class HealthMonitor {
    boolean isHealthy(DBNode node) {
        return node.healthStatus == HealthStatus.HEALTHY;
    }
}

class FailoverManager {
    ReplicationManager replicationManager;

    FailoverManager(ReplicationManager rm) {
        this.replicationManager = rm;
    }

    void handleFailure(DatabaseInstance db) {
        System.out.println("Primary failed. Initiating failover...");
        replicationManager.promoteReplica(db);
    }
}

class QueryRouter {
    void routeRead(DatabaseInstance db) {
        if (!db.replicas.isEmpty()) {
            System.out.println("Read routed to replica");
        } else {
            System.out.println("Read routed to primary");
        }
    }

    void routeWrite(DatabaseInstance db) {
        System.out.println("Write routed to primary");
    }
}

// ===== MAIN SERVICE =====
class ManagedDatabaseService {
    Map<String, DatabaseInstance> databases = new HashMap<>();
    ReplicationManager replicationManager = new ReplicationManager();
    BackupService backupService = new BackupService();
    HealthMonitor healthMonitor = new HealthMonitor();
    FailoverManager failoverManager = new FailoverManager(replicationManager);
    QueryRouter router = new QueryRouter();

    void createDatabase(String dbId) {
        DatabaseInstance db = new DatabaseInstance(dbId);
        DBNode primary = new DBNode("node-primary-" + dbId, NodeRole.PRIMARY);
        db.primaryNode = primary;

        DBNode replica = new DBNode("node-replica-" + dbId, NodeRole.REPLICA);
        replicationManager.addReplica(db, replica);

        db.status = DBStatus.RUNNING;
        databases.put(dbId, db);
        System.out.println("Database created: " + dbId);
    }

    void executeRead(String dbId) {
        router.routeRead(databases.get(dbId));
    }

    void executeWrite(String dbId) {
        router.routeWrite(databases.get(dbId));
    }

    void simulatePrimaryFailure(String dbId) {
        DatabaseInstance db = databases.get(dbId);
        db.primaryNode.healthStatus = HealthStatus.UNHEALTHY;

        if (!healthMonitor.isHealthy(db.primaryNode)) {
            failoverManager.handleFailure(db);
        }
    }
}

// ===== DEMO =====
public class ManagedDatabaseSystem {
    public static void main(String[] args) {
        ManagedDatabaseService service = new ManagedDatabaseService();

        service.createDatabase("orders-db");
        service.executeWrite("orders-db");
        service.executeRead("orders-db");

        service.simulatePrimaryFailure("orders-db");
        service.executeWrite("orders-db");
    }
}
