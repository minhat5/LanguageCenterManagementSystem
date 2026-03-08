package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.RoomRepo;
import vn.edu.ute.service.RoomService;

import java.util.List;

public class RoomServiceImpl implements RoomService {
    private final RoomRepo roomRepo;
    private final TransactionManager tx;

    public RoomServiceImpl(RoomRepo roomRepo, TransactionManager tx) {
        this.roomRepo = roomRepo;
        this.tx = tx;
    }

    // Lấy tất cả phòng học
    @Override
    public List<Room> getAll() throws Exception {
        return tx.runInTransaction(roomRepo::findAll);
    }

    // Lọc phòng học theo chi nhánh
    @Override
    public List<Room> getByBranchId(List<Room> rooms, Long branchId) {
        return rooms.stream()
                .filter(r -> r.getBranch().getBranchId().equals(branchId))
                .toList();
    }
}
