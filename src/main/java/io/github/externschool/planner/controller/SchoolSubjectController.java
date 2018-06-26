package io.github.externschool.planner.controller;

import io.github.externschool.planner.entity.SchoolSubject;
import io.github.externschool.planner.exceptions.SubjectExistsException;
import io.github.externschool.planner.service.RoleService;
import io.github.externschool.planner.service.SchoolSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@Controller
public class SchoolSubjectController {
    @Autowired
    SchoolSubjectService subjectService;
    @Autowired
    RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public String saveOrUpdateSubject(@Valid SchoolSubject subject, BindingResult bindingResult, Model model) throws SubjectExistsException {
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
        } else
            subjectService.saveOrUpdateSubject(subject);
        model.addAttribute("subject", subject);
        return "redirect:/subject";
    }

    @GetMapping("/subject/")
    public String showSubjects(Map<String, Object> model) {
        Iterable<SchoolSubject> subjects = subjectService.findAll();
        model.put("subjects", subjects);
        return "subject";
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@RequestParam SchoolSubject subject, @PathVariable("name") Long id) {
        subject = subjectService.findSubjectById(id);
        if (subject == null) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        subjectService.deleteSubjectById(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
