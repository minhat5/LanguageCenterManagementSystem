package vn.edu.ute.service;

import vn.edu.ute.dto.ScheduleView;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Room;
import vn.edu.ute.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    List<Schedule> getAll() throws Exception;
    void insert(Schedule schedule) throws Exception;
    void update(Schedule schedule) throws Exception;
    void delete(Long id) throws Exception;
    void insertUntilEndDate(Schedule schedule, List<Schedule> existingSchedules) throws Exception;
    List<Schedule> getByDate(List<Schedule> schedules, LocalDate date);
    List<Schedule> getByClass(List<Schedule> schedules, Clas clas);
    void deleteUntilEndDate(Schedule schedule, List<Schedule> existingSchedules) throws Exception;
    List<ScheduleView> toScheduleView(List<Schedule> schedules);
    Schedule findById(Long id) throws Exception;
    List<Schedule> getAccessibleSchedule() throws Exception;
}
