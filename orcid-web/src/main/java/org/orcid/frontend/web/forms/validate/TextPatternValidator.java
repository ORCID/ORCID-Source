package org.orcid.frontend.web.forms.validate;

import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;

import org.orcid.pojo.ajaxForm.Text;

/**
 * Get the content of a Text object and validates that it matches a given regex
 * @author Angel Montenegro
 * */
public class TextPatternValidator implements ConstraintValidator<TextPattern, Text> {
    private java.util.regex.Pattern pattern;

    @Override
    public void initialize(TextPattern parameters) {
            Pattern.Flag flags[] = parameters.flags();
            int intFlag = 0;
            for ( Pattern.Flag flag : flags ) {
                    intFlag = intFlag | flag.getValue();
            }

            try {
                    pattern = java.util.regex.Pattern.compile( parameters.regexp(), intFlag );
            }
            catch ( PatternSyntaxException e ) {
                    throw new IllegalArgumentException( "Invalid regular expression.", e );
            }
    }
    
    @Override
    public boolean isValid(Text text, ConstraintValidatorContext constraintValidatorContext) {
        try {            
            if ( text == null ) {
                return true;
            }
            Matcher m = pattern.matcher( text.getValue() );
            return m.matches();
         
        } catch (final Exception ignore) {
            // ignore
        }
        
        return false;
    }
}
