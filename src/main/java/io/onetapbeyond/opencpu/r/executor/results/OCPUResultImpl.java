/*
 * Copyright 2015 David Russell
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.onetapbeyond.opencpu.r.executor.results;

import io.onetapbeyond.opencpu.r.executor.*;
import java.util.*;
import com.google.gson.*;

/*
 * OCPUResultImpl holds the result data of a
 * completed {@link OCPUResult}.
 */
public class OCPUResultImpl implements OCPUResult {

	private boolean success;
	private String input;
	private String[] output;
	private String error;
	private Exception cause;
	private long timeTaken;

	public OCPUResultImpl(boolean success,
						String input,
						String[] output,
						String error,
						Exception cause,
						long timeTaken) {
		this.success = success;
		this.input = input;
		this.output = output;
		this.error = error;
		this.cause = cause;
		this.timeTaken = timeTaken;
	}

	/*
	 * Determine if task execution was successful.
	 */
    public boolean success() {
    	return success;
    }

	/*
	 * Return data inputs passed on task execution.
	 */
	public Map input() {

		Map inputMap = null;

		/*
		 * Covert JSON input string to Map representation.
		 */
		try {
			inputMap = gson.fromJson(input, Map.class);
		} catch(Exception gex) {}

		return inputMap;
	}

	/*
	 * Return data outputs generated on task execution.
	 */
	public Map output() {

		/*
		 * Ensure result data exists and contains valid
		 * sets of key:value pairs.
		 */
		if(output == null || output.length % 2 != 0) {
			return null;
		}

		Map outputMap = new HashMap();

		for(int d=0; d < output.length; d = d + 2) {

			String functionOrObjectName = output[d];
			String jsonString = output[d+1];
			Object jsonData = null;

			/*
			 * Covert JSON data string to corresponding
			 * String, List or Map representation.
			 */
			try {
				jsonData = gson.fromJson(jsonString, List.class);
			} catch(Exception lex) {
				try {
					jsonData = gson.fromJson(jsonString, Map.class);
				} catch(Exception mex) {
					jsonData = jsonString;
				}
			}

			/*
			 * Build FluentTask result map.
			 */
			outputMap.put(functionOrObjectName, jsonData);
		}

		return outputMap;
	}

	/*
	 * Retrieve error message if task execution failed.
	 */
    public String error() {
    	return error;
    }

	/*
	 * Retrieve cause of error if task execution failed.
	 */
    public Exception cause() {
    	return cause;
    }

    /*
     * Returns approximate time taken (ms) by task execution.
     */
	public long	timeTaken() {
		return timeTaken;
	}

    public String toString() {
    	String outcome = success ? "successful" : "failed";
    	return "OCPUResult [ " + outcome + " ].";
    }

	private static Gson gson = new Gson();
}
