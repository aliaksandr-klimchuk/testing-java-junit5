package alex.klimchuk.petclinic.controllers;

import alex.klimchuk.petclinic.fauxspring.BindingResult;
import alex.klimchuk.petclinic.fauxspring.Model;
import alex.klimchuk.petclinic.fauxspring.ModelAndView;
import alex.klimchuk.petclinic.fauxspring.WebDataBinder;
import alex.klimchuk.petclinic.model.Owner;
import alex.klimchuk.petclinic.services.OwnerService;

import javax.validation.Valid;
import java.util.List;

public class OwnerController {
    private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";

    private final OwnerService ownerService;

    public OwnerController(OwnerService ownerService) {
        this.ownerService = ownerService;
    }

    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

    public String findOwners(Model model) {
        model.addAttribute("owner", new Owner(null, null, null));
        return "owners/findOwners";
    }

    public String processFindForm(Owner owner, BindingResult result, Model model) {
        if (owner.getLastName() == null) {
            owner.setLastName("");
        }

        List<Owner> results = ownerService.findAllByLastNameLike("%" + owner.getLastName() + "%");

        if (results.isEmpty()) {
            result.rejectValue("lastName", "notFound", "not found");
            return "owners/findOwners";
        } else if (results.size() == 1) {
            owner = results.get(0);
            return "redirect:/owners/" + owner.getId();
        } else {
            model.addAttribute("selections", results);
            return "owners/ownersList";
        }
    }

    public ModelAndView showOwner(Long ownerId) {
        ModelAndView mav = new ModelAndView("owners/ownerDetails");
        mav.addObject(ownerService.findById(ownerId));
        return mav;
    }

    public String initCreationForm(Model model) {
        model.addAttribute("owner", new Owner(null, null, null));
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    public String processCreationForm(@Valid Owner owner, BindingResult result) {
        if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            Owner savedOwner = ownerService.save(owner);
            return "redirect:/owners/" + savedOwner.getId();
        }
    }

    public String initUpdateOwnerForm(Long ownerId, Model model) {
        model.addAttribute(ownerService.findById(ownerId));
        return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
    }

    public String processUpdateOwnerForm(@Valid Owner owner, BindingResult result, Long ownerId) {
        if (result.hasErrors()) {
            return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
        } else {
            owner.setId(ownerId);
            Owner savedOwner = ownerService.save(owner);
            return "redirect:/owners/" + savedOwner.getId();
        }
    }
}
