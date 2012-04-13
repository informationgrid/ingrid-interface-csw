package de.ingrid.interfaces.csw.admin.validation;

import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

import de.ingrid.interfaces.csw.admin.command.HarvesterCommandObject;

@Service
public class HarvesterValidator extends AbstractValidator<HarvesterCommandObject> {

    public final Errors validate(final Errors errors) {
        rejectIfEmptyOrWhitespace(errors, "name");
        rejectIfEmptyOrWhitespace(errors, "type");

        return errors;
    }
}
