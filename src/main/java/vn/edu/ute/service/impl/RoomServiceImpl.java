package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Room;
import vn.edu.ute.repo.RoomRepository;
import vn.edu.ute.service.RoomService;
import vn.edu.ute.common.enumeration.Status;

import java.util.List;
import java.util.stream.Collectors;

public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepo;
    private final TransactionManager txManager;

    public RoomServiceImpl(RoomRepository roomRepo, TransactionManager txManager) {
        this.roomRepo = roomRepo;
        this.txManager = txManager;
    }    
    @Override
    public List<Room> getAll() throws Exception {
        return getAllRooms();
    }

    @Override
    public List<Room> getByBranchId(List<Room> rooms, Long branchId) {
        return rooms.stream()
                .filter(r -> r.getBranch() != null && r.getBranch().getBranchId().equals(branchId))
                .collect(Collectors.toList());
    }

    @Override
    public Room saveRoom(Room room) throws Exception {
        return txManager.runInTransaction(em -> roomRepo.save(em, room));
    }

    @Override
    public Room getRoomById(Long id) throws Exception {
        return txManager.runInTransaction(em -> roomRepo.findById(em, id).orElse(null));
    }

    @Override
    public List<Room> getAllRooms() throws Exception {
        return txManager.runInTransaction(em -> roomRepo.findAll(em));
    }

    @Override
    public List<Room> getRoomsByBranchId(Long branchId) throws Exception {
        return txManager.runInTransaction(em -> roomRepo.findByBranchId(em, branchId));
    }

    @Override
    public void deleteRoom(Long id) throws Exception {
        txManager.runInTransaction(em -> {
            roomRepo.deleteById(em, id);
            return null;
        });
    }

    @Override
    public List<Room> filterRooms(String keyword, Status status, Integer minCapacity) throws Exception {
        return getAllRooms().stream()
            .filter(r -> {
                boolean matchKw = (keyword == null || keyword.trim().isEmpty()) ||
                                  (r.getRoomName() != null && r.getRoomName().toLowerCase().contains(keyword.toLowerCase())) ||
                                  (r.getLocation() != null && r.getLocation().toLowerCase().contains(keyword.toLowerCase()));
                boolean matchSt = (status == null) || (r.getStatus() == status);
                boolean matchCap = (minCapacity == null) || (r.getCapacity() != null && r.getCapacity() >= minCapacity);
                return matchKw && matchSt && matchCap;
            })
            .collect(Collectors.toList());
    }
}