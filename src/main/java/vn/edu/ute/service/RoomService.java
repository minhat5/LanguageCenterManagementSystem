package vn.edu.ute.service;

import vn.edu.ute.model.Room;

import java.util.List;

public interface RoomService {
    List<Room> getAll() throws Exception;
    List<Room> getByBranchId(List<Room> rooms, Long branchId);
}
