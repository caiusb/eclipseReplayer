/**
 * This file is licensed under the University of Illinois/NCSA Open Source License. See LICENSE.TXT for details.
 */
package edu.illinois.codingtracker.operations.starts;

import static edu.oregonstate.cope.clientRecorder.JSONConstants.JSON_LAUNCH_ATTRIBUTES;

import java.util.Map;

import org.json.simple.JSONObject;

import edu.illinois.codingtracker.operations.OperationLexer;
import edu.illinois.codingtracker.operations.OperationSymbols;
import edu.illinois.codingtracker.operations.OperationTextChunk;
import edu.illinois.codingtracker.operations.UserOperation;

/**
 * 
 * @author Caius Brindescu
 * 
 */
public class LaunchedApplicationOperation extends UserOperation {
	
	private Map launchAttributes;

	@Override
	protected char getOperationSymbol() {
		return OperationSymbols.APPLICATION_LAUNCHED_SYMBOL;
	}

	@Override
	public String getDescription() {
		return "Launched application";
	}

	@Override
	protected void populateTextChunk(OperationTextChunk textChunk) {
	}

	@Override
	protected void initializeFrom(OperationLexer operationLexer) {
	}
	
	@Override
	public void parse(JSONObject value) {
		launchAttributes = (Map) value.get(JSON_LAUNCH_ATTRIBUTES);
	}

	@Override
	public void replay() throws Exception {
		
	}

	@Override
	public String toString() {
		return "";
	}

}
