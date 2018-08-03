package io.github.externschool.planner.controller;

import io.github.externschool.planner.dto.PersonDTO;
import io.github.externschool.planner.entity.User;
import io.github.externschool.planner.entity.VerificationKey;
import io.github.externschool.planner.entity.profile.Person;
import io.github.externschool.planner.service.PersonServiceImpl;
import io.github.externschool.planner.service.UserService;
import io.github.externschool.planner.service.VerificationKeyService;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/guest")
public class GuestController {

    private final PersonServiceImpl personService;
    private final ConversionService conversionService;
    private final VerificationKeyService keyService;
    private final UserService userService;

    public GuestController(final PersonServiceImpl personService,
                           final ConversionService conversionService,
                           final VerificationKeyService keyService,
                           final UserService userService) {
        this.personService = personService;
        this.conversionService = conversionService;
        this.keyService = keyService;
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView findAll(){
        List<PersonDTO> persons = personService.findAllByOrderByName().stream()
                .map(p -> p.getClass().equals(Person.class) ? conversionService.convert(p, PersonDTO.class) : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new ModelAndView("guest/person_list", "persons", persons);
    }

    @PostMapping("/{id}")
    public ModelAndView edit(@PathVariable("id") Long id){
        PersonDTO personDTO = conversionService.convert(personService.findPersonById(id), PersonDTO.class);

        return show(personDTO);
    }

    @PostMapping("/add")
    public ModelAndView add(final Principal principal){
        final User user = userService.findUserByEmail(principal.getName());

        return show(new PersonDTO());
    }

    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable("id") Long id){
        personService.deletePerson(personService.findPersonById(id));

        return new ModelAndView("redirect:/guest/");
    }

    @PostMapping(value = "/update", params = "action=save")
    public ModelAndView save(@ModelAttribute("person") PersonDTO personDTO) {
        //TODO Fix this to save changed fields
        //TODO Refactor this spaghetti style logic
        if (personDTO.getId() == null || personService.findPersonById(personDTO.getId()) == null) {
            personDTO.setVerificationKey(keyService.saveOrUpdateKey(personDTO.getVerificationKey()));
        } else {
            PersonDTO newDTO = setNewKey(personDTO);
            if (newDTO == personDTO) {
                return show(personDTO);
            }
            personDTO = newDTO;
        }

        personService.saveOrUpdatePerson(conversionService.convert(personDTO,Person.class));

        return new ModelAndView("redirect:/guest/");
    }

    @PostMapping(value = "/update", params = "action=cancel")
    public ModelAndView cancel() {
        return new ModelAndView("redirect:/guest/");
    }

    private PersonDTO setNewKey(PersonDTO personDTO) {
        VerificationKey newKey = personDTO.getVerificationKey();
        if (newKey != null) {
            VerificationKey foundKey = keyService.findKeyByValue(newKey.getValue());
            if (foundKey != null && !foundKey.getPerson().getClass().equals(Person.class)) {
                //TODO make some magic with binding this user to a new person
                System.out.println("\n\nA New Profile for This User Found!!!\n\n");
            } else {
                personDTO.setVerificationKey(
                        personService.findPersonById(personDTO.getId()).getVerificationKey());
            }
        }

        return personDTO;
    }

    private ModelAndView show(PersonDTO personDTO){
        ModelAndView modelAndView = new ModelAndView("guest/person_profile");
        modelAndView.addObject("isNew",
                personService.findPersonById(personDTO.getId()) == null);
        modelAndView.addObject("person", personDTO);

        return modelAndView;
    }
}
