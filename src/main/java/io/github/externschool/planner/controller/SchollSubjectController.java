package io.github.externschool.planner.controller;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.service.SchoolSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/suject")
public class SchollSubjectController {
    @Autowired
    SchoolSubjectService schoolSubjectService;

    @GetMapping("/")
    public ModelAndView findAll() {
        return new ModelAndView(
                "subject/subject_list",
                "subjects",
                schoolSubjectService.findAllByOrderByNameAsc()
        );
    }

    @PostMapping
    public ModelAndView add(@ModelAttribute("new_name") String name) {
        SchoolSubject schoolSubject = new SchoolSubject();
        schoolSubject.setName(name);
        schoolSubjectService.saveOrUpdateSubject(schoolSubject);
        return new ModelAndView("redirect:/subject/");
    }

    @RequestMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable("id") Long id) {
        schoolSubjectService.deleteSubject(id);
        return new ModelAndView("redirect:/subject/");
    }

}
