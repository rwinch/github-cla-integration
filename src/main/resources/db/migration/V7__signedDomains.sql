/*
 * Copyright 2013 the original author or authors.
 *
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
 */

CREATE TABLE signed_domains(
	id SERIAL PRIMARY KEY,
	domain VARCHAR(128) NOT NULL,
	agreement_id SERIAL NOT NULL,
	signatory_id SERIAL NOT NULL,

	FOREIGN KEY(agreement_id) REFERENCES agreements(id) ON DELETE CASCADE,
	FOREIGN KEY(signatory_id) REFERENCES signatories(id) ON DELETE CASCADE,
	CONSTRAINT signed_domains_tuple UNIQUE (domain, agreement_id)
);