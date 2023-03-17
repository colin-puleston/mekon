package uk.ac.manchester.cs.hobo.user.app.basic.model;

import uk.ac.manchester.cs.hobo.modeller.*;

/**
 * @author Colin Puleston
 */
public class TextBlock extends TextEntity {

	static private final String PRESENT_TEXT_DISPLAY_STRING = "[TEXT...]";

	public TextBlock(DObjectBuilder builder) {

		super(builder);
	}

	String toAssertionDisplayString(String value) {

		return PRESENT_TEXT_DISPLAY_STRING;
	}
}