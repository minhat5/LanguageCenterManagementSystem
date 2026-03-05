package vn.edu.ute.service;

import vn.edu.ute.common.enumeration.ClassStatus;
import vn.edu.ute.db.TransactionManager;
import vn.edu.ute.dto.ClasView;
import vn.edu.ute.model.Branch;
import vn.edu.ute.model.Clas;
import vn.edu.ute.model.Course;
import vn.edu.ute.repo.ClasRepo;

import java.util.List;

public class ClasService {
    private final ClasRepo clasRepo;
    private final TransactionManager tx;

    public ClasService(ClasRepo clasRepo, TransactionManager tx) {
        this.clasRepo = clasRepo;
        this.tx = tx;
    }

    public List<Clas> getAll() throws Exception {
        return tx.runInTransaction(clasRepo::findAll);
    }

    public void insert(Clas clas) throws Exception {
        tx.runInTransaction(em -> {
            clasRepo.insert(em, clas);
            return null;
        });
    }

    public void update(Clas clas) throws Exception {
        tx.runInTransaction(em -> {
           Clas existingClas = clasRepo.findById(em, clas.getClassId());
           if(existingClas == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với mã lớp học: " + clas.getClassId());
           }
            clasRepo.update(em, clas);
            return null;
        });
    }

    public void delete(Long id) throws Exception {
        tx.runInTransaction(em -> {
            Clas existingClas = clasRepo.findById(em, id);
            if(existingClas == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với mã lớp học: " + id);
            }
            clasRepo.delete(em, id);
            return null;
        });
    }

    public Clas findById(Long id) throws Exception {
        return tx.runInTransaction(em -> {
            Clas clas = clasRepo.findById(em, id);
            if (clas == null) {
                throw new IllegalArgumentException("Không tìm thấy lớp học với mã lớp học: " + id);
            }
            return clas;
        });
    }

    public List<ClasView> toClasView(List<Clas> classes) throws Exception {
        return classes.stream()
                .map(c -> new ClasView(
                        c.getClassId(),
                        c.getClassName(),
                        c.getCourse().getCourseName(),
                        c.getTeacher() != null ? c.getTeacher().getFullName() : "Chưa có giáo viên",
                        c.getRoom() != null ? c.getRoom().getRoomName() : "Chưa có phòng học",
                        c.getBranch() != null ? c.getBranch().getBranchName() : "Chưa có chi nhánh",
                        c.getStartDate(),
                        c.getEndDate(),
                        c.getMaxStudent(),
                        c.getStatus(),
                        c.getCreatedAt(),
                        c.getUpdatedAt()
                )).toList();
    }

    public List<Clas> getClasViewsByStatus(List<Clas> classes, ClassStatus classStatus) throws Exception {
        return classes.stream()
                .filter(c -> c.getStatus() == classStatus)
                .toList();
    }

    public List<Clas> getClasViewsByCourse(List<Clas> classes, Course course) throws Exception {
        return classes.stream()
                .filter(c -> c.getCourse() != null && c.getCourse().getCourseId().equals(course.getCourseId()))
                .toList();
    }

    public List<Clas> getClasViewsByBranch(List<Clas> classes, Branch branch) throws Exception {
        return classes.stream()
                .filter(c -> c.getTeacher() != null && c.getBranch().getBranchId().equals(branch.getBranchId()))
                .toList();
    }

    public List<Clas> findByName(List<Clas> classes, String name) throws Exception {
        String searchName = name.toLowerCase().trim();
        return classes.stream()
                .filter(c -> c.getClassName().toLowerCase().contains(searchName))
                .toList();
    }
}
