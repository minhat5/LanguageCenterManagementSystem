package vn.edu.ute.service;

import vn.edu.ute.model.Room;
import vn.edu.ute.common.enumeration.Status;
import java.util.List;

public interface RoomService {
    List<Room> getAll() throws Exception;
    List<Room> getByBranchId(List<Room> rooms, Long branchId);
    Room saveRoom(Room room) throws Exception;
    Room getRoomById(Long id) throws Exception;
    List<Room> getAllRooms() throws Exception;
    List<Room> getRoomsByBranchId(Long branchId) throws Exception;
    void deleteRoom(Long id) throws Exception;
    List<Room> filterRooms(String keyword, Status status, Integer minCapacity) throws Exception;
}