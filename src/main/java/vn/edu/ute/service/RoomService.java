package vn.edu.ute.service;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.RoomRepo;

import java.util.List;

public class RoomService {
    private final RoomRepo roomRepo;
    private final TransactionManager tx;

    public RoomService(RoomRepo roomRepo, TransactionManager tx) {
        this.roomRepo = roomRepo;
        this.tx = tx;
    }

    public List<Room> getAll() throws Exception {
        return tx.runInTransaction(roomRepo::findAll);
    }
}
