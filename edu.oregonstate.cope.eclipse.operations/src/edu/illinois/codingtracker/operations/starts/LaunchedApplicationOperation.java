/**
 * This file is licensed under the University of Illinois/NCSA Open Source License. See LICENSE.TXT for details.
 */
package edu.illinois.codingtracker.operations.starts;

import org.json.simple.JSONObject;

import edu.illinois.codingtracker.operations.OperationLexer;
import edu.illinois.codingtracker.operations.OperationSymbols;
import edu.illinois.codingtracker.operations.OperationTextChunk;
import edu.illinois.codingtracker.operations.UserOperation;

/**
 * 
 * @author Stas Negara
 * 
 */
public class LaunchedApplicationOperation extends UserOperation {

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
		//application= (String) value.get("entityAddress");
		//TODO MH
	}

	@Override
	public void replay() throws Exception {
		//do nothing
		//
	}

	@Override
	public String toString() {
		return "";
	}

}
