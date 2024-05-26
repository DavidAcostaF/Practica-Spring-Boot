package com.example.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("")
public class StudentViewController {

    private StudentService studentService;
    @Autowired
    public StudentViewController(StudentService studentService) {
        this.studentService = studentService;
    }

    // La clase model se utiliza para transferir objetos del controller a la vista
    @GetMapping("/list")
    public String allStudents(Model model) {
        List<Student> students = studentService.getStudents();
        model.addAttribute("students",students);
        return "students/students_list";
    }

    @GetMapping("/student_form")
    public String studentForm(Model model) {

        model.addAttribute("student",new Student());
        model.addAttribute("url","/student_form/add");
        return "students/student_form";
    }
    @PostMapping("/student_form/add")
    public String saveStudent(@ModelAttribute Student student) {
        studentService.addNewStudent(student);
        return "redirect:/list";
    }

    @GetMapping("/update_student/{id}")
    public String studentFormEdit(@PathVariable Long id, Model model) {

        Student student = studentService.getStudent(id);
        model.addAttribute("student",student);
        System.out.println(student.getDob());
        model.addAttribute("url","/update_student/"+id);

        return "students/update_student_form";
    }

    @PostMapping("/update_student/{id}")
    public String updateStudent(@PathVariable Long id ,@ModelAttribute Student student) {
        studentService.updateStudent(id,student);
        return "redirect:/list";
    }
}
