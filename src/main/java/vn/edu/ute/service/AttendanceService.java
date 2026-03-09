package vn.edu.ute.service;

import vn.edu.ute.dto.AttendanceView;
import vn.edu.ute.model.Attendance;
import vn.edu.ute.model.Clas;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    List<Attendance> getAll() throws Exception;
    void update(Attendance attendance) throws Exception;
    Attendance findById(Long id) throws Exception;
    List<AttendanceView> toAttendanceView(List<Attendance> attendances);
    List<Attendance> getByClass(List<Attendance> attendances, Clas clas);
    List<Attendance> getByAttendDate(List<Attendance> attendances, LocalDate attendDate);
}
