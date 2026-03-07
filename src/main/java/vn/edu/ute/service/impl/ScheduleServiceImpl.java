package vn.edu.ute.service.impl;

import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.model.Schedule;
import vn.edu.ute.repo.ScheduleRepo;
import vn.edu.ute.service.ScheduleService;

import java.time.LocalDate;
import java.util.List;

public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepo scheduleRepo;
    private final TransactionManager tx;

    public ScheduleServiceImpl(ScheduleRepo scheduleRepo, TransactionManager tx) {
        this.scheduleRepo = scheduleRepo;
        this.tx = tx;
    }

    @Override
    public List<Schedule> getAll() throws Exception {
        return tx.runInTransaction(scheduleRepo::findAll);
    }

    @Override
    public void insert(Schedule schedule) throws Exception {
        tx.runInTransaction(em -> {
            scheduleRepo.insert(em, schedule);
            return null;
        });
    }

    @Override
    public void update(Schedule schedule) throws Exception {
        tx.runInTransaction(em -> {
            Schedule existingSchedule = scheduleRepo.findById(em, schedule.getScheduleId());
            if(existingSchedule == null) {
                throw new IllegalArgumentException("Không tìm thấy lịch học với mã lịch học: " + schedule.getScheduleId());
            }
            scheduleRepo.update(em, schedule);
            return null;
        });
    }

    @Override
    public void delete(Long id) throws Exception {
        tx.runInTransaction(em -> {
            Schedule schedule = scheduleRepo.findById(em, id);
            if (schedule == null) {
                throw new IllegalArgumentException("Không tìm thấy lịch học với mã lịch học: " + id);
            }
            scheduleRepo.delete(em, id);
            return null;
        });
    }

    public boolean isConflict(Schedule newSchedule, List<Schedule> existingSchedules) {
        return existingSchedules.stream()
                .anyMatch(s -> s.getStudyDate().equals(newSchedule.getStudyDate())
                        && (s.getRoom().getRoomId().equals(newSchedule.getRoom().getRoomId())
                        || s.getClas().getTeacher().getTeacherId().equals(newSchedule.getClas().getTeacher().getTeacherId()))
                        && s.getStartTime().isBefore(newSchedule.getEndTime())
                        && s.getEndTime().isAfter(newSchedule.getStartTime())
                );
    }

    @Override
    public void insertUntilEndDate(Schedule schedule, List<Schedule> existingSchedules) {
        LocalDate startDate = schedule.getStudyDate();
        LocalDate endDate = schedule.getClas().getEndDate();
        while(startDate.isBefore(endDate)) {
            try {
                if (!isConflict(schedule, existingSchedules)) {
                    insert(schedule);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            startDate = startDate.plusWeeks(1);
            schedule.setStudyDate(startDate);
        }
    }
}
