package com.mitwpu.cseconnect.config;

import com.mitwpu.cseconnect.entity.*;
import com.mitwpu.cseconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherPanelAssignmentRepository panelAssignmentRepository;
    private final ClubRepository clubRepository;
    private final ClubMembershipRepository membershipRepository;
    private final AchievementRepository achievementRepository;
    private final AnnouncementRepository announcementRepository;
    private final AcademicRecordRepository academicRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmail("admin@mitwpu.edu.in")) {
            log.info("Data already seeded. Skipping initialization.");
            return;
        }
        log.info("Seeding initial data...");
        seedData();
        log.info("Data seeding completed.");
    }

    private void seedData() {
        // Users
        User admin = createUser("admin@mitwpu.edu.in", "Admin@1234", User.Role.ADMIN);
        User teacherUser1 = createUser("sunita.sharma@mitwpu.edu.in", "Teacher@1234", User.Role.TEACHER);
        User teacherUser2 = createUser("rajesh.patil@mitwpu.edu.in", "Teacher@1234", User.Role.TEACHER);
        User teacherUser3 = createUser("meena.joshi@mitwpu.edu.in", "Teacher@1234", User.Role.TEACHER);

        User stuUser1 = createUser("ria.modak@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);
        User stuUser2 = createUser("sanika.dange@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);
        User stuUser3 = createUser("himanshu.dikay@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);
        User stuUser4 = createUser("shlok.more@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);
        User stuUser5 = createUser("arjun.kulkarni@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);
        User stuUser6 = createUser("priya.desai@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);
        User stuUser7 = createUser("rohan.mehta@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);
        User stuUser8 = createUser("sneha.jain@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);
        User stuUser9 = createUser("varun.shah@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);
        User stuUser10 = createUser("pooja.nair@mitwpu.edu.in", "Student@1234", User.Role.STUDENT);

        // Teachers
        Teacher t1 = createTeacher(teacherUser1, "Sunita Sharma", "EMP001", "Assistant Professor", null, admin.getId());
        Teacher t2 = createTeacher(teacherUser2, "Rajesh Patil", "EMP002", "Associate Professor", null, admin.getId());
        Teacher t3 = createTeacher(teacherUser3, "Meena Joshi", "EMP003", "Assistant Professor", null, admin.getId());

        // Panel Assignments
        createPanelAssignment(t1, Student.Panel.B, "2024-2025");
        createPanelAssignment(t1, Student.Panel.C, "2024-2025");
        createPanelAssignment(t2, Student.Panel.A, "2024-2025");
        createPanelAssignment(t2, Student.Panel.D, "2024-2025");
        createPanelAssignment(t3, Student.Panel.E, "2024-2025");
        createPanelAssignment(t3, Student.Panel.F, "2024-2025");

        // Students
        Student s1 = createStudent(stuUser1, "1032231769", "Ria Modak", Student.Panel.B, 3,
                new BigDecimal("8.90"), new BigDecimal("87.00"),
                "TCS", "Software Intern", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 8, 31),
                null, "https://github.com/riamodak", "https://linkedin.com/in/riamodak",
                "Java, React, Spring Boot", null, admin.getId());

        Student s2 = createStudent(stuUser2, "1032231763", "Sanika Dange", Student.Panel.B, 3,
                new BigDecimal("8.50"), new BigDecimal("92.00"),
                null, null, null, null,
                null, null, null,
                "Python, Machine Learning, TensorFlow", null, admin.getId());

        Student s3 = createStudent(stuUser3, "1032231737", "Himanshu Dikay", Student.Panel.B, 3,
                new BigDecimal("7.80"), new BigDecimal("78.00"),
                null, null, null, null,
                null, null, null,
                "C++, DSA, Competitive Programming", null, admin.getId());

        Student s4 = createStudent(stuUser4, "1032231797", "Shlok More", Student.Panel.B, 3,
                new BigDecimal("8.20"), new BigDecimal("85.00"),
                null, null, null, null,
                null, null, null,
                "Robotics, Arduino, IoT", null, admin.getId());

        Student s5 = createStudent(stuUser5, "1032231701", "Arjun Kulkarni", Student.Panel.A, 2,
                new BigDecimal("9.10"), new BigDecimal("94.00"),
                null, null, null, null,
                null, null, null,
                "ML, Python, Research", null, admin.getId());

        Student s6 = createStudent(stuUser6, "1032231702", "Priya Desai", Student.Panel.C, 4,
                new BigDecimal("8.70"), new BigDecimal("88.00"),
                "Infosys", "Developer Intern", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30),
                null, null, null,
                "Full Stack, React, Node.js", null, admin.getId());

        Student s7 = createStudent(stuUser7, "1032231703", "Rohan Mehta", Student.Panel.D, 1,
                new BigDecimal("7.50"), new BigDecimal("82.00"),
                null, null, null, null,
                null, null, null,
                "HTML, CSS, JavaScript", null, admin.getId());

        Student s8 = createStudent(stuUser8, "1032231704", "Sneha Jain", Student.Panel.E, 2,
                new BigDecimal("8.80"), new BigDecimal("91.00"),
                null, null, null, null,
                null, null, null,
                "Data Science, SQL, Tableau", null, admin.getId());

        Student s9 = createStudent(stuUser9, "1032231705", "Varun Shah", Student.Panel.F, 3,
                new BigDecimal("7.20"), new BigDecimal("75.00"),
                null, null, null, null,
                null, null, null,
                "Android, Kotlin, Firebase", null, admin.getId());

        Student s10 = createStudent(stuUser10, "1032231706", "Pooja Nair", Student.Panel.A, 4,
                new BigDecimal("9.30"), new BigDecimal("96.00"),
                "Google", "SWE Intern", LocalDate.of(2024, 5, 1), LocalDate.of(2024, 7, 31),
                null, null, null,
                "Algorithms, System Design, Go", null, admin.getId());

        // Clubs
        Club gdsc = createClub("GDSC MITWPU", "Google Developer Student Club at MIT WPU. We build solutions for local businesses and NGOs using Google technologies.",
                "Technical", null, 2019, "Dr. Sunita Sharma", admin.getId());
        Club codechef = createClub("CodeChef Chapter MITWPU", "Official CodeChef Chapter focused on competitive programming, DSA workshops, and preparing students for coding contests.",
                "Technical", null, 2020, "Prof. Rajesh Patil", admin.getId());
        Club robotics = createClub("Robotics Club MITWPU", "Building autonomous robots, participating in Robocon, and exploring IoT, embedded systems, and drone technology.",
                "Technical", null, 2018, "Prof. Meena Joshi", admin.getId());
        Club nss = createClub("NSS MITWPU", "National Service Scheme unit conducting community outreach, blood donation drives, and rural development camps.",
                "Social Service", null, 2016, null, admin.getId());
        Club ecell = createClub("E-Cell MITWPU", "Entrepreneurship Cell fostering startup culture, conducting ideathons, and connecting students with mentors and investors.",
                "Entrepreneurship", null, 2021, null, admin.getId());

        // Club Memberships
        createMembership(s1, gdsc, ClubMembership.ClubRole.MEMBER, "2022-2023", "2022-2023", false, admin.getId());
        createMembership(s1, gdsc, ClubMembership.ClubRole.SECRETARY, "2023-2024", null, true, admin.getId());
        createMembership(s1, nss, ClubMembership.ClubRole.MEMBER, "2021-2022", "2021-2022", false, admin.getId());

        createMembership(s2, codechef, ClubMembership.ClubRole.MEMBER, "2022-2023", "2022-2023", false, admin.getId());
        createMembership(s2, codechef, ClubMembership.ClubRole.VICE_PRESIDENT, "2023-2024", null, true, admin.getId());

        createMembership(s3, codechef, ClubMembership.ClubRole.MEMBER, "2023-2024", null, true, admin.getId());

        createMembership(s4, robotics, ClubMembership.ClubRole.MEMBER, "2022-2023", "2022-2023", false, admin.getId());
        createMembership(s4, robotics, ClubMembership.ClubRole.SECRETARY, "2023-2024", null, true, admin.getId());

        createMembership(s5, ecell, ClubMembership.ClubRole.MEMBER, "2022-2023", "2022-2023", false, admin.getId());
        createMembership(s5, robotics, ClubMembership.ClubRole.MEMBER, "2023-2024", null, true, admin.getId());

        createMembership(s10, gdsc, ClubMembership.ClubRole.VICE_PRESIDENT, "2022-2023", "2022-2023", false, admin.getId());
        createMembership(s10, gdsc, ClubMembership.ClubRole.PRESIDENT, "2023-2024", null, true, admin.getId());

        createMembership(s6, ecell, ClubMembership.ClubRole.SECRETARY, "2022-2023", "2022-2023", false, admin.getId());
        createMembership(s6, ecell, ClubMembership.ClubRole.PRESIDENT, "2023-2024", null, true, admin.getId());

        createMembership(s8, nss, ClubMembership.ClubRole.MEMBER, "2023-2024", null, true, admin.getId());

        // Achievements
        createAchievement(s1, "Smart India Hackathon 2023 — Winner (National Level)",
                Achievement.AchievementCategory.HACKATHON,
                "Led a 6-member team to build an AI-powered crop disease detection system. Won first place among 1,20,000+ teams nationwide.",
                LocalDate.of(2023, 9, 20), "AICTE", Achievement.AchievementStatus.VERIFIED, admin.getId());

        createAchievement(s2, "Google Cloud Associate Cloud Engineer Certification",
                Achievement.AchievementCategory.CERTIFICATION,
                "Passed the Google Cloud ACE exam on first attempt with a score of 89%. Covers deploying, monitoring, and managing GCP services.",
                LocalDate.of(2024, 1, 15), "Google", Achievement.AchievementStatus.VERIFIED, admin.getId());

        createAchievement(s5, "Research Paper — ML-Based Early Detection of Diabetic Retinopathy",
                Achievement.AchievementCategory.PAPER_PUBLICATION,
                "Co-authored paper accepted at the International Conference on Computing, Communication and Intelligent Systems 2024.",
                LocalDate.of(2024, 3, 10), "Springer (ICCCI 2024 Proceedings)", Achievement.AchievementStatus.PENDING, null);

        createAchievement(s6, "HackMIT 2023 — Top 10 Finalist",
                Achievement.AchievementCategory.HACKATHON,
                "Among 3000+ international applicants, built a real-time carbon footprint tracker using React and Node.js.",
                LocalDate.of(2023, 10, 5), "MIT", Achievement.AchievementStatus.VERIFIED, admin.getId());

        createAchievement(s3, "AWS Certified Cloud Practitioner",
                Achievement.AchievementCategory.CERTIFICATION,
                null, LocalDate.of(2023, 11, 20), "Amazon Web Services", Achievement.AchievementStatus.PENDING, null);

        createAchievement(s4, "Robocon 2024 — National Qualifier",
                Achievement.AchievementCategory.COMPETITION,
                null, LocalDate.of(2024, 2, 18), "ABU Asia-Pacific Robot Contest", Achievement.AchievementStatus.VERIFIED, admin.getId());

        createAchievement(s10, "Google Summer of Code 2024 — Selected Contributor",
                Achievement.AchievementCategory.PROJECT,
                "Selected for GSoC 2024 to contribute to the OpenMRS electronic health records platform. Implemented REST API enhancements.",
                LocalDate.of(2024, 5, 1), "Google Open Source", Achievement.AchievementStatus.VERIFIED, admin.getId());

        createAchievement(s9, "Android Developer Nanodegree",
                Achievement.AchievementCategory.CERTIFICATION,
                null, LocalDate.of(2024, 2, 10), "Udacity", Achievement.AchievementStatus.PENDING, null);

        // Announcements
        createAnnouncement(admin, "End Semester Examination Schedule — May 2024",
                "The End Semester Examinations for all years will commence from May 15, 2024. Students are advised to check the detailed timetable on the university ERP portal. Examination hall tickets will be issued through department coordinators. Students with attendance below 75% should contact their panel teacher immediately.",
                Announcement.TargetAudience.ALL, null, Announcement.TargetPanel.ALL, true,
                LocalDateTime.of(2024, 4, 1, 10, 0));

        createAnnouncement(teacherUser1, "Panel B Tutorial Session Rescheduled",
                "The tutorial session for Panel B originally scheduled for Thursday April 4th has been rescheduled to Friday April 5th at 2:00 PM in Lab 302. All Panel B students must attend. Attendance will be marked.",
                Announcement.TargetAudience.STUDENTS, null, Announcement.TargetPanel.B, false,
                LocalDateTime.of(2024, 4, 3, 14, 0));

        createAnnouncement(admin, "GDSC MITWPU — Open Recruitment 2024-25",
                "GDSC MITWPU is now open for recruitment for the academic year 2024-25. We are looking for passionate developers, designers, and problem solvers. Apply through the club portal by April 20th. Shortlisted candidates will be called for a brief interaction round.",
                Announcement.TargetAudience.CLUB, gdsc, Announcement.TargetPanel.ALL, false,
                LocalDateTime.of(2024, 4, 5, 9, 0));

        createAnnouncement(admin, "CSE Department Industry Visit — Infosys Pune Campus",
                "The CSE Department is organizing an industry visit to Infosys Pune campus on April 25, 2024 for Year 3 students. Registration is mandatory. Students must carry their college ID. Report to the department office by 7:30 AM on the day of visit.",
                Announcement.TargetAudience.STUDENTS, null, Announcement.TargetPanel.ALL, false,
                LocalDateTime.of(2024, 4, 8, 8, 0));

        // Academic Records for Ria Modak
        createAcademicRecord(s1, "2021-2022", 1, new BigDecimal("8.20"), new BigDecimal("8.20"), new BigDecimal("90.00"), 0, null, admin.getId());
        createAcademicRecord(s1, "2021-2022", 2, new BigDecimal("8.60"), new BigDecimal("8.40"), new BigDecimal("88.00"), 0, null, admin.getId());
        createAcademicRecord(s1, "2022-2023", 3, new BigDecimal("9.00"), new BigDecimal("8.60"), new BigDecimal("86.00"), 0, null, admin.getId());
        createAcademicRecord(s1, "2022-2023", 4, new BigDecimal("9.10"), new BigDecimal("8.70"), new BigDecimal("85.00"), 0, null, admin.getId());
        createAcademicRecord(s1, "2023-2024", 5, new BigDecimal("9.20"), new BigDecimal("8.90"), new BigDecimal("87.00"), 0, null, admin.getId());
    }

    private User createUser(String email, String password, User.Role role) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setIsActive(true);
        user.setIsDeleted(false);
        return userRepository.save(user);
    }

    private Teacher createTeacher(User user, String fullName, String employeeId, String designation, String phone, Long createdBy) {
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setFullName(fullName);
        teacher.setEmployeeId(employeeId);
        teacher.setDesignation(designation);
        teacher.setPhone(phone);
        teacher.setCreatedBy(createdBy);
        teacher.setIsDeleted(false);
        return teacherRepository.save(teacher);
    }

    private void createPanelAssignment(Teacher teacher, Student.Panel panel, String academicYear) {
        TeacherPanelAssignment assignment = new TeacherPanelAssignment();
        assignment.setTeacher(teacher);
        assignment.setPanel(panel);
        assignment.setAcademicYear(academicYear);
        assignment.setIsCurrent(true);
        assignment.setIsDeleted(false);
        panelAssignmentRepository.save(assignment);
    }

    private Student createStudent(User user, String prn, String fullName, Student.Panel panel, int year,
                                   BigDecimal cgpa, BigDecimal attendance,
                                   String internCompany, String internRole, LocalDate internStart, LocalDate internEnd,
                                   String phone, String github, String linkedin,
                                   String skills, String bio, Long createdBy) {
        Student student = new Student();
        student.setUser(user);
        student.setPrn(prn);
        student.setFullName(fullName);
        student.setPanel(panel);
        student.setYear(year);
        student.setCgpa(cgpa);
        student.setAttendancePercent(attendance);
        student.setInternshipCompany(internCompany);
        student.setInternshipRole(internRole);
        student.setInternshipStart(internStart);
        student.setInternshipEnd(internEnd);
        student.setPhone(phone);
        student.setGithubUrl(github);
        student.setLinkedinUrl(linkedin);
        student.setSkills(skills);
        student.setBio(bio);
        student.setCreatedBy(createdBy);
        student.setIsDeleted(false);
        return studentRepository.save(student);
    }

    private Club createClub(String name, String description, String category, String logoUrl,
                             int foundedYear, String facultyAdvisor, Long createdBy) {
        Club club = new Club();
        club.setName(name);
        club.setDescription(description);
        club.setCategory(category);
        club.setLogoUrl(logoUrl);
        club.setFoundedYear(foundedYear);
        club.setFacultyAdvisor(facultyAdvisor);
        club.setIsActive(true);
        club.setCreatedBy(createdBy);
        club.setIsDeleted(false);
        return clubRepository.save(club);
    }

    private void createMembership(Student student, Club club, ClubMembership.ClubRole role,
                                   String startYear, String endYear, boolean isCurrent, Long createdBy) {
        ClubMembership membership = new ClubMembership();
        membership.setStudent(student);
        membership.setClub(club);
        membership.setRole(role);
        membership.setStartYear(startYear);
        membership.setEndYear(endYear);
        membership.setIsCurrent(isCurrent);
        membership.setJoinedVia(ClubMembership.JoinedVia.DIRECT);
        membership.setCreatedBy(createdBy);
        membership.setIsDeleted(false);
        membershipRepository.save(membership);
    }

    private void createAchievement(Student student, String title, Achievement.AchievementCategory category,
                                    String description, LocalDate date, String org,
                                    Achievement.AchievementStatus status, Long verifiedBy) {
        Achievement achievement = new Achievement();
        achievement.setStudent(student);
        achievement.setTitle(title);
        achievement.setCategory(category);
        achievement.setDescription(description);
        achievement.setDateOfAchievement(date);
        achievement.setIssuingOrganization(org);
        achievement.setStatus(status);
        achievement.setIsDeleted(false);
        if (status == Achievement.AchievementStatus.VERIFIED && verifiedBy != null) {
            achievement.setVerifiedBy(verifiedBy);
            achievement.setVerifiedAt(LocalDateTime.now());
        }
        achievementRepository.save(achievement);
    }

    private void createAnnouncement(User postedBy, String title, String body,
                                     Announcement.TargetAudience audience, Club targetClub,
                                     Announcement.TargetPanel panel, boolean isPinned,
                                     LocalDateTime createdAt) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setBody(body);
        announcement.setPostedBy(postedBy);
        announcement.setTargetAudience(audience);
        announcement.setTargetClub(targetClub);
        announcement.setTargetPanel(panel);
        announcement.setIsPinned(isPinned);
        announcement.setIsDeleted(false);
        announcementRepository.save(announcement);
    }

    private void createAcademicRecord(Student student, String academicYear, int semester,
                                       BigDecimal sgpa, BigDecimal cgpa, BigDecimal attendance,
                                       int backlogs, String remarks, Long recordedBy) {
        AcademicRecord record = new AcademicRecord();
        record.setStudent(student);
        record.setAcademicYear(academicYear);
        record.setSemester(semester);
        record.setSgpa(sgpa);
        record.setCgpa(cgpa);
        record.setAttendancePercent(attendance);
        record.setBacklogs(backlogs);
        record.setRemarks(remarks);
        record.setRecordedBy(recordedBy);
        academicRecordRepository.save(record);
    }
}
