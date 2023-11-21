package ru.practicum.shareit.request.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(name = "description")
    String description;
    @ManyToOne
    @JoinColumn(name = "requestor_id", referencedColumnName = "id")
    User requestor;
    @Column(name = "created")
    LocalDateTime created;
}