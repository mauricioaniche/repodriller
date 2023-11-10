/**
 * Copyright 2014 Maurício Aniche

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.repodriller.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * A ChangeSet is metadata that uniquely identifies a Commit from an SCM.
 *
 * @author Mauricio Aniche
 */
@Data
@EqualsAndHashCode
@ToString
public class ChangeSet {

	private final Calendar date;
	private final String id;

	public ChangeSet(String id, Calendar date) {
		this.id = id;
		this.date = date;
	}


}
