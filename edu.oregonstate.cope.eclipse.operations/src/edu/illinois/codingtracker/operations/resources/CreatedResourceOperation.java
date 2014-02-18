/**
 * This file is licensed under the University of Illinois/NCSA Open Source License. See LICENSE.TXT for details.
 */
package edu.illinois.codingtracker.operations.resources;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.json.simple.JSONObject;

import edu.illinois.codingtracker.helpers.ResourceHelper;
import edu.illinois.codingtracker.operations.OperationLexer;
import edu.illinois.codingtracker.operations.OperationSymbols;
import edu.illinois.codingtracker.operations.OperationTextChunk;
import edu.oregonstate.cope.clientRecorder.util.COPELogger;
import edu.oregonstate.cope.eclipse.COPEPlugin;

/**
 * 
 * @author Stas Negara
 * 
 */
public class CreatedResourceOperation extends UpdatedResourceOperation {

	private boolean isFile= false;

	private byte[] fileContent= new byte[0];

	public CreatedResourceOperation() {
		super();
	}

	public CreatedResourceOperation(IResource resource, int updateFlags, boolean success) {
		super(resource, updateFlags, success);
		if (resource instanceof IFile) {
			isFile= true;
			if (success) {
				fileContent= ResourceHelper.readFileContent((IFile)resource).getBytes();
			}
		}
	}

	@Override
	protected char getOperationSymbol() {
		return OperationSymbols.RESOURCE_CREATED_SYMBOL;
	}

	@Override
	public String getDescription() {
		return "Created resource";
	}

	@Override
	protected void populateTextChunk(OperationTextChunk textChunk) {
		super.populateTextChunk(textChunk);
		textChunk.append(isFile);
		textChunk.append(fileContent);
	}

	@Override
	protected void initializeFrom(OperationLexer operationLexer) {
		super.initializeFrom(operationLexer);
		isFile= operationLexer.readBoolean();
		fileContent= operationLexer.readString().getBytes();
	}
	
	@Override
	public void parse(JSONObject value) {
		super.parse(value);
		isFile= true;
		fileContent= ((String) value.get("text")).getBytes();
	}

	@Override
	public void replayBreakableResourceOperation() throws CoreException {
		if (isFile) {
			createCompilationUnit(new String(fileContent));
		} else {
			createContainer();
		}
	}

	@Override
	public String toString() {
		StringBuffer sb= new StringBuffer();
		sb.append(super.toString());
		
		sb.append("Is file: " + isFile + "\n");
		if (isFile) {
			sb.append("File content: " + fileContent + "\n");
		}
		return sb.toString();
	}

}
