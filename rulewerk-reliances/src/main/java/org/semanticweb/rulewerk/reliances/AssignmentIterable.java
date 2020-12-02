package org.semanticweb.rulewerk.reliances;

/*-
 * #%L
 * Rulewerk Reliances
 * %%
 * Copyright (C) 2018 - 2020 Rulewerk Developers
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Iterator;

public class AssignmentIterable implements Iterable<Assignment> {

	AssignmentIterator assignmentIterator;

	private class AssignmentIterator implements Iterator<Assignment> {
		int originLength;
		int destinationLength;
		NumbersInBaseAndLengthFromMinusOne numbers; // base to count

		public AssignmentIterator(int originLength, int destinationLength) {
			this.originLength = originLength;
			this.destinationLength = destinationLength;
			numbers = new NumbersInBaseAndLengthFromMinusOne(destinationLength, originLength);
		}

		@Override
		public boolean hasNext() {
			return !numbers.stop;
		}

		/**
		 * Returns an Assignment of the positions in the second container to the
		 * positions in the first container. The position in the array ([i]) represents
		 * the location in the second container (what is being mapped). The value in the
		 * array at a given position (array[i]) represents the location in the first
		 * container (what is mapped to).
		 */
		@Override
		public Assignment next() {
			Assignment assignment = new Assignment(numbers.next(), originLength, destinationLength);
			while (!assignment.isValid()) {
				assignment = new Assignment(numbers.next(), originLength, destinationLength);
			}
			return assignment;
		}

	}

	/**
	 * Given two int's that represent the number of elements in an origin and
	 * destination lists, an Assignment is a List of Matches s.t each match maps
	 * indexes of the origin list into indexes of the destination list.
	 * 
	 * @param originLength      number of assigned objects
	 * @param destinationLength number of assignee objects
	 */
	public AssignmentIterable(int originLength, int destinationLength) {
		assignmentIterator = new AssignmentIterator(originLength, destinationLength);
	}

	@Override
	public Iterator<Assignment> iterator() {
		return assignmentIterator;
	}

}
