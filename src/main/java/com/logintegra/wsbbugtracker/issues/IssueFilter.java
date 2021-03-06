package com.logintegra.wsbbugtracker.issues;

import com.logintegra.wsbbugtracker.enums.State;
import com.logintegra.wsbbugtracker.people.Person;
import com.logintegra.wsbbugtracker.projects.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@NoArgsConstructor
public class IssueFilter {

    State state;
    Project project;
    Person assignee;
    String title;

    String globalSearch;

    private Specification<Issue> hasState() {
        return (issueRoot, query, builder) -> builder.equal(issueRoot.get("state"), state);
    }

    private Specification<Issue> hasProject() {
        return (issueRoot, query, builder) -> builder.equal(issueRoot.get("project"), project);
    }

    private Specification<Issue> hasAssignee() {
        return (issueRoot, query, builder) -> builder.equal(issueRoot.get("assignee"), assignee);
    }

    private Specification<Issue> hasTitle() {
        return (issueRoot, query, builder) -> builder.like(builder.lower(issueRoot.get("title")), "%" + title.toLowerCase() + "%");
    }

    private Specification<Issue> globalSearching() {

        Specification<Issue> hasTitle = (issueRoot, query, builder) -> builder.like(builder.lower(issueRoot.get("title")), "%" + globalSearch.toLowerCase() + "%");
        Specification<Issue> hasContent = (issueRoot, query, builder) -> builder.like(builder.lower(issueRoot.get("content")), "%" + globalSearch.toLowerCase() + "%");

        return hasTitle.or(hasContent);
    }

    public Specification<Issue> buildQuery() {
        Specification<Issue> spec = Specification.where(null);

        if (project != null) {
            spec = spec.and(hasProject());
        }

        if (assignee != null) {
            spec = spec.and(hasAssignee());
        }

        if (state != null) {
            spec = spec.and(hasState());
        }

        if (title != null) {
            spec = spec.and(hasTitle());
        }

        if (globalSearch != null) {
            spec = spec.and(globalSearching());
        }

        return spec;
    }

    public String toQueryString(Integer page, Sort sort) {
        return "page=" + page +
                "&sort=" + toSortString(sort) +
                (state != null ? "&state=" + state : "") +
                (project != null ? "&project=" + project.getId() : "") +
                (assignee != null ? "&assignee=" + assignee.getId() : "") +
                (title != null ? "&title=" + title : "") +
                (globalSearch != null ? "&globalSearch=" + globalSearch : "");
    }

    public String toSortString(Sort sort) {
        Sort.Order order = sort.stream().findFirst().orElse(null);

        String sortString = "";
        if (order != null) {
            sortString += order.getProperty() + "," + order.getDirection();
        }

        return sortString;
    }

    public Sort findNextSorting(Sort currentSorting, String property) {
        Sort.Direction currentDirection = currentSorting.getOrderFor(property) != null ? currentSorting.getOrderFor(property).getDirection() : null;

        if (currentDirection == null) {
            return Sort.by(property).ascending();
        } else if (currentDirection.isAscending()) {
            return Sort.by(property).descending();
        } else {
            return Sort.unsorted();
        }
    }
}
