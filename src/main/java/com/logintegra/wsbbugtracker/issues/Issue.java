package com.logintegra.wsbbugtracker.issues;

import com.logintegra.wsbbugtracker.enums.State;
import com.logintegra.wsbbugtracker.people.Person;
import com.logintegra.wsbbugtracker.projects.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
public class Issue {

    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    String title;

    @Column(columnDefinition = "TEXT")
    String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    State state = State.TODO;

    @ManyToOne()
    @JoinColumn(name = "assignee_id")
    Person assignee;

    @ManyToOne(optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    Project project;


}
