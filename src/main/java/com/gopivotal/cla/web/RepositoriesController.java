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

package com.gopivotal.cla.web;

import java.util.Set;
import java.util.SortedSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.gopivotal.cla.Agreement;
import com.gopivotal.cla.LinkedRepository;
import com.gopivotal.cla.github.GitHubClient;
import com.gopivotal.cla.github.Organization;
import com.gopivotal.cla.github.Repository;
import com.gopivotal.cla.repository.AgreementRepository;
import com.gopivotal.cla.repository.LinkedRepositoryRepository;
import com.gopivotal.cla.util.Sets;

@Controller
@RequestMapping("/repositories")
final class RepositoriesController extends AbstractController {

    private final GitHubClient gitHubClient;

    private final AgreementRepository agreementRepository;

    private final LinkedRepositoryRepository linkedRepositoryRepository;

    @Autowired
    RepositoriesController(GitHubClient gitHubClient, AgreementRepository agreementRepository, LinkedRepositoryRepository linkedRepositoryRepository) {
        super(gitHubClient);
        this.gitHubClient = gitHubClient;
        this.agreementRepository = agreementRepository;
        this.linkedRepositoryRepository = linkedRepositoryRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "")
    String listRepositories(ModelMap model) {
        SortedSet<LinkedRepository> linkedRepositories = this.linkedRepositoryRepository.find();
        SortedSet<Agreement> candidateAgreements = this.agreementRepository.find();
        SortedSet<Repository> candidateRepositories = candidateRepositories(getAdminRepositories(), linkedRepositories);

        model.put("linkedRepositories", linkedRepositories);
        model.put("candidateAgreements", candidateAgreements);
        model.put("candidateRepositories", candidateRepositories);

        return "repositories";
    }

    @RequestMapping(method = RequestMethod.POST, value = "")
    String createLinkedRepository(@RequestParam("repository") String name, @RequestParam("agreement") Long agreementId) {
        this.linkedRepositoryRepository.create(name, agreementId, this.gitHubClient.getAccessToken());

        return "redirect:/repositories";
    }

    private SortedSet<Repository> getAdminRepositories() {
        SortedSet<Repository> adminRepositories = Sets.asSortedSet();

        for (Organization organization : this.gitHubClient.getUser().getOrganizations()) {
            for (Repository repository : organization.getRepositories()) {
                if (repository.getPermissions().isAdmin()) {
                    adminRepositories.add(repository);
                }
            }
        }

        adminRepositories.addAll(this.gitHubClient.getUser().getRepositories());

        return adminRepositories;
    }

    private SortedSet<Repository> candidateRepositories(SortedSet<Repository> repositories, SortedSet<LinkedRepository> linkedRepositories) {
        Set<String> linkedRepositoryNames = Sets.asSet();
        for (LinkedRepository linkedRepository : linkedRepositories) {
            linkedRepositoryNames.add(linkedRepository.getName().toLowerCase());
        }

        SortedSet<Repository> candidateRepositories = Sets.asSortedSet();

        for (Repository repository : repositories) {
            if (!linkedRepositoryNames.contains(repository.getFullName().toLowerCase())) {
                candidateRepositories.add(repository);
            }
        }

        return candidateRepositories;
    }
}
