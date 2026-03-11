package vn.edu.ute.service;

import vn.edu.ute.model.Room;
import java.util.List;

public interface RoomService {
    Room saveRoom(Room room) throws Exception;
    Room getRoomById(Long id) throws Exception;
    List<Room> getAllRooms() throws Exception;
    List<Room> getRoomsByBranchId(Long branchId) throws Exception;
    void deleteRoom(Long id) throws Exception;
    List<Room> filterRooms(String keyword, vn.edu.ute.common.enumeration.Status status, Integer minCapacity) throws Exception;
}
