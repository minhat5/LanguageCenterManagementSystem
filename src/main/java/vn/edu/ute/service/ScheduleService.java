package vn.edu.ute.service;

import vn.edu.ute.model.Schedule;

import java.util.List;

public interface ScheduleService {
    List<Schedule> getAll() throws Exception;
    void insert(Schedule schedule) throws Exception;
    void update(Schedule schedule) throws Exception;
    void delete(Long id) throws Exception;
    void insertUntilEndDate(Schedule schedule, List<Schedule> existingSchedules);
}
