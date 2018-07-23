package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.service.PersonServiceImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/guest")
public class PersonController {

    private final PersonServiceImpl personService;
    private final ConversionService conversionService;

    public PersonController(PersonServiceImpl personService, ConversionService conversionService) {
        this.personService = personService;
        this.conversionService = conversionService;
    }

    @GetMapping("/")
    public ModelAndView findAll(){
        return new ModelAndView("guest/person_list", "persons",
                personService.findAllByOrderByNameAsc().stream()
                .map(t->conversionService.convert(t, PersonDTO.class))
                .collect(Collectors.toList()));
    }

    @PostMapping("/{id}")
    public ModelAndView edit(@PathVariable("id") Long id){
        PersonDTO personDTO = conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return show(personDTO);
    }

    @PostMapping("/add")
    public ModelAndView add(){
        Person person = personService.saveOrUpdatePerson(new Person());
        PersonDTO personDTO = conversionService.convert(person, PersonDTO.class);

        return show(personDTO);
    }

    @DeleteMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable("id") Long id){
        personService.deletePerson(id);
        return new ModelAndView("redirect:/guest/");
    }

    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView save(@ModelAttribute("person") PersonDTO personDTO) {
        personService.saveOrUpdatePerson(conversionService.convert(personDTO,Person.class));

        return new ModelAndView("redirect:/guest/");
    }

    private ModelAndView show(PersonDTO personDTO){
        ModelAndView modelAndView = new ModelAndView("guest/person_profile");
        modelAndView.addObject("person", personDTO);

        return modelAndView;
    }

}
