/*
 * Copyright 2016-2017 the original author or authors.
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

package org.springframework.samples.petclinic.repository.springdatajpa;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.samples.petclinic.model.Owner;

@Profile("spring-data-jpa")
public class SpringDataOwnerRepositoryImpl implements OwnerRepositoryOverride {

    @PersistenceContext
    private EntityManager em;

    @Override
    public Page<Owner> findOwnersSorted(String lastName, String sortField, boolean ascending, Pageable pageable) {
        String where = lastName == null ? "" : " WHERE owner.lastName LIKE :lastName";
        TypedQuery<Owner> query = this.em.createQuery(
            "SELECT owner FROM Owner owner" + where
                + " ORDER BY owner." + sortField + (ascending ? " ASC" : " DESC") + ", owner.id",
            Owner.class);
        TypedQuery<Long> countQuery = this.em.createQuery("SELECT COUNT(owner) FROM Owner owner" + where, Long.class);
        if (lastName != null) {
            query.setParameter("lastName", lastName + "%");
            countQuery.setParameter("lastName", lastName + "%");
        }
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Owner> owners = query.getResultList();
        return new PageImpl<>(owners, pageable, countQuery.getSingleResult());
    }
}
