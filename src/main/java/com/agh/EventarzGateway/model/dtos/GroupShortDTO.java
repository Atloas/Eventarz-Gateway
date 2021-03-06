package com.agh.EventarzGateway.model.dtos;

import com.agh.EventarzGateway.model.groups.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupShortDTO {
    private String uuid;
    private String name;

    public GroupShortDTO(Group group) {
        this.uuid = group.getUuid();
        this.name = group.getName();
    }
}
